package com.tech_nova.delivery.application.service;

import com.tech_nova.delivery.HubData;
import com.tech_nova.delivery.presentation.dto.ApiResponseDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

public interface HubService {
    ApiResponseDto<HubData> getHub(@PathVariable("hubId") UUID hubId, @RequestHeader(value = "role", required = false) String role);
}
