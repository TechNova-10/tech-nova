package com.tech_nova.movementInfo.infrastructure.client;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "hub-service")
public interface HubClient {

  @GetMapping("/api/v1/hubs/clients")
  List<HubResponseDto> getHubList();
}
