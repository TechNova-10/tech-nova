package com.tech_nova.delivery.infrastructure.client;

import com.tech_nova.delivery.application.service.UserService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "user-service", url = "http://user-service/api/v1/users")
public interface UserClient extends UserService {
    @PatchMapping("/{userId}/role/delivery")
    void setDeliveryRoleForUser(@PathVariable UUID userId);
}