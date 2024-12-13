package com.tech_nova.hub.infrastructure.repository;

import com.tech_nova.hub.domain.model.Hub;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HubJpaRepository extends JpaRepository<Hub, UUID> {

  Optional<Hub> findByHubIdAndIsDeletedFalse(UUID hubId);
}
