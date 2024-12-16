package com.tech_nova.delivery.presentation.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class DeliverySearchRequest {
    private UUID id;
    private UUID orderId;
    private UUID departureHubId;
    private UUID arrivalHubId;
    private List<UUID> manageHubIds;
    private String currentStatus;
    private String recipient;
    @JsonProperty("deleted")
    private boolean isDeleted;
}

