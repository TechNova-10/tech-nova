package com.tech_nova.delivery.presentation.controller;

import com.tech_nova.delivery.application.dto.res.DeliveryResponse;
import com.tech_nova.delivery.application.service.DeliveryService;
import com.tech_nova.delivery.presentation.dto.ApiResponseDto;
import com.tech_nova.delivery.presentation.request.DeliveryAddressUpdateRequest;
import com.tech_nova.delivery.presentation.request.DeliveryRequest;
import com.tech_nova.delivery.presentation.request.DeliverySearchRequest;
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

    @GetMapping("/{delivery_id}")
    public ResponseEntity<ApiResponseDto<DeliveryResponse>> getDelivery(
            @PathVariable("delivery_id") UUID deliveryId,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role
    ) {
        DeliveryResponse delivery = deliveryService.getDelivery(deliveryId, role);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery created successfully", delivery));
    }

    @PatchMapping("/{delivery_id}/recipient")
    public ResponseEntity<ApiResponseDto<UUID>> updateRecipient(
            @PathVariable("delivery_id") UUID deliveryId,
            @RequestParam String recipient,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role
    ) {
        deliveryService.updateRecipient(deliveryId, recipient, userId, role);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery route status updated successfully", deliveryId));
    }

    @PatchMapping("/{delivery_id}/delivery_address")
    public ResponseEntity<ApiResponseDto<UUID>> updateDeliveryAddress(
            @PathVariable("delivery_id") UUID deliveryId,
            @RequestBody DeliveryAddressUpdateRequest recipient,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role
    ) {
        deliveryService.updateDeliveryAddress(deliveryId, recipient.toDTO(), userId, role);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery route status updated successfully", deliveryId));
    }

    @DeleteMapping("/{delivery_id}")
    public ResponseEntity<ApiResponseDto<Void>> createDelivery(
            @PathVariable("delivery_id") UUID deliveryId,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role
    ) {
        deliveryService.deleteDelivery(deliveryId, userId, role);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery deleted successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<Page<DeliveryResponse>>> getAllCompanyRouteRecords(
            DeliverySearchRequest deliverySearchRequest,
            Pageable pageable,
            @RequestHeader(value = "X-User-Id", required = true) UUID userId,
            @RequestHeader(value = "X-Role", required = true) String role
    ) {
        Page<DeliveryResponse> routeRecords = deliveryService.getDeliverys(deliverySearchRequest, pageable, userId, role);

        return ResponseEntity.ok(ApiResponseDto.success("Delivery company route records retrieved successfully", routeRecords));
    }
}
