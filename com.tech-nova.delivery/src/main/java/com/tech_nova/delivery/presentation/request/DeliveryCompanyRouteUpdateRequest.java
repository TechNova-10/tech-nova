package com.tech_nova.delivery.presentation.request;

import com.tech_nova.delivery.application.dto.DeliveryCompanyRouteRecordUpdateDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class DeliveryCompanyRouteUpdateRequest {
    private UUID deliveryManagerId;
    private String currentStatus;
    private Integer deliveryOrderSequence;
    private Double realDistance;
    private Double realTime;

    public DeliveryCompanyRouteRecordUpdateDto toDTO() {
        return DeliveryCompanyRouteRecordUpdateDto.create(
                this.deliveryManagerId,
                this.currentStatus,
                this.deliveryOrderSequence,
                this.realDistance,
                this.realTime
        );
    }
}

