package com.tech_nova.delivery.application.service;

import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

public interface AuthService {
    UUID getUserId(@RequestHeader("Authorization") String token);

    String getUserRole(@RequestHeader("Authorization") String token);
}
