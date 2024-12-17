package com.tech_nova.movementInfo.application.dtos.res;

import com.tech_nova.movementInfo.domain.model.Movement;
import java.time.LocalDateTime;
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

  private LocalDateTime createdAt;

  private UUID createdBy;

  private LocalDateTime updatedAt;

  private UUID updatedBy;

  public static MovementResponseDto of(Movement movement) {
    return MovementResponseDto.builder()
        .movementInfoId(movement.getMovementId())
        .departureHubId(movement.getDepartureHubId())
        .intermediateHubId(movement.getIntermediateHubId())
        .arrivalHubId(movement.getArrivalHubId())
        .timeTravel(movement.getTimeTravel())
        .distance(movement.getDistance())
        .createdAt(movement.getCreatedAt())
        .createdBy(movement.getCreatedBy())
        .updatedAt(movement.getUpdatedAt())
        .updatedBy(movement.getUpdatedBy())
        .build();
  }
}
