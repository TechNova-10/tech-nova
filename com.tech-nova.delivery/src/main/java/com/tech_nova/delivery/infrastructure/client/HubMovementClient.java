package com.tech_nova.delivery.infrastructure.client;

import com.tech_nova.delivery.application.service.HubMovementService;
import com.tech_nova.delivery.infrastructure.dto.MovementRequestDto;
import com.tech_nova.delivery.infrastructure.dto.MovementResponse;
import com.tech_nova.delivery.presentation.dto.ApiResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "movement-service")
public interface HubMovementClient extends HubMovementService {
    @PostMapping("/api/v1/movements")
    ApiResponseDto<MovementResponse> createMovement(@RequestBody MovementRequestDto movementRequestDto, @RequestHeader(value = "user_id", required = true) UUID userId, @RequestHeader(value = "role", required = true) String role);
}
