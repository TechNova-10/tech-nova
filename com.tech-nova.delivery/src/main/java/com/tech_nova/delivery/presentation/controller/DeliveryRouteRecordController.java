package com.tech_nova.delivery.presentation.controller;

import com.tech_nova.delivery.application.dto.res.DeliveryRouteRecordResponse;
import com.tech_nova.delivery.application.service.DeliveryRouteRecordService;
import com.tech_nova.delivery.application.service.DeliveryService;
import com.tech_nova.delivery.presentation.dto.ApiResponseDto;
import com.tech_nova.delivery.presentation.request.DeliveryRouteSearchRequest;
import com.tech_nova.delivery.presentation.request.DeliveryRouteUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/deliveries/routes")
@RequiredArgsConstructor
public class DeliveryRouteRecordController {

    private final DeliveryService deliveryService;
    private final DeliveryRouteRecordService deliveryRouteRecordService;

    @GetMapping("/{delivery_route_id}")
    public ResponseEntity<ApiResponseDto<DeliveryRouteRecordResponse>> getDelivery(@PathVariable("delivery_route_id") UUID deliveryRouteId) {
        DeliveryRouteRecordResponse routeRecord = deliveryService.getDeliveryRouteRecord(deliveryRouteId);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery created successfully", routeRecord));
    }

    @PatchMapping("/{delivery_route_id}")
    public ResponseEntity<ApiResponseDto<UUID>> updateDeliveryRoute(
            @PathVariable("delivery_route_id") UUID deliveryRouteId,
            @RequestBody DeliveryRouteUpdateRequest request,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role
    ) {
        UUID routeRecordId = deliveryService.updateRouteRecord(deliveryRouteId, request.toDTO(), userId, role);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery route updated successfully", routeRecordId));
    }

    @PatchMapping("/{delivery_route_id}/status")
    public ResponseEntity<ApiResponseDto<UUID>> updateDeliveryRouteStatus(
            @PathVariable("delivery_route_id") UUID deliveryRouteId,
            @RequestParam("current_status") String currentStatus,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role
    ) {
        UUID routeRecordId = deliveryService.updateRouteRecordState(deliveryRouteId, currentStatus, userId, role);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery route status updated successfully", routeRecordId));
    }

    @PatchMapping("/{delivery_route_id}/delivery-managers")
    public ResponseEntity<ApiResponseDto<UUID>> updateDeliveryRouteStatus(
            @PathVariable("delivery_route_id") UUID deliveryRouteId,
            @RequestParam("delivery_manager_id") UUID deliveryManagerId,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role
    ) {
        UUID routeRecordId = deliveryRouteRecordService.updateRouteRecordDeliveryManager(deliveryRouteId, deliveryManagerId, userId, role);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery route status updated successfully", routeRecordId));
    }

    @DeleteMapping("/{delivery_route_id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteDeliveryRoute(
            @PathVariable("delivery_route_id") UUID deliveryRouteId,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role
    ) {
        deliveryRouteRecordService.deleteRouteRecord(deliveryRouteId, userId, role);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery route deleted successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<Page<DeliveryRouteRecordResponse>>> getAllCompanyRouteRecords(
            DeliveryRouteSearchRequest deliveryRouteSearchRequest,
            Pageable pageable,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role
    ) {
        Page<DeliveryRouteRecordResponse> routeRecords = deliveryRouteRecordService.getDeliveryRouteRecords(deliveryRouteSearchRequest, pageable, userId, role);

        return ResponseEntity.ok(ApiResponseDto.success("Delivery company route records retrieved successfully", routeRecords));
    }
}
