package com.tech_nova.delivery.application.service;

import com.tech_nova.delivery.infrastructure.dto.MovementRequestDto;
import com.tech_nova.delivery.presentation.dto.ApiResponseDto;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

public interface HubMovementService {
    ApiResponseDto<Void> createMovement(@RequestBody MovementRequestDto movementRequestDto, @RequestHeader(value = "user_id", required = true) UUID userId, @RequestHeader(value = "role", required = true) String role);

}
