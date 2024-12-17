package com.tech_nova.product.infrastructure.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRequest {
    private UUID orderId;
    private UUID recipientCompanyId;
    private String province;
    private String city;
    private String district;
    private String roadName;
    private String detailAddress;
}
