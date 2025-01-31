package com.tech_nova.delivery.infrastructure.client;

import com.tech_nova.delivery.application.service.CompanyService;
import com.tech_nova.delivery.infrastructure.dto.CompanyResponse;
import com.tech_nova.delivery.presentation.dto.ApiResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "company-service")
public interface CompanyClient extends CompanyService {
    @GetMapping("/api/v1/companies/{companyId}")
    ApiResponseDto<CompanyResponse> getCompanyById(@PathVariable UUID companyId);
}