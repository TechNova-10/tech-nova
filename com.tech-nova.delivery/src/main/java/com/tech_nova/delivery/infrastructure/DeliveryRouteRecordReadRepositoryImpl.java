package com.tech_nova.delivery.infrastructure;

import com.tech_nova.delivery.domain.model.delivery.DeliveryRouteRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeliveryRouteRecordReadRepositoryImpl extends JpaRepository<DeliveryRouteRecord, UUID> {

}