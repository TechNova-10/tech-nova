package com.tech_nova.hub.domain.model;

import com.tech_nova.hub.application.dtos.req.HubRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
@Table(name = "p_hub")
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Hub {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "hub_id", updatable = false, nullable = false)
  private UUID hubId;

  @Column(name = "hub_manager_id", nullable = false)
  private UUID hubManagerId;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "address_code", nullable = false)
  private String addressCode;

  @Column(name = "road_address", nullable = false)
  private String roadAddress;

  @Column(name = "detailed_address")
  private String detailedAddress;

  @Column(name = "latitude", nullable = false)
  @Min(-90)
  @Max(90)
  private double latitude;

  @Column(name = "longitude", nullable = false)
  @Min(-180)
  @Max(180)
  private double longitude;

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

  public static Hub createHub(HubRequestDto hubRequestDto, UUID userId) {
    return Hub.builder()
        .hubManagerId(userId)
        .name(hubRequestDto.getName())
        .addressCode(hubRequestDto.getAddressCode())
        .roadAddress(hubRequestDto.getRoadAddress())
        .detailedAddress(hubRequestDto.getDetailedAddress())
        .latitude(hubRequestDto.getLatitude())
        .longitude(hubRequestDto.getLongitude())
        .createdBy(userId)
        .updatedBy(userId)
        .build();
  }
}
