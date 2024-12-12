package com.tech_nova.delivery.application.dto.res;

import com.tech_nova.delivery.domain.model.delivery.Delivery;
import com.tech_nova.delivery.domain.model.delivery.DeliveryStatus;
import lombok.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class DeliveryResponse {
    private UUID id;
    private UUID orderId;
    private UUID departureHubId;
    private UUID arrivalHubId;
    private UUID recipientCompanyId;
    private DeliveryStatus currentStatus;
    private String province;
    private String city;
    private String district;
    private String roadName;
    private String recipient;
    private List<DeliveryRouteRecordResponse> hubRouteRecords;
    private List<DeliveryCompanyRouteRecordResponse> companyRouteRecords;

    public static DeliveryResponse of(Delivery delivery) {
        return DeliveryResponse.builder()
                .id(delivery.getId())
                .orderId(delivery.getOrderId())
                .departureHubId(delivery.getDepartureHubId())
                .arrivalHubId(delivery.getArrivalHubId())
                .recipientCompanyId(delivery.getRecipientCompanyId())
                .currentStatus(delivery.getCurrentStatus())
                .province(delivery.getProvince())
                .city(delivery.getCity())
                .district(delivery.getDistrict())
                .roadName(delivery.getRoadName())
                .recipient(delivery.getRecipient())
                .hubRouteRecords(delivery.getRouteRecords().stream().map(DeliveryRouteRecordResponse::of).collect(Collectors.toList()))
                .companyRouteRecords(delivery.getCompanyRouteRecords().stream().map(DeliveryCompanyRouteRecordResponse::of).collect(Collectors.toList()))
                .build();
    }
}
