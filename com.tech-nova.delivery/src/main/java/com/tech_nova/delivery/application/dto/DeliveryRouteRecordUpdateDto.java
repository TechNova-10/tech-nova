package com.tech_nova.delivery.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRouteRecordUpdateDto {
    private UUID deliveryManagerId;
    private String currentStatus;
    private Double realDistance;
    private String realTime;

    public static DeliveryRouteRecordUpdateDto create(
            UUID deliveryManagerId,
            String currentStatus,
            Double realDistance,
            String realTime
    ) {
        return new DeliveryRouteRecordUpdateDto(
                deliveryManagerId,
                currentStatus,
                realDistance,
                realTime
        );
    }
}