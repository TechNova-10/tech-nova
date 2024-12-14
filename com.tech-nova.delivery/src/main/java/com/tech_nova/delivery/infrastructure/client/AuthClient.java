package com.tech_nova.delivery.infrastructure.client;

import com.tech_nova.delivery.application.service.AuthService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "auth-service", url = "http://auth-service/api/v1/auth")
public interface AuthClient extends AuthService {

    @GetMapping("/id")
    UUID getUserId(@RequestHeader("Authorization") String token);

    @GetMapping("/role")
    String getUserRole(@RequestHeader("Authorization") String token);
}