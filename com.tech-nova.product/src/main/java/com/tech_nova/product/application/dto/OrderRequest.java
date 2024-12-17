package com.tech_nova.product.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequest {
    private UUID requestingCompanyId;
    private UUID receivingCompanyId;
    private String requestDetails;
    private LocalDate deliveryDeadLine;
    private List<OrderProductRequest> orderProducts;

    private String province;
    private String city;
    private String district;
    private String roadName;
    private String detailAddress;
}