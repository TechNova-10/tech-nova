package com.tech_nova.hub.presentation.exception.handler;

import com.tech_nova.hub.presentation.dto.ApiResponseDto;
import com.tech_nova.hub.presentation.exception.HubNotFoundException;
import com.tech_nova.hub.presentation.exception.MasterRoleRequiredException;
import com.tech_nova.hub.presentation.exception.RoleNotAllowedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MasterRoleRequiredException.class)
  public ResponseEntity<ApiResponseDto<Object>> handleMasterRoleRequiredException(Exception e) {
    return buildErrorResponse("권한이 없습니다.", -2, HttpStatus.FORBIDDEN, e.getMessage());
  }

  @ExceptionHandler(RoleNotAllowedException.class)
  public ResponseEntity<ApiResponseDto<Object>> handleRoleNotAllowedException(Exception e) {
    return buildErrorResponse("등록되지 않은 권한입니다.", -2, HttpStatus.FORBIDDEN, e.getMessage());
  }

  @ExceptionHandler(HubNotFoundException.class)
  public ResponseEntity<ApiResponseDto<Object>> handleHubNotFoundException(HubNotFoundException e) {
    return buildErrorResponse("해당 허브를 찾을 수 없습니다.", -1, HttpStatus.NOT_FOUND, e.getMessage());
  }

  private ResponseEntity<ApiResponseDto<Object>> buildErrorResponse(
      String message,
      int code,
      HttpStatus status,
      Object data
  ) {
    return new ResponseEntity<>(
        ApiResponseDto.builder()
            .code(code)
            .message(message)
            .data(data)
            .build(),
        status
    );
  }
}