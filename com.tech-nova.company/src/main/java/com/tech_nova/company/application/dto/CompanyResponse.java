package com.tech_nova.company.application.dto;

import com.tech_nova.company.domain.model.Company;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class CompanyResponse {

    private UUID companyId; // 업체 ID
    private String name; // 업체명
    private String type; // 업체 타입
//    private UUID hubId; // 허브 ID
//    private UUID hubManagerId; // 허브  관리자 ID
    private String province;
    private String city;
    private String district;
    private String street;

    public CompanyResponse(Company company) {
        this.companyId = company.getCompanyId();
        this.name = company.getName();
        this.type = company.getType().name();
//        this.hubId = company.getHubId();
//        this.hubManagerId = company.getHubManagerId();
        this.province = company.getProvince();
        this.city = company.getCity();
        this.district= company.getDistrict();
        this.street = company.getStreet();
    }
}