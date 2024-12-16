package com.tech_nova.delivery.domain.repository;

import com.tech_nova.delivery.domain.model.manager.DeliveryManager;
import com.tech_nova.delivery.presentation.request.DeliveryManagerSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryManagerRepositoryCustom {
    Page<DeliveryManager> searchDeliveryManager(String role, DeliveryManagerSearchRequest searchRequest, Pageable pageable);

}
