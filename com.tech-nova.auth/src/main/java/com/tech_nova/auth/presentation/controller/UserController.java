package com.tech_nova.auth.presentation.controller;

import com.tech_nova.auth.application.dto.res.UserResponse;
import com.tech_nova.auth.application.service.UserService;
import com.tech_nova.auth.presentation.dto.ApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{user_id}")
    public ResponseEntity<ApiResponseDto<UserResponse>> getUser(
            @PathVariable("user_id") UUID searchUserId,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role) {
        UserResponse user = userService.getUser(searchUserId, userId, role);
        return ResponseEntity.ok(ApiResponseDto.success("getUser successfully", user));
    }

    @PutMapping("/{user_id}/role")
    public ResponseEntity<ApiResponseDto<Void>> updateUserRole(
            @PathVariable("user_id") UUID searchUserId,
            @RequestParam(name = "role") String updateRole,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role) {
        userService.updateUserRole(searchUserId, updateRole, userId, role);
        return ResponseEntity.ok(ApiResponseDto.success("User Role updated successfully"));
    }

    @PatchMapping("/{user_id}/slack-id")
    public ResponseEntity<ApiResponseDto<Void>> updateUserSlackId(
            @PathVariable("user_id") UUID searchUserId,
            @RequestParam(name = "slack_id") String slackId,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role) {
        userService.updateSlackId(searchUserId, slackId, userId, role);
        return ResponseEntity.ok(ApiResponseDto.success("User SlackId updated successfully"));
    }

    @DeleteMapping("/{user_id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteUser(
            @PathVariable("user_id") UUID searchUserId,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role) {
        userService.deleteUser(searchUserId, userId, role);
        return ResponseEntity.ok(ApiResponseDto.success("User Role deleted successfully"));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getUserByToken(@RequestHeader("Authorization") String token) {
        String jwt = token.startsWith("Bearer ") ? token.replace("Bearer ", "") : token;
        UserResponse userResponse = userService.getUserByToken(jwt);
        return ResponseEntity.ok(userResponse);
    }
    @GetMapping("/me/role")
    public ResponseEntity<String> getUserRoleByToken(@RequestHeader("Authorization") String token) {
        String jwt = token.startsWith("Bearer ") ? token.replace("Bearer ", "") : token;
        String role = userService.getUserRoleByToken(jwt);
        return ResponseEntity.ok(role);
    }

}
