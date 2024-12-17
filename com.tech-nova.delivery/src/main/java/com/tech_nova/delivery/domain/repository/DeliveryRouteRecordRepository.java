// DeliveryRouteRecordRepository.java
package com.tech_nova.delivery.domain.repository;

import com.tech_nova.delivery.domain.model.delivery.DeliveryRouteRecord;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeliveryRouteRecordRepository {
    Optional<DeliveryRouteRecord> findById(UUID id);

    Optional<DeliveryRouteRecord> findByIdAndIsDeletedFalse(UUID id);

    List<DeliveryRouteRecord> findByDeliveryIdAndIsDeletedFalse(UUID deliveryId);
}
