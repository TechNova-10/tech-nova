package com.tech_nova.product.application.service;

import com.tech_nova.product.application.dto.ProductRequest;
import com.tech_nova.product.application.dto.ProductResponse;
import com.tech_nova.product.domain.model.Product;
import com.tech_nova.product.domain.repository.ProductRepository;
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

    @Transactional
    public void createProduct(ProductRequest request) {
        if (!companyServiceClient.isCompanyIdValid(request.getCompanyId().toString())) {
            throw new IllegalArgumentException("Invalid companyId: " + request.getCompanyId());
        }

        if (!companyServiceClient.isHubIdValid(request.getHubId().toString())) {
            throw new IllegalArgumentException("Invalid hubId: " + request.getHubId());
        }

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
    public ProductResponse updateProduct(UUID productId, ProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품 존재 X"));

        product.update(request.getName(), request.getDescription(), request.getPrice(), request.getStock());
        productRepository.save(product);

        return ProductResponse.from(product);
    }

    @Transactional
    public void deleteProduct(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품 존재 X"));

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
}
