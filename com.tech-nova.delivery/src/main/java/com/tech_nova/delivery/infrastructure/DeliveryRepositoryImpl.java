package com.tech_nova.delivery.infrastructure;

import com.tech_nova.delivery.domain.model.delivery.Delivery;
import com.tech_nova.delivery.domain.repository.DeliveryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeliveryRepositoryImpl extends JpaRepository<Delivery, UUID>, DeliveryRepository {
}

