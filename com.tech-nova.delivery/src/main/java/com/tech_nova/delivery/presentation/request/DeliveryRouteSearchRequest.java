package com.tech_nova.delivery.presentation.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeliveryRouteSearchRequest {
    private UUID id;
    private UUID deliveryManagerId;
    private UUID deliveryId;
    private String currentStatus;
    @JsonProperty("deleted")
    private boolean isDeleted;
}

