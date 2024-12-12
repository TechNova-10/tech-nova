package com.tech_nova.product.application.dto;

import lombok.*;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {
    private UUID companyId;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
}
