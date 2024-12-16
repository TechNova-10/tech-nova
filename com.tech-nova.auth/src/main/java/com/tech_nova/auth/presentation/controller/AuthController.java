package com.tech_nova.auth.presentation.controller;


import com.tech_nova.auth.application.service.AuthService;
import com.tech_nova.auth.domain.model.User;
import com.tech_nova.auth.presentation.dto.AuthResponseDto;
import com.tech_nova.auth.presentation.request.SignInRequestDto;
import com.tech_nova.auth.presentation.request.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signIn")
    public ResponseEntity<?> signIn(@RequestBody SignInRequestDto signInRequest) {
        String token = authService.signIn(signInRequest.getUsername(), signInRequest.getPassword());
        return ResponseEntity.ok(new AuthResponseDto(token));
    }

    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@RequestBody UserRequest request,
                                    @RequestHeader(value = "X-User-Id", required = true) String userId,
                                    @RequestHeader(value = "X-Role", required = true) String role) {
        User createdUser = authService.signUp(request.toDTO(), userId, role);
        return ResponseEntity.ok(createdUser);
    }
}