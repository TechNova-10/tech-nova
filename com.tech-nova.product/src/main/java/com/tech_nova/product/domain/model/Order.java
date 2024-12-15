package com.tech_nova.product.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name  = "p_order")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Order extends AuditField {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID orderId;

    @Column(nullable = false)
    private UUID requestingCompanyId;

    @Column(nullable = false)
    private UUID receivingCompanyId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String requestDetails;

    @Column
    private LocalDateTime deliveryDeadLine;

    @Column
    private LocalDateTime deliveryDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderProduct> orderProducts = new ArrayList<>();
}
