package com.tech_nova.hub.presentation.exception.handler;

import com.tech_nova.hub.presentation.dto.ApiResponseDto;
import com.tech_nova.hub.presentation.exception.MasterRoleRequiredException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MasterRoleRequiredException.class)
  public ResponseEntity<ApiResponseDto<Object>> handleMasterRoleRequiredException(Exception e) {
    return new ResponseEntity<>(
        ApiResponseDto.builder()
            .code(-10)
            .message("권한이 없습니다.")
            .build(),
        HttpStatus.FORBIDDEN
    );
  }
}
