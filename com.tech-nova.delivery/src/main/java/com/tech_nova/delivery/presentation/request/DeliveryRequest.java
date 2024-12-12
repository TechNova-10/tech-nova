package com.tech_nova.delivery.presentation.request;

import com.tech_nova.delivery.application.dto.DeliveryDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class DeliveryRequest {
    private UUID orderId;
    private UUID recipientCompanyId;
    private String province;
    private String city;
    private String district;
    private String roadName;

    public DeliveryDto toDTO() {
        return DeliveryDto.create(
                this.orderId,
                this.recipientCompanyId,
                this.province,
                this.city,
                this.district,
                this.roadName
        );
    }

}
