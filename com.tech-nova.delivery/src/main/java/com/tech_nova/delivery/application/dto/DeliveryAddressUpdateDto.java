package com.tech_nova.delivery.application.dto;

import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class DeliveryAddressUpdateDto {
    private UUID recipientCompanyId;
    private String province;
    private String city;
    private String district;
    private String roadName;
    private String detailAddress;

    public static DeliveryAddressUpdateDto create(
            UUID recipientCompanyId,
            String province,
            String city,
            String district,
            String roadName,
            String detailAddress
    ) {
        return DeliveryAddressUpdateDto.builder()
                .recipientCompanyId(recipientCompanyId)
                .province(province)
                .city(city)
                .district(district)
                .roadName(roadName)
                .detailAddress(detailAddress)
                .build();
    }
}
