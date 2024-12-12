package com.tech_nova.company.application.dto;

import com.tech_nova.company.domain.model.CompanyType;
import lombok.Data;

import java.util.UUID;

@Data
public class CompanyRequest {

    private String name; // 업체명
    private CompanyType type; // 업체 타입 (생산업체/수령업체)
    private UUID hubId; // 허브 ID
    private UUID hubManagerId; // 허브 관리자 ID
    private String province;
    private String city;
    private String district;
    private String street;
}