package com.tech_nova.hub.application.dtos.req;

import lombok.Getter;
import org.springframework.data.domain.Pageable;

@Getter
public class HubSearchDto {

  private String name;

  private String province;

  private String city;

  private String district;

  private String roadName;

  private double latitudeStart;

  private double latitudeEnd;

  private double longitudeStart;

  private double longitudeEnd;

  private String sortBy;

  private Pageable pageable;
}
