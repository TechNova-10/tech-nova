package com.tech_nova.delivery.presentation.controller;

import com.tech_nova.delivery.application.dto.res.DeliveryRouteRecordResponse;
import com.tech_nova.delivery.application.service.DeliveryService;
import com.tech_nova.delivery.presentation.dto.ApiResponseDto;
import com.tech_nova.delivery.presentation.request.DeliveryRouteUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/deliveries/routes")
@RequiredArgsConstructor
public class DeliveryRouteRecordController {

    private final DeliveryService deliveryService;

    @GetMapping("/{delivery_route_id}")
    public ResponseEntity<ApiResponseDto<DeliveryRouteRecordResponse>> getDelivery(@PathVariable("delivery_route_id") UUID deliveryRouteId) {
        DeliveryRouteRecordResponse routeRecord = deliveryService.getDeliveryRouteRecord(deliveryRouteId);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery created successfully", routeRecord));
    }

    @PatchMapping("/{delivery_route_id}")
    public ResponseEntity<ApiResponseDto<UUID>> updateDeliveryRoute(@PathVariable("delivery_route_id") UUID deliveryRouteId, @RequestBody DeliveryRouteUpdateRequest request) {
        UUID routeRecordId = deliveryService.updateRouteRecord(deliveryRouteId, request.toDTO());
        return ResponseEntity.ok(ApiResponseDto.success("Delivery route updated successfully", routeRecordId));
    }

    @PatchMapping("/{delivery_route_id}/status")
    public ResponseEntity<ApiResponseDto<UUID>> updateDeliveryRouteStatus(@PathVariable("delivery_route_id") UUID deliveryRouteId, @RequestParam("current_status") String currentStatus) {
        UUID routeRecordId = deliveryService.updateRouteRecordState(deliveryRouteId, currentStatus);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery route status updated successfully", routeRecordId));
    }

    @PatchMapping("/{delivery_route_id}/delivery-managers")
    public ResponseEntity<ApiResponseDto<UUID>> updateDeliveryRouteStatus(@PathVariable("delivery_route_id") UUID deliveryRouteId, @RequestParam("delivery_manage_id") UUID deliveryManagerId) {
        UUID routeRecordId = deliveryService.updateRouteRecordDeliveryManager(deliveryRouteId, deliveryManagerId);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery route status updated successfully", routeRecordId));
    }

    @DeleteMapping("/{delivery_route_id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteDeliveryRoute(@PathVariable("delivery_route_id") UUID deliveryRouteId) {
        deliveryService.deleteRouteRecord(deliveryRouteId);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery route deleted successfully"));
    }
}
