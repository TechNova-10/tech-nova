package com.tech_nova.delivery.infrastructure.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class MovementResponse {
    private UUID movementInfoId;
    private UUID departureHubId;
    private UUID intermediateHubId;
    private UUID arrivalHubId;
    private double timeTravel;
    private double distance;
    private LocalDateTime createdAt;
    private UUID createdBy;
    private LocalDateTime updatedAt;
    private UUID updatedBy;
}
