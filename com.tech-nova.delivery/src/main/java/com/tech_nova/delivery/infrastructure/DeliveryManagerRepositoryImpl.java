package com.tech_nova.delivery.infrastructure;

import com.tech_nova.delivery.domain.model.manager.DeliveryManager;
import com.tech_nova.delivery.domain.repository.DeliveryManagerRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface DeliveryManagerRepositoryImpl extends JpaRepository<DeliveryManager, UUID>, DeliveryManagerRepository {
    @Query("SELECT COALESCE(MAX(dm.deliveryOrderSequence), 0) FROM DeliveryManager dm WHERE dm.assignedHubId = :assignedHubId")
    Integer findMaxDeliveryOrderByHubId(UUID assignedHubId);

    boolean existsByAssignedHubIdAndDeliveryOrderSequence(UUID assignedHubId, Integer deliveryOrderSequence);
}

