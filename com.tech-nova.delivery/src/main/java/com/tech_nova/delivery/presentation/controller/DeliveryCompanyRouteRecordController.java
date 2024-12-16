package com.tech_nova.delivery.presentation.controller;

import com.tech_nova.delivery.application.dto.res.DeliveryCompanyRouteRecordResponse;
import com.tech_nova.delivery.application.service.DeliveryCompanyRouteRecordService;
import com.tech_nova.delivery.application.service.DeliveryService;
import com.tech_nova.delivery.presentation.dto.ApiResponseDto;
import com.tech_nova.delivery.presentation.request.DeliveryCompanyRouteUpdateRequest;
import com.tech_nova.delivery.presentation.request.DeliveryRouteSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/deliveries/companies/routes")
@RequiredArgsConstructor
public class DeliveryCompanyRouteRecordController {

    private final DeliveryService deliveryService;
    private final DeliveryCompanyRouteRecordService deliveryCompanyRouteRecordService;

    @PostMapping
    public ResponseEntity<ApiResponseDto<Void>> createCompanyRouteRecord(
            @RequestParam("delivery_id") UUID deliveryId,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role
    ) {
        deliveryService.createCompanyRouteRecordByDeliveryId(deliveryId, userId, role);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery route status created successfully"));
    }

    @GetMapping("/{delivery_route_id}")
    public ResponseEntity<ApiResponseDto<DeliveryCompanyRouteRecordResponse>> getDelivery(
            @PathVariable("delivery_route_id") UUID deliveryRouteId,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role
    ) {
        DeliveryCompanyRouteRecordResponse routeRecord = deliveryCompanyRouteRecordService.getDeliveryCompanyRouteRecord(deliveryRouteId, userId, role);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery created successfully", routeRecord));
    }

    @PatchMapping("/{delivery_route_id}")
    public ResponseEntity<ApiResponseDto<UUID>> updateCompanyDeliveryRoute(
            @PathVariable("delivery_route_id") UUID deliveryRouteId,
            @RequestBody DeliveryCompanyRouteUpdateRequest request,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role
    ) {
        UUID routeRecordId = deliveryCompanyRouteRecordService.updateCompanyRouteRecord(deliveryRouteId, request.toDTO(), userId, role);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery route updated successfully", routeRecordId));
    }

    @PatchMapping("/{delivery_route_id}/status")
    public ResponseEntity<ApiResponseDto<UUID>> updateCompanyDeliveryRouteStatus(
            @PathVariable("delivery_route_id") UUID deliveryRouteId,
            @RequestParam("current_status") String currentStatus,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role
    ) {
        UUID routeRecordId = deliveryCompanyRouteRecordService.updateCompanyRouteRecordState(deliveryRouteId, currentStatus, userId, role);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery route status updated successfully", routeRecordId));
    }

    @PatchMapping("/{delivery_route_id}/delivery-managers")
    public ResponseEntity<ApiResponseDto<UUID>> updateCompanyDeliveryRouteStatus(
            @PathVariable("delivery_route_id") UUID deliveryRouteId,
            @RequestParam("delivery_manage_id") UUID deliveryManagerId,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role
    ) {
        UUID routeRecordId = deliveryCompanyRouteRecordService.updateCompanyRouteRecordDeliveryManager(deliveryRouteId, deliveryManagerId, userId, role);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery route status updated successfully", routeRecordId));
    }

    @DeleteMapping("/{delivery_route_id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteCompanyDeliveryRoute(
            @PathVariable("delivery_route_id") UUID deliveryRouteId,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role
    ) {
        deliveryCompanyRouteRecordService.deleteCompanyRouteRecord(deliveryRouteId, userId, role);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery route deleted successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<Page<DeliveryCompanyRouteRecordResponse>>> getAllCompanyRouteRecords(
            DeliveryRouteSearchRequest deliveryRouteSearchRequest,
            Pageable pageable,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role
    ) {
        Page<DeliveryCompanyRouteRecordResponse> routeRecords = deliveryCompanyRouteRecordService.getDeliveryCompanyRouteRecords(deliveryRouteSearchRequest, pageable, userId, role);

        return ResponseEntity.ok(ApiResponseDto.success("Delivery company route records retrieved successfully", routeRecords));
    }

}
