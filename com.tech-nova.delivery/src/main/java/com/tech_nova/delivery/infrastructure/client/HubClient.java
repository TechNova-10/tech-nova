package com.tech_nova.delivery.infrastructure.client;

import com.tech_nova.delivery.application.dto.HubData;
import com.tech_nova.delivery.application.dto.res.HubResponseDto;
import com.tech_nova.delivery.application.service.HubService;
import com.tech_nova.delivery.infrastructure.dto.HubSearchDto;
import com.tech_nova.delivery.presentation.dto.ApiResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "hub-service")
public interface HubClient extends HubService {
    @GetMapping("/api/v1/hubs/{hubId}")
    ApiResponseDto<HubData> getHub(@PathVariable("hubId") UUID hubId, @RequestHeader(value = "role", required = false) String role);

    // @GetMapping("/api/v1/hubs")
    // ApiResponseDto<Page<HubResponseDto>> getHubs(HubSearchDto hubSearchDto, @RequestHeader(value = "role", required = false) String role, Pageable pageable);

    @GetMapping("/api/v1/hubs")
    ApiResponseDto<Page<HubResponseDto>> getHubs(
            @SpringQueryMap HubSearchDto hubSearchDto,
            @RequestHeader(value = "role", required = false) String role,
            @RequestParam("page") int page,
            @RequestParam("size") int size
    );
}
