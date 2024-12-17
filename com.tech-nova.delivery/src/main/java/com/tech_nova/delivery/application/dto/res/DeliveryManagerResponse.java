package com.tech_nova.delivery.application.dto.res;

import com.tech_nova.delivery.domain.model.manager.DeliveryManager;
import com.tech_nova.delivery.domain.model.manager.DeliveryManagerRole;
import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class DeliveryManagerResponse {
    private UUID id;
    private UUID assignedHubId;
    private UUID managerUserId;
    private DeliveryManagerRole managerRole;
    private Integer deliveryOrderSequence;

    public static DeliveryManagerResponse of(DeliveryManager deliveryManager) {
        return DeliveryManagerResponse.builder()
                .id(deliveryManager.getId())
                .assignedHubId(deliveryManager.getAssignedHubId())
                .managerUserId(deliveryManager.getManagerUserId())
                .managerRole(deliveryManager.getManagerRole())
                .deliveryOrderSequence(deliveryManager.getDeliveryOrderSequence())
                .build();
    }
}
