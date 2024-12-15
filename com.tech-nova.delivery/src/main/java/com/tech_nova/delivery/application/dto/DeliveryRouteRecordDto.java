package com.tech_nova.delivery.application.dto;

import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class DeliveryRouteRecordDto {
    private UUID deliveryManagerId;
    private UUID departureHubId;
    private UUID arrivalHubId;
    private Integer sequence;
    private String currentStatus;
    private Double expectedDistance;
    private Double expectedTime;

    public static DeliveryRouteRecordDto create(
            UUID deliveryManagerId,
            UUID departureHubId,
            UUID arrivalHubId,
            Integer sequence,
            String currentStatus,
            Double expectedDistance,
            Double expectedTime
    ) {
        return DeliveryRouteRecordDto.builder()
                .deliveryManagerId(deliveryManagerId)
                .departureHubId(departureHubId)
                .arrivalHubId(arrivalHubId)
                .sequence(sequence)
                .currentStatus(currentStatus)
                .expectedDistance(expectedDistance)
                .expectedTime(expectedTime)
                .build();
    }
}
