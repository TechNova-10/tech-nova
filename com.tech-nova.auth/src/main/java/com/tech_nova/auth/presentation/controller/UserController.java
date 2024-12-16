package com.tech_nova.auth.presentation.controller;

import com.tech_nova.auth.application.dto.res.UserResponse;
import com.tech_nova.auth.application.service.UserService;
import com.tech_nova.auth.presentation.dto.ApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{user_id}")
    public ResponseEntity<ApiResponseDto<UserResponse>> getDelivery(@PathVariable("user_id") UUID userId) {
        UserResponse user = userService.getUser(userId);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery created successfully", user));
    }
}
