package com.tech_nova.delivery.presentation.controller;

import com.tech_nova.delivery.application.dto.res.DeliveryCompanyRouteRecordResponse;
import com.tech_nova.delivery.application.service.DeliveryCompanyRouteRecordService;
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
    private final DeliveryCompanyRouteRecordService deliveryCompanyRouteRecordService;

    @PostMapping
    public ResponseEntity<ApiResponseDto<Void>> createCompanyRouteRecord(@RequestParam("delivery_id") UUID deliveryId) {
        deliveryService.createCompanyRouteRecordByDeliveryId(deliveryId);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery route status created successfully"));
    }

    @GetMapping("/{delivery_route_id}")
    public ResponseEntity<ApiResponseDto<DeliveryCompanyRouteRecordResponse>> getDelivery(@PathVariable("delivery_route_id") UUID deliveryRouteId) {
        DeliveryCompanyRouteRecordResponse routeRecord = deliveryService.getDeliveryCompanyRouteRecord(deliveryRouteId);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery created successfully", routeRecord));
    }

    @PatchMapping("/{delivery_route_id}")
    public ResponseEntity<ApiResponseDto<UUID>> updateCompanyDeliveryRoute(@PathVariable("delivery_route_id") UUID deliveryRouteId, @RequestBody DeliveryCompanyRouteUpdateRequest request) {
        UUID routeRecordId = deliveryService.updateCompanyRouteRecord(deliveryRouteId, request.toDTO());
        return ResponseEntity.ok(ApiResponseDto.success("Delivery route updated successfully", routeRecordId));
    }

    @PatchMapping("/{delivery_route_id}/status")
    public ResponseEntity<ApiResponseDto<UUID>> updateCompanyDeliveryRouteStatus(@PathVariable("delivery_route_id") UUID deliveryRouteId, @RequestParam("current_status") String currentStatus) {
        UUID routeRecordId = deliveryService.updateCompanyRouteRecordState(deliveryRouteId, currentStatus);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery route status updated successfully", routeRecordId));
    }

    @PatchMapping("/{delivery_route_id}/delivery-managers")
    public ResponseEntity<ApiResponseDto<UUID>> updateCompanyDeliveryRouteStatus(@PathVariable("delivery_route_id") UUID deliveryRouteId, @RequestParam("delivery_manage_id") UUID deliveryManagerId) {
        UUID routeRecordId = deliveryService.updateCompanyRouteRecordDeliveryManager(deliveryRouteId, deliveryManagerId);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery route status updated successfully", routeRecordId));
    }

    @DeleteMapping("/{delivery_route_id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteCompanyDeliveryRoute(@PathVariable("delivery_route_id") UUID deliveryRouteId) {
        deliveryService.deleteCompanyRouteRecord(deliveryRouteId);
        return ResponseEntity.ok(ApiResponseDto.success("Delivery route deleted successfully"));
    }

    // @GetMapping
    // public ResponseEntity<ApiResponseDto<List<DeliveryCompanyRouteRecordResponse>>> getAllCompanyRouteRecords() {
    //     List<DeliveryCompanyRouteRecordResponse> routeRecords = deliveryCompanyRouteRecordService.getDeliveryCompanyList();
    //
    //     return ResponseEntity.ok(ApiResponseDto.success("Delivery company route records fetched successfully", routeRecords));
    // }

    // TODO 테스트 끝나면 삭제 에정. 이 기능은 스케줄로 자동 실행되기 때문.
    @PatchMapping("/order-sequence")
    public ResponseEntity<ApiResponseDto<Void>> setOrderSequence() {
        deliveryCompanyRouteRecordService.setOrderSequence();

        return ResponseEntity.ok(ApiResponseDto.success("Delivery company route records fetched successfully"));
    }

}
