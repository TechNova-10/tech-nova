package com.tech_nova.auth.application.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserDto {
    private String username;
    private String password;
    private String role;
    private String slackId;

    public static UserDto create(
            String username,
            String password,
            String role,
            String slackId
    ) {
        return UserDto.builder()
                .username(username)
                .password(password)
                .role(role)
                .slackId(slackId)
                .build();
    }
}
