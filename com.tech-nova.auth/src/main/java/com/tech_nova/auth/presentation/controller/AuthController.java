package com.tech_nova.auth.presentation.controller;


import com.tech_nova.auth.application.service.AuthService;
import com.tech_nova.auth.presentation.dto.ApiResponseDto;
import com.tech_nova.auth.presentation.dto.AuthResponseDto;
import com.tech_nova.auth.presentation.request.SignInRequestDto;
import com.tech_nova.auth.presentation.request.UserRequest;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "로그인")
    @PostMapping("/signIn")
    public ResponseEntity<?> signIn(@RequestBody SignInRequestDto signInRequest) {
        String token = authService.signIn(signInRequest.getUsername(), signInRequest.getPassword());
        return ResponseEntity.ok(new AuthResponseDto(token));
    }

    @Operation(summary = "회원가입")
    @PostMapping("/signUp")
    public ResponseEntity<ApiResponseDto<Void>> signUp(@RequestBody UserRequest request) {
        authService.signUp(request.toDTO());
        return ResponseEntity.ok(ApiResponseDto.success("User created successfully"));
    }
}