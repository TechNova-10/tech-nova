package com.tech_nova.delivery.presentation.request;

import com.tech_nova.delivery.application.dto.DeliveryRouteRecordUpdateDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class DeliveryRouteUpdateRequest {
    private UUID deliveryManagerId;
    private String currentStatus;
    private Double realDistance;
    private Double realTime;

    public DeliveryRouteRecordUpdateDto toDTO() {
        return DeliveryRouteRecordUpdateDto.create(
                this.deliveryManagerId,
                this.currentStatus,
                this.realDistance,
                this.realTime
        );
    }
}

