package com.tech_nova.delivery.presentation.controller;

import com.tech_nova.delivery.application.dto.res.DeliveryRouteRecordResponse;
import com.tech_nova.delivery.application.service.DeliveryRouteRecordService;
import com.tech_nova.delivery.application.service.DeliveryService;
import com.tech_nova.delivery.presentation.dto.ApiResponseDto;
import com.tech_nova.delivery.presentation.request.DeliveryRouteSearchRequest;
import com.tech_nova.delivery.presentation.request.DeliveryRouteUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "경로 기록 단건 조회",
            description = "마스터와 허브 관리자는 삭제 처리된 기록도 조회 가능")
    @GetMapping("/{delivery_route_id}")
    public ResponseEntity<ApiResponseDto<DeliveryRouteRecordResponse>> getDelivery(
            @PathVariable("delivery_route_id") UUID deliveryRouteId,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role
    ) {
        DeliveryRouteRecordResponse routeRecord = deliveryService.getDeliveryRouteRecord(deliveryRouteId, userId, role);
        return ResponseEntity.ok(ApiResponseDto.success("getDelivery route successfully", routeRecord));
    }

    @Operation(summary = "경로 기록 수정",
            description = "여러 데이터를 한번에 조작할 수 있는 위험으로 마스터만 권한 부여")
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

    @Operation(summary = "경로 기록 상태 수정")
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

    @Operation(summary = "경로 기록 배송 담당자 수정")
    @PatchMapping("/{delivery_route_id}/delivery-managers")
    public ResponseEntity<ApiResponseDto<UUID>> updateDeliveryRouteStatus(
            @PathVariable("delivery_route_id") UUID deliveryRouteId,
            @RequestParam("delivery_manager_id") UUID deliveryManagerId,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role
    ) {
        UUID routeRecordId = deliveryRouteRecordService.updateRouteRecordDeliveryManager(deliveryRouteId, deliveryManagerId, userId, role);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery route dlievery manager updated successfully", routeRecordId));
    }

    @Operation(summary = "경로 기록 삭제")
    @DeleteMapping("/{delivery_route_id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteDeliveryRoute(
            @PathVariable("delivery_route_id") UUID deliveryRouteId,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role
    ) {
        deliveryRouteRecordService.deleteRouteRecord(deliveryRouteId, userId, role);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery route deleted successfully"));
    }

    @Operation(summary = " 경로 기록 검색 조회")
    @GetMapping
    public ResponseEntity<ApiResponseDto<Page<DeliveryRouteRecordResponse>>> getAllCompanyRouteRecords(
            DeliveryRouteSearchRequest deliveryRouteSearchRequest,
            Pageable pageable,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role
    ) {
        Page<DeliveryRouteRecordResponse> routeRecords = deliveryRouteRecordService.getDeliveryRouteRecords(deliveryRouteSearchRequest, pageable, userId, role);

        return ResponseEntity.ok(ApiResponseDto.success("getDelivery routes successfully", routeRecords));
    }
}
