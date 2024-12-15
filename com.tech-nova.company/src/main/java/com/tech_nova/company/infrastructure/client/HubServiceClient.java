package com.tech_nova.company.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "hub-service", path = "/api/v1/hubs")
public interface HubServiceClient {

    @GetMapping("/{hubId}")
    boolean isHubIdValid(@PathVariable("hubId") String hubId); // 허브 ID 검증
}