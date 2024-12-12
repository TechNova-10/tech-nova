package com.tech_nova.delivery.infrastructure;

import com.tech_nova.delivery.domain.model.delivery.DeliveryCompanyRouteRecord;
import com.tech_nova.delivery.domain.repository.DeliveryCompanyRouteRecordRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeliveryCompanyRouteRecordReadRepositoryImpl extends JpaRepository<DeliveryCompanyRouteRecord, UUID>, DeliveryCompanyRouteRecordRepository {
}

