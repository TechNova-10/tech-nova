package com.tech_nova.product.presentation.controller;

import com.tech_nova.product.application.dto.ProductRequest;
import com.tech_nova.product.application.dto.ProductResponse;
import com.tech_nova.product.application.service.ProductService;
import com.tech_nova.product.presentation.dto.ApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Void> createProduct(@RequestBody ProductRequest request) {
        productService.createProduct(request);
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponseDto<ProductResponse>> updateProduct(
            @PathVariable UUID productId, @RequestBody ProductRequest request) {
        ProductResponse response = productService.updateProduct(productId, request);
        return ResponseEntity.ok(ApiResponseDto.success("상품 수정 완료", response));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponseDto<Void>> deleteProduct(@PathVariable UUID productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok(ApiResponseDto.successDelete());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponseDto<ProductResponse>> getProduct(@PathVariable UUID productId) {
        ProductResponse response = productService.getProductById(productId);
        return ResponseEntity.ok(ApiResponseDto.success("상품 조회 완료", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<PagedModel<ProductResponse>>> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) UUID companyId,
            Pageable pageable) {
        PagedModel<ProductResponse> products = productService.searchProducts(name, companyId, pageable);
        return ResponseEntity.ok(ApiResponseDto.success("상품 검색 완료", products));
    }
}
