package com.tech_nova.product.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "company-service", path = "/api/v1/companies")
public interface CompanyServiceClient {

    @GetMapping("/{companyId}/exists")
    boolean isCompanyIdValid(@PathVariable("companyId") String companyId); // 회사 ID 검증

    @GetMapping("/hubs/{hubId}/exists")
    boolean isHubIdValid(@PathVariable("hubId") String hubId); // 허브 ID 검증
}
