package com.tech_nova.delivery.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovementRequestDto {
    private UUID departureHubId;
    private UUID arrivalHubId;
}
