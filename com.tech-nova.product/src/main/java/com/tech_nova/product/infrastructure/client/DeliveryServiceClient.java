package com.tech_nova.product.infrastructure.client;

import com.tech_nova.product.infrastructure.dto.DeliveryRequest;
import com.tech_nova.product.presentation.dto.ApiResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "delivery-service", path = "/api/v1/deliveries")
public interface DeliveryServiceClient {
    @PostMapping
    ApiResponseDto<UUID> createDelivery(
            @RequestBody DeliveryRequest request,
            @RequestHeader(value = "X-Order-Origin", required = false) String orderOriginToken,
            @RequestHeader(value = "X-User-Id") UUID userId,
            @RequestHeader(value = "X-Role") String role
    );
}
