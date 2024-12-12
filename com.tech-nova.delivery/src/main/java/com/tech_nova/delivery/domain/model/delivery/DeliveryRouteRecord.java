package com.tech_nova.delivery.domain.model.delivery;

import com.tech_nova.delivery.domain.model.Timestamped;
import com.tech_nova.delivery.domain.model.manager.DeliveryManager;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "p_delivery_route_record")
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRouteRecord extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "delivery_route_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_manager_id")
    private DeliveryManager deliveryManager;

    @Column(nullable = false)
    private UUID departureHubId;

    @Column(nullable = false)
    private UUID arrivalHubId;

    @Column(nullable = false)
    private Integer sequence;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryHubStatus currentStatus;

    @Column
    private Double expectedDistance;

    @Column
    private String expectedTime;

    @Column
    private Double realDistance;

    @Column
    private String realTime;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isDeleted;

    public static DeliveryRouteRecord create(
            DeliveryManager deliveryManager,
            UUID departureHubId,
            UUID arrivalHubId,
            Integer sequence,
            DeliveryHubStatus currentStatus,
            Double expectedDistance,
            String expectedTime
    ) {
        return DeliveryRouteRecord.builder()
                .deliveryManager(deliveryManager)
                .departureHubId(departureHubId)
                .arrivalHubId(arrivalHubId)
                .sequence(sequence)
                .currentStatus(currentStatus)
                .expectedDistance(expectedDistance)
                .expectedTime(expectedTime)
                .build();
    }

    public void update(
            DeliveryManager deliveryManager,
            UUID departureHubId,
            UUID arrivalHubId,
            DeliveryHubStatus currentStatus,
            Double expectedDistance,
            String expectedTime,
            Double realDistance,
            String realTime) {
        this.deliveryManager = deliveryManager;
        this.departureHubId = departureHubId;
        this.arrivalHubId = arrivalHubId;
        this.currentStatus = currentStatus;
        this.expectedDistance = expectedDistance;
        this.expectedTime = expectedTime;
        this.realDistance = realDistance;
        this.realTime = realTime;
    }

    public void connectDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public void updateCurrentStatus(DeliveryHubStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    public void updateDeliveryManager(DeliveryManager deliveryManager) {
        this.deliveryManager = deliveryManager;
    }

    public void markAsDeleted(UUID deletedBy) {
        super.markAsDeleted(deletedBy);
        this.isDeleted = true;
    }
}
