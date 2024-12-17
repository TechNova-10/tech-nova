package com.tech_nova.delivery.application.service;

import com.tech_nova.delivery.application.dto.UserData;
import com.tech_nova.delivery.presentation.dto.ApiResponseDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

public interface AuthService {
    ApiResponseDto<UserData> getUser(
            @PathVariable("user_id") UUID searchUserId,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role);

    ApiResponseDto<Void> updateUserRole(
            @PathVariable("user_id") UUID searchUserId,
            @RequestParam(name = "role") String updateRole,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role);

}
