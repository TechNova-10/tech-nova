package com.tech_nova.auth.application.dto.res;

import com.tech_nova.auth.domain.model.User;
import com.tech_nova.auth.domain.model.UserRole;
import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String username;
    private UserRole role;
    private String slackId;


    public static UserResponse of(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .slackId(user.getSlackId())
                .build();
    }
}
