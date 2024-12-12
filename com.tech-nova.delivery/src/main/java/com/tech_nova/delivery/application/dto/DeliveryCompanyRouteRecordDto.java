package com.tech_nova.delivery.application.dto;

import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class DeliveryCompanyRouteRecordDto {
    private UUID deliveryManagerId;
    private UUID departureHubId;
    private UUID recipientCompanyId;
    private String currentStatus;
    private Integer deliveryOrderSequence;
    private Double expectedDistance;
    private String exceptedTime;

    public static DeliveryCompanyRouteRecordDto create(
            UUID deliveryManagerId,
            UUID departureHubId,
            UUID arrivalHubId,
            String currentStatus,
            Integer deliveryOrderSequence,
            Double expectedDistance,
            String exceptedTime
    ) {
        return DeliveryCompanyRouteRecordDto.builder()
                .deliveryManagerId(deliveryManagerId)
                .departureHubId(departureHubId)
                .recipientCompanyId(arrivalHubId)
                .currentStatus(currentStatus)
                .deliveryOrderSequence(deliveryOrderSequence)
                .expectedDistance(expectedDistance)
                .exceptedTime(exceptedTime)
                .build();
    }
}
