package com.tech_nova.product.application.service;

import com.tech_nova.product.application.dto.ProductRequest;
import com.tech_nova.product.application.dto.ProductResponse;
import com.tech_nova.product.domain.model.Product;
import com.tech_nova.product.domain.repository.ProductRepository;
import com.tech_nova.product.infrastructure.client.AuthServiceClient;
import com.tech_nova.product.infrastructure.client.CompanyServiceClient;
import com.tech_nova.product.presentation.controller.ProductController;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CompanyServiceClient companyServiceClient;
    private final AuthServiceClient authServiceClient;

    @Transactional
    public void createProduct(String token, ProductRequest request) {
        validateIds(request.getHubId(), request.getCompanyId());
        validateUserRole(token, request.getHubId(), request.getCompanyId());

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .companyId(request.getCompanyId())
                .hubId(request.getHubId())
                .build();

        productRepository.save(product);
    }

    @Transactional
    public ProductResponse updateProduct(String token, UUID productId, ProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품 존재 X"));

        validateIds(request.getHubId(), request.getCompanyId());
        validateUserRole(token, request.getHubId(), request.getCompanyId());

        product.update(request.getName(), request.getDescription(), request.getPrice(), request.getStock());
        productRepository.save(product);

        return ProductResponse.from(product);
    }

    @Transactional
    public void deleteProduct(String token, UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품 존재 X"));

        validateUserRole(token, product.getHubId(), product.getCompanyId());

        product.softDelete();
        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID productId) {
        Product product = productRepository.findById(productId)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("상품 존재 X"));

        return ProductResponse.from(product);
    }

    @Transactional(readOnly = true)
    public PagedModel<ProductResponse> searchProducts(String name, UUID companyId, Pageable pageable) {
        Page<Product> productPage = productRepository.searchByNameAndCompanyId(name, companyId, pageable);

        return PagedModel.of(
                productPage.map(ProductResponse::from).getContent(),
                new PagedModel.PageMetadata(productPage.getSize(), productPage.getNumber(), productPage.getTotalElements()),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProductController.class).searchProducts(name, companyId, pageable)).withSelfRel()
        );
    }

    private void validateIds(UUID hubId, UUID companyId) {
        if (!companyServiceClient.isCompanyIdValid(companyId.toString())) {
            throw new IllegalArgumentException("유효하지 않은 업체 Id!");
        }
        if (!companyServiceClient.isHubIdValid(hubId.toString())) {
            throw new IllegalArgumentException("유효하지 않은 허브 Id!");
        }
    }

    private void validateUserRole(String token, UUID hubId, UUID companyId) {
        String userRole = authServiceClient.getUserRole(token);

        if ("MASTER".equals(userRole)) {
            return;
        }

        String userHubId = authServiceClient.getUserHubId(token);

        if ("HUB".equals(userRole) && !hubId.toString().equals(userHubId)) {
            throw new IllegalArgumentException("해당 허브의 제품을 관리할 권한이 없습니다.");
        }

        if ("COMPANY".equals(userRole) && !companyServiceClient.isCompanyIdValid(companyId.toString())) {
            throw new IllegalArgumentException("해당 회사의 제품을 관리할 수 있는 권한이 없습니다.");
        }
    }
}
