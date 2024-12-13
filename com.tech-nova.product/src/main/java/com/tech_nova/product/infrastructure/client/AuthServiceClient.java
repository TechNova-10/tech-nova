package com.tech_nova.product.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-service", path = "/api/v1/auth")
public interface AuthServiceClient {

    @GetMapping("/hub")
    String getUserHubId(@RequestHeader("Authorization") String token); // 사용자 허브 ID 조회

    @GetMapping("/role")
    String getUserRole(@RequestHeader("Authorization") String token); // 사용자 역할 조회
}
