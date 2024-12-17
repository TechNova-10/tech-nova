package com.tech_nova.delivery.domain.service;

import com.tech_nova.delivery.domain.model.delivery.DeliveryCompanyRouteRecord;
import com.tech_nova.delivery.domain.model.delivery.DeliveryRouteRecord;
import com.tech_nova.delivery.domain.model.manager.DeliveryManager;
import com.tech_nova.delivery.domain.model.manager.DeliveryManagerRole;
import com.tech_nova.delivery.domain.repository.DeliveryManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DeliveryManagerAssignmentService {
    private final DeliveryManagerRepository deliveryManagerRepository;

    // 마지막 배정된 인덱스 (상태 저장용, 필요 시 DB나 Redis로 대체 가능)
    private static volatile int lastAssignedIndex = -1;

    @Transactional
    public DeliveryManager assignCompanyDeliveryManager() {
//        // 데이터 검증
//        if (companyRouteRecord == null) {
//            throw new IllegalArgumentException("Invalid company route record");
//        }

        // 업체 배송 담당자 조회
        List<DeliveryManager> companyManagers = deliveryManagerRepository.findAllByRoleAndIsDeletedFalse(DeliveryManagerRole.valueOf("COMPANY_DELIVERY_MANAGER"));

        if (companyManagers.isEmpty()) {
            throw new NoSuchElementException("가능한 업체 배송 담당자가 없습니다.");
        }

        // 순번 기준 정렬
        companyManagers.sort(Comparator.comparingInt(DeliveryManager::getDeliveryOrderSequence));

        // 순차적 배정 로직
        synchronized (this) {
            int totalManagers = companyManagers.size();
            lastAssignedIndex = (lastAssignedIndex + 1) % totalManagers; // 다음 순번으로 이동
            return companyManagers.get(lastAssignedIndex);
        }
    }

    @Transactional
    public Map<UUID, DeliveryManager> assignHubDeliveryManagers(List<DeliveryRouteRecord> routeRecords) {
        if (routeRecords == null || routeRecords.isEmpty()) {
            throw new IllegalArgumentException("Route records cannot be null or empty");
        }

        Map<UUID, DeliveryManager> assignedManagers = new HashMap<>();

        for (DeliveryRouteRecord routeRecord : routeRecords) {
            UUID hubId = routeRecord.getDepartureHubId();

            // 허브 배송 담당자 조회
            List<DeliveryManager> hubManagers = deliveryManagerRepository.findAllByRoleAndHubIdAndIsDeletedFalse(DeliveryManagerRole.valueOf("HUB_DELIVERY_MANAGER"), hubId);

            if (hubManagers.isEmpty()) {
                throw new NoSuchElementException("가능한 허브 배송 담당자가 없습니다." + hubId);
            }

            // 순번 기준 정렬
            hubManagers.sort(Comparator.comparingInt(DeliveryManager::getDeliveryOrderSequence));

            // 순차적 배정 로직
            synchronized (this) {
                int totalManagers = hubManagers.size();
                lastAssignedIndex = (lastAssignedIndex + 1) % totalManagers; // 다음 순번으로 이동
                DeliveryManager assignedManager = hubManagers.get(lastAssignedIndex);

                // 담당자 배정 및 결과 저장
                routeRecord.updateDeliveryManager(assignedManager);
                assignedManagers.put(hubId, assignedManager);
            }
        }

        return assignedManagers;
    }
}
