package com.tech_nova.delivery.infrastructure.client;

import com.tech_nova.delivery.HubData;
import com.tech_nova.delivery.application.dto.res.HubResponseDto;
import com.tech_nova.delivery.application.service.HubService;
import com.tech_nova.delivery.infrastructure.dto.HubSearchDto;
import com.tech_nova.delivery.presentation.dto.ApiResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "hub-service", url = "http://auth-service/api/v1/hubs")
public interface HubClient extends HubService {
    @GetMapping("/{hubId}")
    ApiResponseDto<HubData> getHub(@PathVariable("hubId") UUID hubId, @RequestHeader(value = "role", required = false) String role);

    @GetMapping
    ApiResponseDto<Page<HubResponseDto>> getHubs(HubSearchDto hubSearchDto, @RequestHeader(value = "role", required = false) String role, Pageable pageable);
}
