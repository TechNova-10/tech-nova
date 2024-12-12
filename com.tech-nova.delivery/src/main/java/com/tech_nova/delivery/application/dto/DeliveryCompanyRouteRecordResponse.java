package com.tech_nova.delivery.application.dto;

import com.tech_nova.delivery.domain.model.delivery.DeliveryCompanyRouteRecord;
import com.tech_nova.delivery.domain.model.delivery.DeliveryCompanyStatus;
import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class DeliveryCompanyRouteRecordResponse {
    private UUID id;
    private UUID deliveryId;
    private DeliveryManagerResponse deliveryManager;
    private UUID departureHubId;
    private UUID recipientCompanyId;
    private DeliveryCompanyStatus currentStatus;
    private Integer deliveryOrderSequence;
    private Double expectedDistance;
    private String exceptedTime;
    private Double realDistance;
    private String realTime;

    public static DeliveryCompanyRouteRecordResponse of(DeliveryCompanyRouteRecord routeRecord) {
        return DeliveryCompanyRouteRecordResponse.builder()
                .id(routeRecord.getId())
                .deliveryId(routeRecord.getDelivery().getId())
                .deliveryManager(DeliveryManagerResponse.of(routeRecord.getDeliveryManager()))
                .departureHubId(routeRecord.getDepartureHubId())
                .recipientCompanyId(routeRecord.getRecipientCompanyId())
                .currentStatus(routeRecord.getCurrentStatus())
                .expectedDistance(routeRecord.getExpectedDistance())
                .realDistance(routeRecord.getRealDistance())
                .realTime(routeRecord.getRealTime())
                .build();
    }
}
