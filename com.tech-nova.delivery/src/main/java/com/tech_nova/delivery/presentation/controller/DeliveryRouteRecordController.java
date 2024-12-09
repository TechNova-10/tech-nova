package com.tech_nova.delivery.presentation.controller;

import com.tech_nova.delivery.application.service.DeliveryService;
import com.tech_nova.delivery.presentation.dto.ApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/deliveries/routes")
@RequiredArgsConstructor
public class DeliveryRouteRecordController {

    private final DeliveryService deliveryService;

    @PatchMapping("/{delivery_route_id}/status")
    public ApiResponseDto<UUID> updateDeliveryRouteStatus(@PathVariable("delivery_route_id") UUID deliveryRouteId, @RequestParam("current_status") String currentStatus) {
        UUID routeRecordId = deliveryService.updateRouteRecordState(deliveryRouteId, currentStatus);
        return ApiResponseDto.success("Delivery route status updated successfully", routeRecordId);
    }
}
