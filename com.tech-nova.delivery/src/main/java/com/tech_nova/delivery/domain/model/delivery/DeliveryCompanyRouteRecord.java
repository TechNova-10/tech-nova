package com.tech_nova.delivery.domain.model.delivery;

import com.tech_nova.delivery.domain.model.Timestamped;
import com.tech_nova.delivery.domain.model.manager.DeliveryManager;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "p_delivery_company_route_record")
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryCompanyRouteRecord extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "delivery_company_route_id")
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
    private UUID recipientCompanyId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryCompanyStatus currentStatus;

    @Column
    private Integer deliveryOrderSequence;

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

    public static DeliveryCompanyRouteRecord create(
            Delivery delivery,
            DeliveryManager deliveryManager,
            UUID departureHubId,
            UUID recipientCompanyId,
            DeliveryCompanyStatus currentStatus,
            Integer deliveryOrderSequence,
            Double expectedDistance,
            String expectedTime,
            Double realDistance,
            String realTime
    ) {
        return DeliveryCompanyRouteRecord.builder()
                .delivery(delivery)
                .deliveryManager(deliveryManager)
                .departureHubId(departureHubId)
                .recipientCompanyId(recipientCompanyId)
                .currentStatus(currentStatus)
                .deliveryOrderSequence(deliveryOrderSequence)
                .expectedDistance(expectedDistance)
                .expectedTime(expectedTime)
                .realDistance(realDistance)
                .realTime(realTime)
                .build();
    }

    public void update(
            DeliveryManager deliveryManager,
            UUID departureHubId,
            UUID recipientCompanyId,
            DeliveryCompanyStatus currentStatus,
            Integer deliveryOrderSequence,
            Double expectedDistance,
            String expectedTime,
            Double realDistance,
            String realTime) {
        this.deliveryManager = deliveryManager;
        this.departureHubId = departureHubId;
        this.recipientCompanyId = recipientCompanyId;
        this.currentStatus = currentStatus;
        this.deliveryOrderSequence = deliveryOrderSequence;
        this.expectedDistance = expectedDistance;
        this.expectedTime = expectedTime;
        this.realDistance = realDistance;
        this.realTime = realTime;
    }

    public void updateCurrentStatus(DeliveryCompanyStatus currentStatus) {
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
