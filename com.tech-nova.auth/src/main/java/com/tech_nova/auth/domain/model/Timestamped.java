package com.tech_nova.auth.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Timestamped {
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column(updatable = false)
    private UUID createdBy;

    @LastModifiedDate
    @Column
    private LocalDateTime updateAt;

    @Column
    private UUID updatedBy;

    @Column
    private LocalDateTime deletedAt;

    @Column
    private UUID deletedBy;

    public void markAsCreated(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public void markAsUpdated(UUID updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void markAsDeleted(UUID deletedBy) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }
}
