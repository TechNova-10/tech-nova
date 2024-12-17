package com.tech_nova.company.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "p_company")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Company extends AuditField {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID companyId; // 업체 ID

    @Column(nullable = false)
    private UUID hubId; // 허브 ID

    @Column(nullable = false)
    private UUID hubManagerId; // 허브 관리자 ID

    @Column(nullable = false)
    private String name; // 업체명

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CompanyType type; // 업체 타입 (생산업체/수령업체)

    @Column(name = "province", nullable = false)
    private String province;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "district", nullable = false)
    private String district;

    @Column(name = "street", nullable = false)
    private String street;

}