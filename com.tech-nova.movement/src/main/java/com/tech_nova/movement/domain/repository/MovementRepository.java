package com.tech_nova.movement.domain.repository;

import com.tech_nova.movement.domain.model.Movement;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovementRepository extends JpaRepository<Movement, UUID> {

}
