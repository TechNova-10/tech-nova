package com.tech_nova.product.infrastructure.client;

import com.tech_nova.product.infrastructure.dto.CompanyApiResponse;
import com.tech_nova.product.infrastructure.dto.CompanyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "company-service", path = "/api/v1/companies")
public interface CompanyServiceClient {
    @GetMapping("/{companyId}")
    CompanyApiResponse<CompanyResponse> getCompanyById(@PathVariable("companyId") UUID companyId);

    @GetMapping("/hub/{hubId}")
    CompanyApiResponse<List<CompanyResponse>> getCompaniesByHubId(@PathVariable("hubId") UUID hubId);

}
