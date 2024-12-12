package com.tech_nova.delivery.presentation.controller;

import com.tech_nova.delivery.application.service.DeliveryService;
import com.tech_nova.delivery.presentation.dto.ApiResponseDto;
import com.tech_nova.delivery.presentation.request.DeliveryCompanyRouteUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/deliveries/companies/routes")
@RequiredArgsConstructor
public class DeliveryCompanyRouteRecordController {

    private final DeliveryService deliveryService;

    @PostMapping
    public ResponseEntity<ApiResponseDto<Void>> createCompanyRouteRecord(@RequestParam("delivery_id") UUID deliveryId) {
        deliveryService.createCompanyRouteRecordByDeliveryId(deliveryId);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery route status created successfully"));
    }

    @PatchMapping("/{delivery_route_id}")
    public ResponseEntity<ApiResponseDto<UUID>> updateCompanyDeliveryRoute(@PathVariable("delivery_route_id") UUID deliveryRouteId, @RequestBody DeliveryCompanyRouteUpdateRequest request) {
        UUID routeRecordId = deliveryService.updateCompanyRouteRecord(deliveryRouteId, request.toDTO());
        return ResponseEntity.ok(ApiResponseDto.success("Delivery route status updated successfully", routeRecordId));
    }

    @PatchMapping("/{delivery_route_id}/status")
    public ResponseEntity<ApiResponseDto<UUID>> updateCompanyDeliveryRouteStatus(@PathVariable("delivery_route_id") UUID deliveryRouteId, @RequestParam("current_status") String currentStatus) {
        UUID routeRecordId = deliveryService.updateCompanyRouteRecordState(deliveryRouteId, currentStatus);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery route status updated successfully", routeRecordId));
    }

    @DeleteMapping("/{delivery_route_id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteCompanyDeliveryRoute(@PathVariable("delivery_route_id") UUID deliveryRouteId) {
        deliveryService.deleteCompanyRouteRecord(deliveryRouteId);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery route status deleted successfully"));
    }
}
