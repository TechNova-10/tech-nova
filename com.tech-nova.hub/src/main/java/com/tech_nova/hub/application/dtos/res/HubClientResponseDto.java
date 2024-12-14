package com.tech_nova.hub.application.dtos.res;

import com.tech_nova.hub.domain.model.Hub;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HubClientResponseDto {

  private UUID hubId;

  private String name;

  private double latitude;

  private double longitude;

  public static HubClientResponseDto of(Hub hub) {
    return HubClientResponseDto.builder()
        .hubId(hub.getHubId())
        .name(hub.getName())
        .latitude(hub.getLatitude())
        .longitude(hub.getLongitude())
        .build();
  }
}
