package com.tech_nova.delivery.domain.repository;

import com.tech_nova.delivery.domain.model.delivery.Delivery;
import com.tech_nova.delivery.presentation.request.DeliverySearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DeliveryRepositoryCustom {
    Page<Delivery> searchDelivery(UUID userId, String role, DeliverySearchRequest searchRequest, Pageable pageable);

}
