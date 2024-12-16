package com.tech_nova.auth.presentation.request;

import com.tech_nova.auth.application.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserRequest {
    private String username;
    private String password;
    private String role;
    private String slackId;

    public UserDto toDTO() {
        return UserDto.create(
                this.username,
                this.password,
                this.role,
                this.slackId
        );
    }
}
