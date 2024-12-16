package com.tech_nova.delivery.presentation.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeliveryManagerSearchRequest {
    private UUID id;
    private UUID assignedHubId;
    private UUID managerUserId;
    private String managerRole;
    @JsonProperty("deleted")
    private boolean isDeleted;
}

