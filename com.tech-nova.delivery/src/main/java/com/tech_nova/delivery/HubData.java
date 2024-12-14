package com.tech_nova.delivery;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HubData {
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
