package com.tech_nova.auth.presentation.controller;

import com.tech_nova.auth.application.dto.res.UserResponse;
import com.tech_nova.auth.application.service.UserService;
import com.tech_nova.auth.presentation.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "사용자 정보 조회")
    @GetMapping("/{user_id}")
    public ResponseEntity<ApiResponseDto<UserResponse>> getUser(
            @PathVariable("user_id") UUID searchUserId,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role) {
        UserResponse user = userService.getUser(searchUserId, userId, role);
        return ResponseEntity.ok(ApiResponseDto.success("getUser successfully", user));
    }

    @Operation(summary = "사용자 역할 수정")
    @PutMapping("/{user_id}/role")
    public ResponseEntity<ApiResponseDto<Void>> updateUserRole(
            @PathVariable("user_id") UUID searchUserId,
            @RequestParam(name = "role") String updateRole,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role) {
        userService.updateUserRole(searchUserId, updateRole, userId, role);
        return ResponseEntity.ok(ApiResponseDto.success("User Role updated successfully"));
    }

    @Operation(summary = "사용자 슬랙 아이디 수정")
    @PatchMapping("/{user_id}/slack-id")
    public ResponseEntity<ApiResponseDto<Void>> updateUserSlackId(
            @PathVariable("user_id") UUID searchUserId,
            @RequestParam(name = "slack_id") String slackId,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role) {
        userService.updateSlackId(searchUserId, slackId, userId, role);
        return ResponseEntity.ok(ApiResponseDto.success("User SlackId updated successfully"));
    }

    @Operation(summary = "사용자 삭제")
    @DeleteMapping("/{user_id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteUser(
            @PathVariable("user_id") UUID searchUserId,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role) {
        userService.deleteUser(searchUserId, userId, role);
        return ResponseEntity.ok(ApiResponseDto.success("User Role deleted successfully"));
    }
}
