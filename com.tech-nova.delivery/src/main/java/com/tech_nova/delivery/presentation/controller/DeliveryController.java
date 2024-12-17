package com.tech_nova.delivery.presentation.controller;

import com.tech_nova.delivery.application.dto.res.DeliveryResponse;
import com.tech_nova.delivery.application.service.DeliveryService;
import com.tech_nova.delivery.presentation.dto.ApiResponseDto;
import com.tech_nova.delivery.presentation.request.DeliveryAddressUpdateRequest;
import com.tech_nova.delivery.presentation.request.DeliveryRequest;
import com.tech_nova.delivery.presentation.request.DeliverySearchRequest;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @Operation(summary = "배송 생성",
            description = "X-Order-Origin 헤더 값이 있으면 주문 서비스에서 호출해 자동 실행된다고 간주,"
                    + "X-Order-Origin 헤더 값이 없이 API를 호출하면 마스터 권한이 있는지 검증")
    @PostMapping
    public ResponseEntity<ApiResponseDto<UUID>> createDelivery(
            @RequestBody DeliveryRequest request,
            @RequestHeader(value = "X-Order-Origin", required = false) String orderOriginToken,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role
    ) {
        UUID deliveryId = deliveryService.createDelivery(request.toDTO(), orderOriginToken);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery created successfully", deliveryId));
    }

    @Operation(summary = "배송 단건 조회")
    @GetMapping("/{delivery_id}")
    public ResponseEntity<ApiResponseDto<DeliveryResponse>> getDelivery(
            @PathVariable("delivery_id") UUID deliveryId,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role
    ) {
        DeliveryResponse delivery = deliveryService.getDelivery(deliveryId, userId, role);
        return ResponseEntity.ok(ApiResponseDto.success("getDelivery successfully", delivery));
    }

    @Operation(summary = "배송 수령인 변경")
    @PatchMapping("/{delivery_id}/recipient")
    public ResponseEntity<ApiResponseDto<UUID>> updateRecipient(
            @PathVariable("delivery_id") UUID deliveryId,
            @RequestParam String recipient,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role
    ) {
        deliveryService.updateRecipient(deliveryId, recipient, userId, role);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery recipient updated successfully", deliveryId));
    }

    @Operation(summary = "배송 주소 변경")
    @PatchMapping("/{delivery_id}/delivery_address")
    public ResponseEntity<ApiResponseDto<UUID>> updateDeliveryAddress(
            @PathVariable("delivery_id") UUID deliveryId,
            @RequestBody DeliveryAddressUpdateRequest recipient,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role
    ) {
        deliveryService.updateDeliveryAddress(deliveryId, recipient.toDTO(), userId, role);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery address updated successfully", deliveryId));
    }

    @Operation(summary = "배송 삭제")
    @DeleteMapping("/{delivery_id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteDelivery(
            @PathVariable("delivery_id") UUID deliveryId,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role
    ) {
        deliveryService.deleteDelivery(deliveryId, userId, role);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery deleted successfully"));
    }

    @Operation(summary = "배송 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponseDto<Page<DeliveryResponse>>> getAllDeliveries(
            DeliverySearchRequest deliverySearchRequest,
            Pageable pageable,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role
    ) {
        Page<DeliveryResponse> deliveries = deliveryService.getDeliveries(deliverySearchRequest, pageable, userId, role);

        return ResponseEntity.ok(ApiResponseDto.success("getAllDeliveries successfully", deliveries));
    }
}
