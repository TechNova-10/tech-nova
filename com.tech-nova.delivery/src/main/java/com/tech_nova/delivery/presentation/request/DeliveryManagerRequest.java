package com.tech_nova.delivery.presentation.request;

import com.tech_nova.delivery.application.dto.DeliveryManagerDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class DeliveryManagerRequest {
    private UUID assignedHubId;
    private UUID managerUserId;
    private String managerRole;
    private Integer deliveryOrderSequence;

    public DeliveryManagerDto toDTO() {
        return DeliveryManagerDto.create(this.assignedHubId, this.managerUserId, this.managerRole, this.deliveryOrderSequence);
    }
}
