package com.tech_nova.auth.application.service;


import com.tech_nova.auth.application.dto.res.UserResponse;
import com.tech_nova.auth.domain.model.User;
import com.tech_nova.auth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserResponse getUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return UserResponse.of(user);
    }

    // 권한 업데이트

    // password 업데이트

    // slackId 업데이트

    // 회원탈퇴 -> AuthController에
}
