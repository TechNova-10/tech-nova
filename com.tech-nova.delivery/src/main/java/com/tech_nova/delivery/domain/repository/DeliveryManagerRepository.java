package com.tech_nova.delivery.domain.repository;

import com.tech_nova.delivery.domain.model.manager.DeliveryManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeliveryManagerRepository {
    List<DeliveryManager> findAll();

    Optional<DeliveryManager> findById(UUID id);

    boolean existsByManagerUserId(UUID managerUserId);

    DeliveryManager save(DeliveryManager deliveryManager);

    Integer findMaxDeliveryOrderByHubId(UUID assignedHubId);

    boolean existsByAssignedHubIdAndDeliveryOrderSequence(UUID assignedHubId, Integer deliveryOrderSequence);
}
