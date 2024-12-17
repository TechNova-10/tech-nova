package com.tech_nova.delivery.domain.model.delivery;

import com.tech_nova.delivery.domain.model.Timestamped;
import com.tech_nova.delivery.domain.model.manager.DeliveryManager;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    private Double expectedTime;

    @Column
    private Double realDistance;

    @Column
    private Double realTime;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isDeleted;

    public static DeliveryRouteRecord create(
            DeliveryManager deliveryManager,
            UUID departureHubId,
            UUID arrivalHubId,
            Integer sequence,
            DeliveryHubStatus currentStatus,
            Double expectedDistance,
            Double expectedTime
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
            DeliveryHubStatus currentStatus,
            Double realDistance,
            Double realTime) {

        if (deliveryManager != null) {
            this.deliveryManager = deliveryManager;
        }
        if (currentStatus != null) {
            this.currentStatus = currentStatus;
        }

        if (realDistance != null) {
            this.realDistance = realDistance;
        }
        if (realTime != null) {
            this.realTime = realTime;
        }
    }

    public void connectDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public void updateCurrentStatus(DeliveryHubStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    public void updateRealDistanceAndRealTime() {
        LocalDateTime updateAt = this.getUpdateAt();
        LocalDateTime currentTime = LocalDateTime.now();

        long differenceInMillis = ChronoUnit.MILLIS.between(updateAt, currentTime);

        this.realDistance = this.expectedDistance;
        this.realTime = (double) differenceInMillis;
    }

    public void updateDeliveryManager(DeliveryManager deliveryManager) {
        this.deliveryManager = deliveryManager;
    }

    public void markAsDeleted(UUID deletedBy) {
        super.markAsDeleted(deletedBy);
        this.isDeleted = true;
    }
}
