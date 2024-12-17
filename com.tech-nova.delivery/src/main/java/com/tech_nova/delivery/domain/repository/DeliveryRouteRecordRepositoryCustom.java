package com.tech_nova.delivery.domain.repository;

import com.tech_nova.delivery.domain.model.delivery.DeliveryRouteRecord;
import com.tech_nova.delivery.presentation.request.DeliveryRouteSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DeliveryRouteRecordRepositoryCustom {
    Page<DeliveryRouteRecord> searchDeliveryRouteRecords(UUID userId, String role, DeliveryRouteSearchRequest searchRequest, Pageable pageable);

}
