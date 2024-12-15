package com.tech_nova.delivery.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HubSearchDto {

    private String name;

    private String province;

    private String city;

    private String district;

    private String roadName;

    @JsonProperty("deleted")
    private boolean isDeleted;
}
