package com.tech_nova.delivery.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HubMovementData {
    private UUID id;
    private UUID departureHubId;
    private UUID arrivalHubId;
    private Double timeTravel;
    private Double distance;
}
