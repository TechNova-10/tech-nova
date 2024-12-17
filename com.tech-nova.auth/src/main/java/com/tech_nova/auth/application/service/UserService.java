package com.tech_nova.auth.application.service;

import com.tech_nova.auth.application.dto.res.UserResponse;
import com.tech_nova.auth.domain.model.User;
import com.tech_nova.auth.domain.model.UserRole;
import com.tech_nova.auth.domain.repository.UserRepository;
import com.tech_nova.auth.presentation.exception.AuthenticationException;
import com.tech_nova.auth.presentation.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserResponse getUser(UUID searchUserId, UUID userId, String role) {
        if (role == null) {
            throw new AuthenticationException("인증 오류: 역할 정보가 필요합니다.");
        }

        UserRole userRole = UserRole.valueOf(role.toUpperCase());
        if (!userRole.equals(UserRole.MASTER)) {
            if (!searchUserId.equals(userId)) {
                throw new ForbiddenException("사용자 본인만 자신의 정보를 조회할 수 있습니다.");
            }
        }

        User user = userRepository.findById(searchUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return UserResponse.of(user);
    }

    @Transactional
    public void updateUserRole(UUID searchUserId, String updateRole, UUID userId, String role) {
        if (!role.equals("MASTER") && !role.equals("HUB_MANAGER")) {
            throw new AuthenticationException("사용자 역할을 변경하는 권한이 없습니다.");
        }

        if (role.equals("HUB_MANAGER")) {
            if (!updateRole.equals("COMPANY_DELIVERY_MANAGER")) {
                throw new AuthenticationException("사용자 역할을 변경하는 권한이 없습니다.");
            }
        }

        User user = userRepository.findById(searchUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        user.updateRole(UserRole.valueOf(updateRole), userId);
    }

    @Transactional
    public void updateSlackId(UUID searchUserId, String slackId, UUID userId, String role) {
        UserRole userRole = UserRole.valueOf(role.toUpperCase());
        if (!userRole.equals(UserRole.MASTER)) {
            throw new ForbiddenException("마스터 관리자만 수정이 가능합니다.");
        }

        User user = userRepository.findById(searchUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        user.updateSlackId(slackId, userId);
    }

    @Transactional
    public void deleteUser(UUID searchUserId, UUID userId, String role) {
        UserRole userRole = UserRole.valueOf(role.toUpperCase());
        if (!userRole.equals(UserRole.MASTER)) {
            throw new ForbiddenException("마스터 관리자만 삭제가  가능합니다.");
        }

        User user = userRepository.findById(searchUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        user.markAsDeleted(userId);
    }
}
