package com.tech_nova.delivery.presentation.controller;

import com.tech_nova.delivery.application.dto.res.DeliveryManagerResponse;
import com.tech_nova.delivery.application.service.DeliveryManagerService;
import com.tech_nova.delivery.presentation.dto.ApiResponseDto;
import com.tech_nova.delivery.presentation.request.DeliveryManagerRequest;
import com.tech_nova.delivery.presentation.request.DeliveryManagerSearchRequest;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/delivery-managers")
@RequiredArgsConstructor
public class DeliveryManagerController {
    private final DeliveryManagerService deliveryManagerService;

    @Operation(summary = "배송 담당자 생성")
    @PostMapping
    public ResponseEntity<ApiResponseDto<UUID>> createDeliveryManager(
            @RequestBody DeliveryManagerRequest request,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role
    ) {
        UUID deliveryManagerId = deliveryManagerService.createDeliveryManager(request.toDTO(), userId, role);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery Manager created successfully", deliveryManagerId));
    }

    @Operation(summary = "배송 담당자 조회")
    @GetMapping("/{delivery_manager_id}")
    public ResponseEntity<ApiResponseDto<DeliveryManagerResponse>> getDeliveryManager(
            @PathVariable("delivery_manager_id") UUID deliveryManagerId,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role
    ) {
        DeliveryManagerResponse deliveryManager = deliveryManagerService.getDeliveryManager(deliveryManagerId, userId, role);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery Manager created successfully", deliveryManager));
    }

    @Operation(summary = "배송 담당자 수정")
    @PatchMapping("/{delivery_manager_id}")
    public ResponseEntity<ApiResponseDto<Void>> updateDeliveryManager(
            @PathVariable("delivery_manager_id") UUID deliveryManagerId,
            @RequestBody DeliveryManagerRequest request,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role
    ) {
        deliveryManagerService.updateDeliveryManager(deliveryManagerId, request, userId, role);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery Manager created successfully"));
    }

    @Operation(summary = "배송 담당자 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponseDto<Page<DeliveryManagerResponse>>> getAllDeliveryManagers(
            DeliveryManagerSearchRequest deliveryManagerSearchRequest,
            Pageable pageable,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role
    ) {
        Page<DeliveryManagerResponse> routeRecords = deliveryManagerService.getDeliveryManagers(deliveryManagerSearchRequest, pageable, userId, role);

        return ResponseEntity.ok(ApiResponseDto.success("Delivery company route records retrieved successfully", routeRecords));
    }
}
