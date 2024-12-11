package com.tech_nova.hub.application.dtos.res;

import com.tech_nova.hub.domain.model.Hub;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HubResponseDto {

  private UUID hubId;

  private UUID hubManagerId;

  private String name;

  private String province;

  private String city;

  private String district;

  private String roadName;

  private double latitude;

  private double longitude;

  private boolean isDeleted;

  private LocalDateTime createdAt;

  private UUID createdBy;

  private LocalDateTime updatedAt;

  private UUID updatedBy;

  public static HubResponseDto of(Hub hub) {
    return HubResponseDto.builder()
        .hubId(hub.getHubId())
        .hubManagerId(hub.getHubManagerId())
        .name(hub.getName())
        .province(hub.getProvince())
        .city(hub.getCity())
        .district(hub.getDistrict())
        .roadName(hub.getRoadName())
        .latitude(hub.getLatitude())
        .longitude(hub.getLongitude())
        .isDeleted(hub.getIsDeleted())
        .createdAt(hub.getCreatedAt())
        .createdBy(hub.getCreatedBy())
        .updatedAt(hub.getUpdatedAt())
        .updatedBy(hub.getUpdatedBy())
        .build();
  }
}
