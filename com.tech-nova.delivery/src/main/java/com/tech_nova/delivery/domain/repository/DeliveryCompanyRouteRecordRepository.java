package com.tech_nova.delivery.domain.repository;

import com.tech_nova.delivery.domain.model.delivery.DeliveryCompanyRouteRecord;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeliveryCompanyRouteRecordRepository {
    Optional<DeliveryCompanyRouteRecord> findById(UUID id);

    Optional<DeliveryCompanyRouteRecord> findByIdAndIsDeletedFalse(UUID id);

    boolean existsByDeliveryIdAndIsDeletedFalse(UUID deliveryId);

    DeliveryCompanyRouteRecord findByDeliveryIdAndIsDeletedFalse(UUID deliveryId);
}
