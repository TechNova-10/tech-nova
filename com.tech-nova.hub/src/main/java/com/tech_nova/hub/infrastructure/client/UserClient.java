package com.tech_nova.hub.infrastructure.client;

import com.tech_nova.hub.infrastructure.client.dto.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserClient {

  @GetMapping("/users/{id}")
  UserResponseDto getUser(@PathVariable("id") Long id);
}
