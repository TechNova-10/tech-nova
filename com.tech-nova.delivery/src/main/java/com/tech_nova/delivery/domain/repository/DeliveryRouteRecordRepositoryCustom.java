package com.tech_nova.delivery.domain.repository;

import com.tech_nova.delivery.domain.model.delivery.DeliveryRouteRecord;
import com.tech_nova.delivery.presentation.request.DeliveryRouteSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryRouteRecordRepositoryCustom {
    Page<DeliveryRouteRecord> searchDeliveryRouteRecords(String role, DeliveryRouteSearchRequest searchRequest, Pageable pageable);

}
