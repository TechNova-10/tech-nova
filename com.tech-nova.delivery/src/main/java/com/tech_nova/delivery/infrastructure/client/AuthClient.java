package com.tech_nova.delivery.infrastructure.client;

import com.tech_nova.delivery.application.dto.UserData;
import com.tech_nova.delivery.application.service.AuthService;
import com.tech_nova.delivery.presentation.dto.ApiResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "auth-service")
public interface AuthClient extends AuthService {

    @GetMapping("/api/v1/users/{user_id}")
    ApiResponseDto<UserData> getUser(
            @PathVariable("user_id") UUID searchUserId,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role);

    @PutMapping("/api/v1/users/{user_id}/role")
    ApiResponseDto<Void> updateUserRole(
            @PathVariable("user_id") UUID searchUserId,
            @RequestParam(name = "role") String updateRole,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role);

}