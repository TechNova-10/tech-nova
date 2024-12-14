package com.tech_nova.movementInfo.presentation.controller;

import com.tech_nova.movementInfo.application.dtos.req.MovementRequestDto;
import com.tech_nova.movementInfo.application.dtos.res.MovementResponseDto;
import com.tech_nova.movementInfo.application.service.MovementService;
import com.tech_nova.movementInfo.presentation.dto.ApiResponseDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/movements")
public class MovementController {

  private final MovementService movementService;

  @PostMapping
  public ResponseEntity<ApiResponseDto<MovementResponseDto>> createMovement(
      @RequestBody MovementRequestDto movementRequestDto,
      @RequestHeader(value = "user_id", required = true) UUID userId,
      @RequestHeader(value = "role", required = true) String role
  ) {
    return new ResponseEntity<>(
        ApiResponseDto.<MovementResponseDto>builder()
            .code(201)
            .message("허브간 이동 정보 생성 완료")
            .data(movementService.createMovement(movementRequestDto, userId, role))
            .build(),
        HttpStatus.CREATED
    );
  }

  @GetMapping("/{movementId}")
  public ResponseEntity<ApiResponseDto<MovementResponseDto>> getMovement(
      @PathVariable UUID movementId
  ) {
    return new ResponseEntity<>(
        ApiResponseDto.<MovementResponseDto>builder()
            .code(200)
            .message("허브간 이동 정보 단일 조회 완료")
            .data(movementService.getMovement(movementId))
            .build(),
        HttpStatus.OK
    );
  }
}
