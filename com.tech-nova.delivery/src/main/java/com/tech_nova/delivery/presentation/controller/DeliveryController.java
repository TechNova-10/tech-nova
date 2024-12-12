package com.tech_nova.delivery.presentation.controller;

import com.tech_nova.delivery.application.service.DeliveryService;
import com.tech_nova.delivery.presentation.dto.ApiResponseDto;
import com.tech_nova.delivery.presentation.request.DeliveryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping
    public ResponseEntity<ApiResponseDto<Void>> createDelivery(@RequestBody DeliveryRequest request) {
        deliveryService.createDelivery(request.toDTO());
        return ResponseEntity.ok(ApiResponseDto.success("Delivery created successfully"));
    }

    @DeleteMapping("/{delivery_id}")
    public ResponseEntity<ApiResponseDto<Void>> createDelivery(@PathVariable("delivery_id") UUID deliveryId) {
        deliveryService.deleteDelivery(deliveryId);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery created successfully"));
    }
}
