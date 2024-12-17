package com.tech_nova.product.application.dto;

import com.tech_nova.product.domain.model.Product;
import lombok.*;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {
    private UUID productId;
    private UUID companyId;
    private String name;
    private String description;
    private Double price;
    private Integer stock;

    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .companyId(product.getCompanyId())
                .build();
    }
}
