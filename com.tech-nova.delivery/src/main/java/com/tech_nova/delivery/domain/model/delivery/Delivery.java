package com.tech_nova.delivery.domain.model.delivery;

import com.tech_nova.delivery.domain.model.Timestamped;
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

    @Column(name = "province", nullable = false)
    private String province;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "district", nullable = false)
    private String district;

    @Column(name = "roadName", nullable = false)
    private String roadName;

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
            String province,
            String city,
            String district,
            String roadName,
            List<DeliveryRouteRecord> routeRecords
    ) {
        return Delivery.builder()
                .orderId(orderId)
                .departureHubId(departureHubId)
                .arrivalHubId(arrivalHubId)
                .currentStatus(currentStatus)
                .province(province)
                .city(city)
                .district(district)
                .roadName(roadName)
                .routeRecords(routeRecords)
                .build();
    }

    public void update(
            UUID departureHubId,
            UUID arrivalHubId,
            DeliveryStatus currentStatus,
            String province,
            String city,
            String district,
            String roadName,
            String recipient) {
        this.departureHubId = departureHubId;
        this.arrivalHubId = arrivalHubId;
        this.currentStatus = currentStatus;
        this.province = province;
        this.city = city;
        this.district = district;
        this.roadName = roadName;
        this.recipient = recipient;
    }

    public void updateCurrentStatus(DeliveryStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    public void addRouteRecord(DeliveryRouteRecord routeRecord) {
        this.routeRecords.add(routeRecord);
    }

    public void markAsDeleted(UUID deletedBy) {
        super.markAsDeleted(deletedBy);
        this.isDeleted = true;
    }
}
