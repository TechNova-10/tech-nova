package com.tech_nova.delivery.application.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
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

    private LocalDateTime createdAt;

    private UUID createdBy;

    private LocalDateTime updatedAt;

    private UUID updatedBy;
}
