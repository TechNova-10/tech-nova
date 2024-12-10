package com.tech_nova.hub.domain.repository;

import com.tech_nova.hub.domain.model.Hub;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HubRepository extends JpaRepository<Hub, UUID> {

}
