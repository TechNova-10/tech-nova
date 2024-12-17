package com.tech_nova.movementInfo.infrastructure.client;

import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HubResponseDto {

  private UUID hubId;
  private String name;
  private double latitude;
  private double longitude;
}
