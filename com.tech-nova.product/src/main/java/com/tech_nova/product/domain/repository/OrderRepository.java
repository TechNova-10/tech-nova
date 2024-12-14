package com.tech_nova.product.domain.repository;

import com.tech_nova.product.domain.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository <Order, UUID> {
    Page<Order> findByRequestingCompanyIdAndIsDeletedFalse(UUID companyId, Pageable pageable);
    Page<Order> findByReceivingCompanyIdAndIsDeletedFalse(UUID companyId, Pageable pageable);
    Page<Order> findByIsDeletedFalse(Pageable pageable);
}
