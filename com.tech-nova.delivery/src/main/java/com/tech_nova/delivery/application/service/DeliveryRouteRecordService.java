package com.tech_nova.delivery.application.service;

import com.tech_nova.delivery.application.dto.res.DeliveryRouteRecordResponse;
import com.tech_nova.delivery.domain.model.delivery.Delivery;
import com.tech_nova.delivery.domain.model.delivery.DeliveryRouteRecord;
import com.tech_nova.delivery.domain.model.delivery.DeliveryStatus;
import com.tech_nova.delivery.domain.model.manager.DeliveryManager;
import com.tech_nova.delivery.domain.model.manager.DeliveryManagerRole;
import com.tech_nova.delivery.domain.repository.DeliveryManagerRepository;
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

    private final DeliveryManagerRepository deliveryManagerRepository;
    private final DeliveryRouteRecordRepository deliveryRouteRecordRepository;
    private final DeliveryRouteRecordRepositoryCustom deliveryRouteRecordRepositoryCustom;

    @Transactional(readOnly = true)
    public Page<DeliveryRouteRecordResponse> getDeliveryRouteRecords(DeliveryRouteSearchRequest deliveryRouteSearchRequest, Pageable pageable, UUID userId, String role) {

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

    @Transactional
    public UUID updateRouteRecordDeliveryManager(UUID deliveryRouteId, UUID deliveryManagerId, UUID userId, String role) {
        DeliveryRouteRecord routeRecord = deliveryRouteRecordRepository.findById(deliveryRouteId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 경로를 찾을 수 없습니다."));

        Delivery delivery = routeRecord.getDelivery();

        if (delivery.getCurrentStatus().equals(DeliveryStatus.DELIVERY_COMPLETED)) {
            throw new IllegalArgumentException("완료된 배송은 배송 담당자를 변경할 수 없습니다.");
        }

        DeliveryManager deliveryManager = deliveryManagerRepository.findByIdAndIsDeletedFalse(deliveryManagerId)
                .orElseThrow(() -> new IllegalArgumentException("배송 담당자를 찾을 수 없습니다."));

        if (!deliveryManager.getManagerRole().equals(DeliveryManagerRole.HUB_DELIVERY_MANAGER)) {
            throw new IllegalArgumentException("업체 배송 담당자는 허브 배송에 배정할 수 없습니다.");
        }

        delivery.updateRouteRecordDeliveryManager(deliveryRouteId, deliveryManager, userId);

        return routeRecord.getId();
    }

    @Transactional
    public void deleteRouteRecord(UUID deliveryRouteId, UUID userId, String role) {
        DeliveryRouteRecord routeRecord = deliveryRouteRecordRepository.findById(deliveryRouteId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 경로를 찾을 수 없습니다."));

        Delivery delivery = routeRecord.getDelivery();
        delivery.deleteRouteRecordState(deliveryRouteId, userId);
    }
}
