// DeliveryRouteRecordRepositoryImpl.java
package com.tech_nova.delivery.infrastructure.repository;

import com.tech_nova.delivery.domain.model.delivery.DeliveryRouteRecord;
import com.tech_nova.delivery.domain.repository.DeliveryRouteRecordRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeliveryRouteRecordReadRepositoryImpl extends JpaRepository<DeliveryRouteRecord, UUID>, DeliveryRouteRecordRepository {
}
