package com.tech_nova.movementInfo.application.dtos.req;

import java.util.UUID;
import lombok.Getter;

@Getter
public class MovementRequestDto {

  private UUID departureHubId;

  private UUID arrivalHubId;
}
