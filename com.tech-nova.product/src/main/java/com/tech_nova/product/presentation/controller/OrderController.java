package com.tech_nova.product.presentation.controller;

import com.tech_nova.product.application.dto.OrderRequest;
import com.tech_nova.product.application.dto.OrderResponse;
import com.tech_nova.product.application.service.OrderService;
import com.tech_nova.product.presentation.dto.ApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    @PostMapping
    public ResponseEntity<ApiResponseDto<Void>> createOrder(@RequestBody OrderRequest request) {
        orderService.createOrder(request);
        return ResponseEntity.status(201).body(ApiResponseDto.success("주문 생성 완료", null));
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<ApiResponseDto<Void>> updateOrder(@PathVariable UUID orderId,
                                                            @RequestBody OrderRequest request) {
        orderService.updateOrder(orderId, request);
        return ResponseEntity.ok(ApiResponseDto.success("주문이 수정되었습니다."));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponseDto<Void>> deleteOrder(@PathVariable UUID orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok(ApiResponseDto.successDelete());
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponseDto<Void>> cancelOrder(@PathVariable UUID orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok(ApiResponseDto.success("주문이 취소되었습니다."));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponseDto<OrderResponse>> getOrderById(@PathVariable UUID orderId) {
        OrderResponse response = orderService.getOrderById(orderId);
        return ResponseEntity.ok(ApiResponseDto.success("주문 상세 조회 성공", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<Page<OrderResponse>>> searchAllOrders(Pageable pageable) {
        Page<OrderResponse> responses = orderService.searchAllOrders(pageable);
        return ResponseEntity.ok(ApiResponseDto.success("전체 주문 조회 성공", responses));
    }

    @GetMapping("/requesting/{companyId}")
    public ResponseEntity<ApiResponseDto<Page<OrderResponse>>> searchOrdersByRequestingCompany(@PathVariable UUID companyId,
                                                                                               Pageable pageable) {
        Page<OrderResponse> responses = orderService.searchOrdersByRequestingCompany(companyId, pageable);
        return ResponseEntity.ok(ApiResponseDto.success("요청 업체 기준 주문 조회 성공", responses));
    }

    @GetMapping("/receiving/{companyId}")
    public ResponseEntity<ApiResponseDto<Page<OrderResponse>>> searchOrdersByReceivingCompany(@PathVariable UUID companyId,
                                                                                              Pageable pageable) {
        Page<OrderResponse> responses = orderService.searchOrdersByReceivingCompany(companyId, pageable);
        return ResponseEntity.ok(ApiResponseDto.success("수령 업체 기준 주문 조회 성공", responses));
    }

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        throw new IllegalArgumentException("유효하지 않은 Authorization header");
    }
}
