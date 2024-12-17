package com.tech_nova.delivery.application.dto.res;

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
    private String province;
    private String city;
    private String district;
    private String roadName;
    private String detailAddress;
    private DeliveryCompanyStatus currentStatus;
    private Integer deliveryOrderSequence;
    private Double expectedDistance;
    private Double exceptedTime;
    private Double realDistance;
    private Double realTime;

    public static DeliveryCompanyRouteRecordResponse of(DeliveryCompanyRouteRecord routeRecord) {
        return DeliveryCompanyRouteRecordResponse.builder()
                .id(routeRecord.getId())
                .deliveryId(routeRecord.getDelivery().getId())
                .deliveryManager(DeliveryManagerResponse.of(routeRecord.getDeliveryManager()))
                .departureHubId(routeRecord.getDepartureHubId())
                .recipientCompanyId(routeRecord.getRecipientCompanyId())
                .province(routeRecord.getProvince())
                .district(routeRecord.getDistrict())
                .city(routeRecord.getCity())
                .roadName(routeRecord.getRoadName())
                .detailAddress(routeRecord.getDetailAddress())
                .currentStatus(routeRecord.getCurrentStatus())
                .expectedDistance(routeRecord.getExpectedDistance())
                .realDistance(routeRecord.getRealDistance())
                .realTime(routeRecord.getRealTime())
                .build();
    }
}
