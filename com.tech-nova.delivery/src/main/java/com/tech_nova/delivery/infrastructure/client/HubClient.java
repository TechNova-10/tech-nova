package com.tech_nova.delivery.infrastructure.client;

import com.tech_nova.delivery.HubData;
import com.tech_nova.delivery.application.service.HubService;
import com.tech_nova.delivery.presentation.dto.ApiResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "hub-service")
public interface HubClient extends HubService {
    @GetMapping("/api/v1/hubs/{hubId}")
    ApiResponseDto<HubData> getHub(@PathVariable("hubId") UUID hubId, @RequestHeader(value = "role", required = false) String role);
}
