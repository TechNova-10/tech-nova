package com.tech_nova.delivery.infrastructure.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class MovementRequestDto {
    private UUID departureHubId;
    private UUID arrivalHubId;
}
