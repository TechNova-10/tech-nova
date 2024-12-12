package com.tech_nova.product.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@MappedSuperclass
public abstract class AuditField {

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성 일시

    @Column
    private UUID createdBy; // 생성자 ID

    @UpdateTimestamp
    @Column
    private LocalDateTime updatedAt; // 수정 일시

    @Column
    private UUID updatedBy; // 수정자 ID

    @Column
    private LocalDateTime deletedAt; // 삭제 일시 (논리적 삭제)

    @Column
    private UUID deletedBy; // 삭제 처리자 ID

    @Column(nullable = false)
    private boolean isDeleted = false; // 논리적 삭제 여부

    // 소프트 삭제 처리
    public void softDelete(){ //(UUID userId) {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
//        this.deletedBy = userId;
    }

}