package com.tech_nova.delivery.application.dto;

import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class DeliveryManagerDto {
    private UUID assignedHubId;
    private UUID managerUserId;
    private String managerRole;
    private Integer deliveryOrderSequence;

    public static DeliveryManagerDto create(UUID assignedHubId, UUID managerUserId, String managerRole, Integer deliveryOrderSequence) {
        return DeliveryManagerDto.builder()
                .assignedHubId(assignedHubId)
                .managerUserId(managerUserId)
                .managerRole(managerRole)
                .deliveryOrderSequence(deliveryOrderSequence)
                .build();
    }
}
