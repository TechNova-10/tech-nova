package com.tech_nova.delivery.infrastructure.repository;

import com.tech_nova.delivery.domain.model.delivery.DeliveryCompanyRouteRecord;
import com.tech_nova.delivery.domain.repository.DeliveryCompanyRouteRecordRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface DeliveryCompanyRouteRecordReadRepositoryImpl extends JpaRepository<DeliveryCompanyRouteRecord, UUID>, DeliveryCompanyRouteRecordRepository {

    @Query("SELECT dcr FROM DeliveryCompanyRouteRecord dcr " +
            "WHERE dcr.isDeleted = false " +
            "AND dcr.currentStatus = 'COMPANY_WAITING' " +
            "ORDER BY dcr.deliveryManager.id, dcr.deliveryOrderSequence ASC")
    List<DeliveryCompanyRouteRecord> findAllByIsDeletedFalseGroupedByDeliveryManager();
}

