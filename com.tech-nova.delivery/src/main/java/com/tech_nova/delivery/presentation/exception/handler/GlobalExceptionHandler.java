package com.tech_nova.delivery.presentation.exception.handler;

import com.tech_nova.delivery.presentation.dto.ApiResponseDto;
import com.tech_nova.delivery.presentation.exception.DeliveryOrderSequenceAlreadyExistsException;
import com.tech_nova.delivery.presentation.exception.DuplicateDeliveryException;
import com.tech_nova.delivery.presentation.exception.HubDeliveryCompletedException;
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
                .statusCode(HttpStatus.BAD_REQUEST.value())  // You can change to HttpStatus.CONFLICT for business conflicts
                .statusMessage(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .data(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);  // Or HttpStatus.CONFLICT
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

    @ExceptionHandler(DeliveryOrderSequenceAlreadyExistsException.class)
    public ResponseEntity<ApiResponseDto<String>> handleDeliveryOrderSequenceAlreadyExistsException(DeliveryOrderSequenceAlreadyExistsException ex) {
        ApiResponseDto<String> response = ApiResponseDto.<String>builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .statusMessage(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .data(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateDeliveryException.class)
    public ResponseEntity<ApiResponseDto<String>> DuplicateDeliveryException(DuplicateDeliveryException ex) {
        ApiResponseDto<String> response = ApiResponseDto.<String>builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .statusMessage(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .data(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HubDeliveryCompletedException.class)
    public ResponseEntity<ApiResponseDto<String>> HubDeliveryCompletedException(HubDeliveryCompletedException ex) {
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
