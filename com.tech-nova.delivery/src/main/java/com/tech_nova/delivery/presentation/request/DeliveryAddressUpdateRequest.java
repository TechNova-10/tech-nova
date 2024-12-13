package com.tech_nova.delivery.presentation.request;

import com.tech_nova.delivery.application.dto.DeliveryAddressUpdateDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class DeliveryAddressUpdateRequest {
    private UUID recipientCompanyId;
    private String province;
    private String city;
    private String district;
    private String roadName;
    private String detailAddress;

    public DeliveryAddressUpdateDto toDTO() {
        return DeliveryAddressUpdateDto.create(
                this.recipientCompanyId,
                this.province,
                this.city,
                this.district,
                this.roadName,
                this.detailAddress
        );
    }
}
