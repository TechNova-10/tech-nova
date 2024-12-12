package com.tech_nova.delivery.presentation.controller;

import com.tech_nova.delivery.application.service.DeliveryService;
import com.tech_nova.delivery.presentation.dto.ApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/deliveries/companies/routes")
@RequiredArgsConstructor
public class DeliveryCompanyRouteRecordController {

    private final DeliveryService deliveryService;

    @PostMapping
    public ApiResponseDto<Void> createCompanyRouteRecord(@RequestParam("delivery_id") UUID deliveryId) {
        deliveryService.createCompanyRouteRecordByDeliveryId(deliveryId);
        return ApiResponseDto.success("Delivery route status updated successfully");
    }


}
