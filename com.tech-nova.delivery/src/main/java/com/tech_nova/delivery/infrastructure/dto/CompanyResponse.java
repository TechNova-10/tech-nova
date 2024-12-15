package com.tech_nova.delivery.infrastructure.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CompanyResponse {
    private UUID companyId;
    private String name;
    private String type;
    private UUID hubId;
    //    private UUID hubManagerId; // 허브  관리자 ID
    private String province;
    private String city;
    private String district;
    private String street;

}
