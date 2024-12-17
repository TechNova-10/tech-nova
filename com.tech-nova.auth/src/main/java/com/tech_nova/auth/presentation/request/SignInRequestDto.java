package com.tech_nova.auth.presentation.request;

import lombok.Getter;

@Getter
public class SignInRequestDto {
    private String username;
    private String password;
}
