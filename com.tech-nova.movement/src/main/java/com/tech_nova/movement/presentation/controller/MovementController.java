package com.tech_nova.movement.presentation.controller;

import com.tech_nova.movement.application.dtos.req.MovementRequestDto;
import com.tech_nova.movement.application.dtos.res.MovementResponseDto;
import com.tech_nova.movement.application.sevice.MovementService;
import com.tech_nova.movement.presentation.dto.ApiResponseDto;
import java.util.List;
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
  public ResponseEntity<ApiResponseDto<Void>> createMovement(
      @RequestBody MovementRequestDto movementRequestDto,
      @RequestHeader(value = "user_id", required = true) UUID userId,
      @RequestHeader(value = "role", required = true) String role
  ) {
    movementService.createMovement(movementRequestDto, userId, role);
    return new ResponseEntity<>(
        ApiResponseDto.<Void>builder()
            .code(201)
            .message("허브간 이동 정보 생성 완료")
            .build(),
        HttpStatus.CREATED
    );
  }
}
