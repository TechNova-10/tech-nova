package com.tech_nova.hub.application.dtos.res;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;

@Getter
public class HubRequestDto {

  private String hubName;

  private String addressCode;

  private String roadAddress;

  private String detailedAddress;

  @Min(-90)
  @Max(90)
  private double latitude;

  @Min(-180)
  @Max(180)
  private double longitude;
}
