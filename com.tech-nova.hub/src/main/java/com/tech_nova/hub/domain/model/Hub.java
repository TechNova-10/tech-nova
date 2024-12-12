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

  @Column(name = "province", nullable = false)
  private String province;

  @Column(name = "city", nullable = false)
  private String city;

  @Column(name = "district", nullable = false)
  private String district;

  @Column(name = "roadName", nullable = false)
  private String roadName;

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

  @Column(name = "deleted_at")
  private LocalDateTime deleted_at;

  @Column(name = "deleted_By")
  private UUID deleted_By;

  public static Hub createHub(HubRequestDto hubRequestDto, UUID userId) {
    return Hub.builder()
        .hubManagerId(userId)
        .name(hubRequestDto.getName())
        .province(hubRequestDto.getProvince())
        .city(hubRequestDto.getCity())
        .district(hubRequestDto.getDistrict())
        .roadName(hubRequestDto.getRoadName())
        .latitude(hubRequestDto.getLatitude())
        .longitude(hubRequestDto.getLongitude())
        .isDeleted(false)
        .createdBy(userId)
        .updatedBy(userId)
        .build();
  }

  public void updateHub(HubRequestDto hubRequestDto, UUID userId) {
    this.name = hubRequestDto.getName();
    this.province = hubRequestDto.getProvince();
    this.city = hubRequestDto.getCity();
    this.district = hubRequestDto.getDistrict();
    this.roadName = hubRequestDto.getRoadName();
    this.latitude = hubRequestDto.getLatitude();
    this.longitude = hubRequestDto.getLongitude();
    this.updatedBy = userId;
  }

  public void deleteHub(UUID userId) {
    this.isDeleted = true;
    this.deleted_at = LocalDateTime.now();
    this.deleted_By = userId;
  }
}
