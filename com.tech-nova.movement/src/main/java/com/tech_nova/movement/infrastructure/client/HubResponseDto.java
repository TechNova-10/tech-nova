package com.tech_nova.movement.infrastructure.client;

import java.util.List;
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
