package com.tech_nova.company.presentation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDto<T> {
    private int statusCode;
    private String statusMessage;
    private String message;
    private T data;

    public static <T> ApiResponseDto<T> success(String message) {
        return ApiResponseDto.<T>builder()
                .statusCode(HttpStatus.OK.value())
                .statusMessage(HttpStatus.OK.getReasonPhrase())
                .message(message)
                .build();
    }

    public static <T> ApiResponseDto<T> success(String message, T data) {
        return ApiResponseDto.<T>builder()
                .statusCode(HttpStatus.OK.value())
                .statusMessage(HttpStatus.OK.getReasonPhrase())
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponseDto<T> successDelete() {
        return ApiResponseDto.<T>builder()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .statusMessage(HttpStatus.NO_CONTENT.getReasonPhrase())
                .build();
    }
}
