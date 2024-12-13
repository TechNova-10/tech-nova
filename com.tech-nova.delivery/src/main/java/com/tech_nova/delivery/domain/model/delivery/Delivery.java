package com.tech_nova.delivery.domain.model.delivery;

import com.tech_nova.delivery.domain.model.Timestamped;
import com.tech_nova.delivery.domain.model.manager.DeliveryManager;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_delivery")
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class Delivery extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "delivery_id")
    private UUID id;

    @Column(nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private UUID departureHubId;

    @Column(nullable = false)
    private UUID arrivalHubId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus currentStatus;

    @Column(nullable = false)
    private UUID recipientCompanyId;

    @Column(nullable = false)
    private String province;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String district;

    @Column(nullable = false)
    private String roadName;

    @Column
    private String detailAddress;

    @Column
    private String recipient;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isDeleted;

    @Builder.Default
    @OneToMany(mappedBy = "delivery", cascade = CascadeType.PERSIST)
    private List<DeliveryRouteRecord> routeRecords = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "delivery", cascade = CascadeType.PERSIST)
    private List<DeliveryCompanyRouteRecord> companyRouteRecords = new ArrayList<>();

    public static Delivery create(
            UUID orderId,
            UUID departureHubId,
            UUID arrivalHubId,
            DeliveryStatus currentStatus,
            UUID recipientCompanyId,
            String province,
            String city,
            String district,
            String roadName,
            String detailAddress,
            List<DeliveryRouteRecord> routeRecords
    ) {
        return Delivery.builder()
                .orderId(orderId)
                .departureHubId(departureHubId)
                .arrivalHubId(arrivalHubId)
                .currentStatus(currentStatus)
                .recipientCompanyId(recipientCompanyId)
                .province(province)
                .city(city)
                .district(district)
                .roadName(roadName)
                .detailAddress(detailAddress)
                .routeRecords(routeRecords)
                .build();
    }

    public void updateRecipient(String recipient, UUID updatedBy) {
        this.recipient = recipient;
        markAsUpdated(updatedBy);
    }

    public void updateRouteRecord(
            UUID deliveryRouteId,
            DeliveryManager deliveryManager,
            DeliveryHubStatus currentStatus,
            Double realDistance,
            String realTime,
            UUID updatedBy) {
        DeliveryRouteRecord routeRecord = routeRecords.stream()
                .filter(record -> record.getId().equals(deliveryRouteId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 경로를 찾을 수 없습니다."));

        routeRecord.update(deliveryManager, currentStatus, realDistance, realTime);
        routeRecord.markAsUpdated(updatedBy);
    }

    public void updateRouteRecordState(UUID deliveryRouteId, DeliveryHubStatus currentStatus, UUID updatedBy) {
        DeliveryRouteRecord routeRecord = routeRecords.stream()
                .filter(record -> record.getId().equals(deliveryRouteId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 경로를 찾을 수 없습니다."));

        routeRecord.updateCurrentStatus(currentStatus);
        routeRecord.markAsUpdated(updatedBy);
        this.currentStatus = DeliveryStatus.valueOf(currentStatus.name());
    }

    public void updateCompanyRouteRecord(
            UUID deliveryRouteId,
            DeliveryManager deliveryManager,
            DeliveryCompanyStatus currentStatus,
            Integer deliveryOrderSequence,
            Double realDistance,
            String realTime,
            UUID updatedBy) {
        DeliveryCompanyRouteRecord routeRecord = companyRouteRecords.stream()
                .filter(record -> record.getId().equals(deliveryRouteId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 경로를 찾을 수 없습니다."));

        routeRecord.update(deliveryManager, currentStatus, deliveryOrderSequence, realDistance, realTime);
        routeRecord.markAsUpdated(updatedBy);
    }

    public void updateCompanyRouteRecordState(UUID deliveryRouteId, DeliveryCompanyStatus currentStatus, UUID updatedBy) {
        DeliveryCompanyRouteRecord routeRecord = companyRouteRecords.stream()
                .filter(record -> record.getId().equals(deliveryRouteId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 경로를 찾을 수 없습니다."));

        routeRecord.updateCurrentStatus(currentStatus);
        routeRecord.markAsUpdated(updatedBy);
        this.currentStatus = DeliveryStatus.valueOf(currentStatus.name());
    }

    public void deleteRouteRecordState(UUID deliveryRouteId, UUID deletedBy) {
        DeliveryRouteRecord routeRecord = routeRecords.stream()
                .filter(record -> record.getId().equals(deliveryRouteId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 경로를 찾을 수 없습니다."));

        // 추후 인증 구현되면 사용자 Id로 변경
        routeRecord.markAsDeleted(deletedBy);
    }

    public void deleteCompanyRouteRecordState(UUID deliveryRouteId, UUID deletedBy) {
        DeliveryCompanyRouteRecord routeRecord = companyRouteRecords.stream()
                .filter(record -> record.getId().equals(deliveryRouteId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 경로를 찾을 수 없습니다."));

        // 추후 인증 구현되면 사용자 Id로 변경
        routeRecord.markAsDeleted(deletedBy);
    }

    public void addRouteRecord(DeliveryRouteRecord routeRecord) {
        this.routeRecords.add(routeRecord);
    }

    public void addCompanyRouteRecord(DeliveryCompanyRouteRecord companyRouteRecord) {
        this.companyRouteRecords.add(companyRouteRecord);
    }

    public void markAsDeleted(UUID deletedBy) {
        super.markAsDeleted(deletedBy);
        this.isDeleted = true;
    }

    public void markAsUpdated(UUID updatedBy) {
        super.markAsUpdated(updatedBy);
    }
}
