package com.tech_nova.auth.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "p_user")
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class User extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column
    private UserRole role;

    @Column
    private String slackId;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isDeleted;

    public static User create(
            String username,
            String password,
            UserRole role,
            String slackId
    ) {
        return User.builder()
                .username(username)
                .password(password)
                .role(role)
                .slackId(slackId)
                .build();
    }

    public void updateRole(UserRole role, UUID updatedBy) {
        this.role = role;
        markAsUpdated(updatedBy);
    }

    public void updateSlackId(String slackId, UUID updatedBy) {
        this.slackId = slackId;
        markAsUpdated(updatedBy);
    }

    public void markAsCreated(UUID createdBy) {
        super.markAsCreated(createdBy);
    }

    public void markAsDeleted(UUID deletedBy) {
        super.markAsDeleted(deletedBy);
        this.isDeleted = true;
    }

    public void markAsUpdated(UUID updatedBy) {
        super.markAsUpdated(updatedBy);
    }
}
