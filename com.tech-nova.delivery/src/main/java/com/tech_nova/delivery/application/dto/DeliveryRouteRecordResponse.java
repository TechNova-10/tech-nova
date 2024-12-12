package com.tech_nova.delivery.application.dto;

import com.tech_nova.delivery.domain.model.delivery.DeliveryHubStatus;
import com.tech_nova.delivery.domain.model.delivery.DeliveryRouteRecord;
import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class DeliveryRouteRecordResponse {
    private UUID id;
    private UUID deliveryId;
    private DeliveryManagerResponse deliveryManager;
    private UUID departureHubId;
    private UUID arrivalHubId;
    private Integer sequence;
    private DeliveryHubStatus currentStatus;
    private Double expectedDistance;
    private String exceptedTime;
    private Double realDistance;
    private String realTime;

    public static DeliveryRouteRecordResponse of(DeliveryRouteRecord routeRecord) {
        return DeliveryRouteRecordResponse.builder()
                .id(routeRecord.getId())
                .deliveryId(routeRecord.getDelivery().getId())
                .deliveryManager(DeliveryManagerResponse.of(routeRecord.getDeliveryManager()))
                .departureHubId(routeRecord.getDepartureHubId())
                .arrivalHubId(routeRecord.getArrivalHubId())
                .currentStatus(routeRecord.getCurrentStatus())
                .sequence(routeRecord.getSequence())
                .expectedDistance(routeRecord.getExpectedDistance())
                .exceptedTime(routeRecord.getExpectedTime())
                .realDistance(routeRecord.getRealDistance())
                .realTime(routeRecord.getRealTime())
                .build();
    }
}
