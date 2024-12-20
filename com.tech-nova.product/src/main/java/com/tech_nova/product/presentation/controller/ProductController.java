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
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponseDto<Void>> createProduct(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody ProductRequest request) {
        String token = extractToken(authorizationHeader);
        productService.createProduct(request, token);
        return ResponseEntity.status(201).body(ApiResponseDto.success("상품 생성 완료", null));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponseDto<ProductResponse>> updateProduct(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable UUID productId, @RequestBody ProductRequest request) {
        String token = extractToken(authorizationHeader);
        ProductResponse response = productService.updateProduct(productId, request, token);
        return ResponseEntity.ok(ApiResponseDto.success("상품 수정 완료", response));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponseDto<Void>> deleteProduct(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable UUID productId) {
        String token = extractToken(authorizationHeader);
        productService.deleteProduct(productId, token);
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

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        throw new IllegalArgumentException("유효하지 않은 Authorization header");
    }
}
