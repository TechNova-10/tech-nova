package com.tech_nova.delivery.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RouteEstimateData {
    private Integer distance;
    private Integer duration;
}
