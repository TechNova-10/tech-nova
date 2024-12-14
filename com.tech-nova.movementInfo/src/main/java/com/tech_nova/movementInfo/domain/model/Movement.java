package com.tech_nova.movementInfo.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Entity
@Table(name = "p_movement_info")
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Movement {


  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "movement_info_id", updatable = false, nullable = false)
  private UUID movementId;

  @Column(name = "departure_hub_id", nullable = false)
  private UUID departureHubId;

  @Column(name = "intermediate_Hub_Id", nullable = false)
  private UUID intermediateHubId;

  @Column(name = "arrival_hub_id", nullable = false)
  private UUID arrivalHubId;

  @Column(name = "time_travel", nullable = false)
  private double timeTravel;

  @Column(name = "distance", nullable = false)
  private double distance;

  @Column(name = "is_deleted", nullable = false)
  private boolean isDeleted = false;

  @Column(name = "created_at", updatable = false)
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(name = "created_by", nullable = false)
  private UUID createdBy;

  @Column(name = "updated_at")
  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @Column(name = "updated_by", nullable = false)
  private UUID updatedBy;

  @Column(name = "deleted_at")
  private LocalDateTime deleted_at;

  @Column(name = "deleted_By")
  private UUID deleted_By;

  public static Movement creatMovement(
      UUID departureHubId,
      UUID intermediateHubId,
      UUID arrivalHubId,
      double timeTravel,
      double distance,
      UUID userId) {
    return Movement.builder()
        .departureHubId(departureHubId)
        .intermediateHubId(intermediateHubId)
        .arrivalHubId(arrivalHubId)
        .timeTravel(timeTravel)
        .distance(distance)
        .isDeleted(false)
        .createdBy(userId)
        .updatedBy(userId)
        .build();
  }
}
