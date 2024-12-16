package com.tech_nova.notificaion.presentation.controller;

import com.tech_nova.notificaion.application.dtos.req.NotificationRequestDto;
import com.tech_nova.notificaion.application.dtos.res.NotificationResponseDto;
import com.tech_nova.notificaion.application.service.NotificationService;
import com.tech_nova.notificaion.presentation.dto.ApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

  private final NotificationService notificationService;

  @PostMapping
  public ResponseEntity<ApiResponseDto<NotificationResponseDto>> createNotification(
      @RequestBody NotificationRequestDto notificationRequestDto
  ) {
    return new ResponseEntity<>(
        ApiResponseDto.<NotificationResponseDto>builder()
            .code(201)
            .message("알림 생성 완료")
            .data(notificationService.createNotification(notificationRequestDto.getRequest()))
            .build(),
        HttpStatus.CREATED
    );
  }
}
