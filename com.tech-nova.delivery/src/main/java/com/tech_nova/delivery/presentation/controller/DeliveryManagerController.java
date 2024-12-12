package com.tech_nova.delivery.presentation.controller;

import com.tech_nova.delivery.application.service.DeliveryManagerService;
import com.tech_nova.delivery.presentation.dto.ApiResponseDto;
import com.tech_nova.delivery.presentation.request.DeliveryManagerRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/delivery-managers")
@RequiredArgsConstructor
public class DeliveryManagerController {
    private final DeliveryManagerService deliveryManagerService;

    @PostMapping
    public ResponseEntity<ApiResponseDto<UUID>> createDeliveryManager(@RequestBody DeliveryManagerRequest request) {
        UUID deliveryManagerId = deliveryManagerService.createDeliveryManager(request.toDTO());
        return ResponseEntity.ok(ApiResponseDto.success("Delivery Manager created successfully", deliveryManagerId));
    }
}
