package com.tech_nova.delivery.application.dto;

import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class DeliveryDto {
    private UUID orderId;
    private UUID recipientCompanyId;
    private String province;
    private String city;
    private String district;
    private String roadName;

    public static DeliveryDto create(
            UUID orderId,
            UUID recipientCompanyId,
            String province,
            String city,
            String district,
            String roadName
    ) {
        return DeliveryDto.builder()
                .orderId(orderId)
                .recipientCompanyId(recipientCompanyId)
                .province(province)
                .city(city)
                .district(district)
                .roadName(roadName)
                .build();
    }
}
