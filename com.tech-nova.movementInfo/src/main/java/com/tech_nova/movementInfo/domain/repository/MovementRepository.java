package com.tech_nova.movementInfo.domain.repository;

import com.tech_nova.movementInfo.domain.model.Movement;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovementRepository extends JpaRepository<Movement, UUID> {
  Optional<Movement> findByMovementIdAndIsDeletedFalse(UUID hubId);
}
