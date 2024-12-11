package com.tech_nova.delivery.domain.model.manager;

import com.tech_nova.delivery.domain.model.Timestamped;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "p_delivery_manager")
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryManager extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "delivery_manager_id")
    private UUID id;

    @Column(nullable = false)
    private UUID assignedHubId;

    @Column(nullable = false)
    private UUID managerUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryManagerRole managerRole;

    @Column(nullable = false)
    private Integer deliveryOrderSequence;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isDeleted;

    public static DeliveryManager create(
            UUID assignedHubId,
            UUID managerUserId,
            String managerRole,
            int deliveryOrderSequence
    ) {
        return DeliveryManager.builder()
                .assignedHubId(assignedHubId)
                .managerUserId(managerUserId)
                .managerRole(DeliveryManagerRole.fromString(managerRole))
                .deliveryOrderSequence(deliveryOrderSequence)
                .build();
    }

    public void update(
            UUID assignedHubId,
            DeliveryManagerRole managerRole,
            int deliveryOrderSequence) {
        this.assignedHubId = assignedHubId;
        this.managerRole = managerRole;
        this.deliveryOrderSequence = deliveryOrderSequence;
    }

    public void markAsDeleted(UUID deletedBy) {
        super.markAsDeleted(deletedBy);
        this.isDeleted = true;
    }
}
