package com.tech_nova.delivery.presentation.controller;

import com.tech_nova.delivery.application.dto.res.DeliveryCompanyRouteRecordResponse;
import com.tech_nova.delivery.application.service.DeliveryCompanyRouteRecordService;
import com.tech_nova.delivery.application.service.DeliveryService;
import com.tech_nova.delivery.presentation.dto.ApiResponseDto;
import com.tech_nova.delivery.presentation.request.DeliveryCompanyRouteUpdateRequest;
import com.tech_nova.delivery.presentation.request.DeliveryRouteSearchRequest;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "업체 경로 기록 생성",
            description = "(허브) 경로 기록이 모두 완료되면 자동 생성되나 오류에 대비해 마스터만 권한 부여")
    @PostMapping
    public ResponseEntity<ApiResponseDto<Void>> createCompanyRouteRecord(
            @RequestParam("delivery_id") UUID deliveryId,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role
    ) {
        deliveryService.createCompanyRouteRecordByDeliveryId(deliveryId, userId, role);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery route status created successfully"));
    }

    @Operation(summary = "업체 경로 기록 단건 조회",
            description = "마스터와 허브 관리자는 삭제 처리된 기록도 조회 가능")
    @GetMapping("/{delivery_route_id}")
    public ResponseEntity<ApiResponseDto<DeliveryCompanyRouteRecordResponse>> getDelivery(
            @PathVariable("delivery_route_id") UUID deliveryRouteId,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role
    ) {
        DeliveryCompanyRouteRecordResponse routeRecord = deliveryCompanyRouteRecordService.getDeliveryCompanyRouteRecord(deliveryRouteId, userId, role);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery created successfully", routeRecord));
    }

    @Operation(summary = "업체 경로 기록 수정",
            description = "여러 데이터를 한번에 조작할 수 있는 위험으로 마스터만 권한 부여")
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

    @Operation(summary = "업체 경로 기록 상태 수정")
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

    @Operation(summary = "업체 경로 기록 배송 담당자 수정")
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

    @Operation(summary = "업체 경로 기록 삭제")
    @DeleteMapping("/{delivery_route_id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteCompanyDeliveryRoute(
            @PathVariable("delivery_route_id") UUID deliveryRouteId,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role
    ) {
        deliveryCompanyRouteRecordService.deleteCompanyRouteRecord(deliveryRouteId, userId, role);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery route deleted successfully"));
    }

    @Operation(summary = "업체 경로 기록 검색 조회")
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
