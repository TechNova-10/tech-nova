package com.tech_nova.delivery.application.service;

import com.tech_nova.delivery.application.dto.res.DeliveryRouteRecordResponse;
import com.tech_nova.delivery.domain.model.delivery.Delivery;
import com.tech_nova.delivery.domain.repository.DeliveryRouteRecordRepository;
import com.tech_nova.delivery.domain.repository.DeliveryRouteRecordRepositoryCustom;
import com.tech_nova.delivery.presentation.request.DeliveryRouteSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class DeliveryRouteRecordService {
    private final AuthService authService;

    private final DeliveryRouteRecordRepository deliveryRouteRecordRepository;
    private final DeliveryRouteRecordRepositoryCustom deliveryRouteRecordRepositoryCustom;

    @Transactional(readOnly = true)
    public Page<DeliveryRouteRecordResponse> getDeliveryRouteRecords(DeliveryRouteSearchRequest deliveryRouteSearchRequest, Pageable pageable) {

        int pageSize =
                (pageable.getPageSize() == 30
                        || pageable.getPageSize() == 50)
                        ? pageable.getPageSize() : 10;

        Pageable customPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageSize,
                pageable.getSort()
        );

        // TODO 권한 검증 추가
        return deliveryRouteRecordRepositoryCustom.searchDeliveryRouteRecords("MASTER", deliveryRouteSearchRequest, customPageable).map(DeliveryRouteRecordResponse::of);

    }

    private boolean validateMaster(String token, Delivery delivery) {
        UUID userId = authService.getUserId(token);
        String userRole = authService.getUserRole(token);

        return "HUB_MANAGER".equals(userRole);
    }
}
