package com.tech_nova.delivery.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryCompanyRouteRecordUpdateDto {
    private UUID deliveryManagerId;
    private String currentStatus;
    private Integer deliveryOrderSequence;
    private Double realDistance;
    private String realTime;

    public static DeliveryCompanyRouteRecordUpdateDto create(
            UUID deliveryManagerId,
            String currentStatus,
            Integer deliveryOrderSequence,
            Double realDistance,
            String realTime
    ) {
        return new DeliveryCompanyRouteRecordUpdateDto(
                deliveryManagerId,
                currentStatus,
                deliveryOrderSequence,
                realDistance,
                realTime
        );
    }
}