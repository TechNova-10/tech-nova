package com.tech_nova.delivery.infrastructure.repository;

import com.tech_nova.delivery.domain.model.manager.DeliveryManager;
import com.tech_nova.delivery.domain.model.manager.DeliveryManagerRole;
import com.tech_nova.delivery.domain.repository.DeliveryManagerRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryManagerRepositoryImpl extends JpaRepository<DeliveryManager, UUID>, DeliveryManagerRepository {
    @Query("SELECT COALESCE(MAX(dm.deliveryOrderSequence), 0) FROM DeliveryManager dm WHERE dm.assignedHubId = :assignedHubId")
    Integer findMaxDeliveryOrderByHubId(UUID assignedHubId);

    @Query("SELECT dm FROM DeliveryManager dm WHERE dm.managerRole = :role ORDER BY dm.deliveryOrderSequence ASC limit 1")
    Optional<DeliveryManager> findFirstByRole(DeliveryManagerRole role);

    @Query("SELECT COUNT(dm) FROM DeliveryManager dm WHERE dm.managerRole = :role AND dm.assignedHubId IS NULL AND dm.isDeleted = false")
    int countHubManagers(DeliveryManagerRole role);

    @Query("SELECT COUNT(dm) FROM DeliveryManager dm WHERE dm.assignedHubId = :assignedHubId AND dm.managerRole = :role AND dm.isDeleted = false")
    int countCompanyManagers(UUID assignedHubId, DeliveryManagerRole role);

}

