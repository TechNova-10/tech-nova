package com.tech_nova.auth.presentation.exception.handler;


import com.tech_nova.auth.presentation.dto.ApiResponseDto;
import com.tech_nova.auth.presentation.exception.AuthenticationException;
import com.tech_nova.auth.presentation.exception.ForbiddenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseDto<String>> illegalArgumentExceptionHandler(IllegalArgumentException ex) {
        ApiResponseDto<String> response = ApiResponseDto.<String>builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .statusMessage(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .data(null)
                .build();
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponseDto<String>> handleIllegalStateException(IllegalStateException ex) {
        ApiResponseDto<String> response = ApiResponseDto.<String>builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .statusMessage(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .data(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponseDto<String>> nullPointerExceptionHandler(NullPointerException ex) {
        ApiResponseDto<String> response = ApiResponseDto.<String>builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .statusMessage(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .data(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponseDto<String>> handleAuthenticationException(AuthenticationException ex) {
        ApiResponseDto<String> response = ApiResponseDto.<String>builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .statusMessage(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .data(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponseDto<String>> handleForbiddenException(ForbiddenException ex) {
        ApiResponseDto<String> response = ApiResponseDto.<String>builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .statusMessage(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .data(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<String>> handleGenericException(Exception ex) {
        ApiResponseDto<String> response = ApiResponseDto.<String>builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .statusMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("서버 오류: " + ex.getMessage())
                .data(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
