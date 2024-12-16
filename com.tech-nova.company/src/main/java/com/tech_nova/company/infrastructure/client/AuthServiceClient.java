package com.tech_nova.company.infrastructure.client;

import com.tech_nova.company.infrastructure.dto.ClientApiResponse;
import com.tech_nova.company.infrastructure.dto.UserData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-service", path = "/api/v1/users")
public interface AuthServiceClient {

    @GetMapping("/me")
    ClientApiResponse<UserData> getUserByToken(@RequestHeader("Authorization") String token); // 사용자 허브 ID 조회

    @GetMapping("/me/role")
    ClientApiResponse<String> getUserRoleByToken(@RequestHeader("Authorization") String token); // 사용자 역할 조회
}