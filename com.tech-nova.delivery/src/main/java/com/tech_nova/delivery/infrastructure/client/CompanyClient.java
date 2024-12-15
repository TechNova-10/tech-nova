package com.tech_nova.delivery.infrastructure.client;

import com.tech_nova.delivery.application.service.CompanyService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "company-service", url = "http://auth-service/api/v1/companies")
public interface CompanyClient extends CompanyService {
}