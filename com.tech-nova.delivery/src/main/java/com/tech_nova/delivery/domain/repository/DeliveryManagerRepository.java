package com.tech_nova.delivery.domain.repository;

import com.tech_nova.delivery.domain.model.manager.DeliveryManager;
import com.tech_nova.delivery.domain.model.manager.DeliveryManagerRole;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeliveryManagerRepository {
    List<DeliveryManager> findAll();

    Optional<DeliveryManager> findById(UUID id);

    Optional<DeliveryManager> findByIdAndIsDeletedFalse(UUID id);

    boolean existsByManagerUserId(UUID managerUserId);

    DeliveryManager save(DeliveryManager deliveryManager);

    Integer findMaxDeliveryOrderByHubId(UUID assignedHubId);

    boolean existsByAssignedHubIdAndDeliveryOrderSequence(UUID assignedHubId, Integer deliveryOrderSequence);

    Optional<DeliveryManager> findFirstByRole(DeliveryManagerRole role);

    int countHubManagers(DeliveryManagerRole role);

    int countCompanyManagers(UUID assignedHubId, DeliveryManagerRole role);
}
