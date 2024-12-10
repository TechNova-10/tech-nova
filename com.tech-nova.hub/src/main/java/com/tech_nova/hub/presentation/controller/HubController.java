package com.tech_nova.hub.presentation.controller;

import com.tech_nova.hub.application.dtos.res.HubRequestDto;
import com.tech_nova.hub.application.service.HubService;
import com.tech_nova.hub.presentation.dto.ApiResponseDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hubs")
public class HubController {

  private final HubService hubService;

  @PostMapping
  public ResponseEntity<ApiResponseDto<Void>> createHub(
      @RequestBody HubRequestDto hubRequestDto,
      @RequestHeader(value = "userId", required = true) UUID userId,
      @RequestHeader(value = "role", required = true) String role
  ) {
    hubService.createHub(hubRequestDto, userId, role);
    return new ResponseEntity<>(
        ApiResponseDto.<Void>builder()
            .code(201)
            .message("허브 생성 완료")
            .build(),
        HttpStatus.CREATED
    );
  }
}
