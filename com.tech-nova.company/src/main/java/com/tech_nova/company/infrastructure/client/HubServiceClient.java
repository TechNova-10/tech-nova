package com.tech_nova.company.infrastructure.client;


import com.tech_nova.company.infrastructure.dto.ClientApiResponse;
import com.tech_nova.company.infrastructure.dto.HubResponse;
import com.tech_nova.company.presentation.dto.ApiResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.UUID;

@FeignClient(name = "hub-service", path = "/api/v1/hubs")
public interface HubServiceClient {

    @GetMapping("/{hubId}")
    ClientApiResponse<ApiResponseDto<HubResponse>> getHub(@PathVariable("hubId") UUID hubId); // 허브 ID로 조회

}
