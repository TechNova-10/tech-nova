package com.tech_nova.delivery.application.service;

import com.tech_nova.delivery.infrastructure.dto.CompanyResponse;
import com.tech_nova.delivery.presentation.dto.ApiResponseDto;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

public interface CompanyService {
    ApiResponseDto<CompanyResponse> getCompanyById(@PathVariable UUID companyId);
}
