package com.tech_nova.movement.application.dtos.res;

import com.tech_nova.movement.domain.model.Movement;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MovementResponseDto {

  private UUID movementInfoId;

  private UUID departureHubId;

  private UUID intermediateHubId;

  private UUID arrivalHubId;

  private double timeTravel;

  private double distance;

  public static MovementResponseDto of(Movement movement) {
    return MovementResponseDto.builder()
        .movementInfoId(movement.getMovementInfoId())
        .departureHubId(movement.getDepartureHubId())
        .intermediateHubId(movement.getIntermediateHubId())
        .timeTravel(movement.getTimeTravel())
        .distance(movement.getDistance())
        .build();
  }
}
