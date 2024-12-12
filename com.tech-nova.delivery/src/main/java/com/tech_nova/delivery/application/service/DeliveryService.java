package com.tech_nova.delivery.application.service;

import com.tech_nova.delivery.application.dto.DeliveryCompanyRouteRecordUpdateDto;
import com.tech_nova.delivery.application.dto.DeliveryDto;
import com.tech_nova.delivery.application.dto.HubMovementData;
import com.tech_nova.delivery.application.dto.res.DeliveryResponse;
import com.tech_nova.delivery.domain.model.delivery.*;
import com.tech_nova.delivery.domain.model.manager.DeliveryManager;
import com.tech_nova.delivery.domain.model.manager.DeliveryManagerRole;
import com.tech_nova.delivery.domain.repository.DeliveryCompanyRouteRecordRepository;
import com.tech_nova.delivery.domain.repository.DeliveryManagerRepository;
import com.tech_nova.delivery.domain.repository.DeliveryRepository;
import com.tech_nova.delivery.domain.repository.DeliveryRouteRecordRepository;
import com.tech_nova.delivery.domain.service.DeliveryManagerAssignmentService;
import com.tech_nova.delivery.presentation.exception.DuplicateDeliveryException;
import com.tech_nova.delivery.presentation.exception.HubDeliveryCompletedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryManagerRepository deliveryManagerRepository;
    private final DeliveryRouteRecordRepository deliveryRouteRecordRepository;
    private final DeliveryCompanyRouteRecordRepository deliveryCompanyRouteRecordRepository;

    private final DeliveryManagerAssignmentService deliveryManagerAssignmentService;

    @Transactional
    public UUID createDelivery(DeliveryDto request) {
        if (deliveryRepository.existsByOrderId(request.getOrderId())) {
            throw new DuplicateDeliveryException("해당 주문은 이미 배송이 등록되어 있습니다.");
        }

        // 추후 hubService 이용해 List<HubMovementData> 가져오기
        List<HubMovementData> hubMovementDatas = createHubMovementDataList();

        Delivery delivery = Delivery.create(
                request.getOrderId(),
                hubMovementDatas.get(0).getDepartureHubId(),
                hubMovementDatas.get(hubMovementDatas.size() - 1).getArrivalHubId(),
                DeliveryStatus.HUB_WAITING,
                request.getRecipientCompanyId(),
                request.getProvince(),
                request.getCity(),
                request.getDistrict(),
                request.getRoadName(),
                new ArrayList<>()
        );

        // 담당자 배정
        // 생성 테스트 위해 임시로 첫번째 허브 배송 담당자 데이터 1명만 이용
        // 추후 담당자 배정 로직 구현 후 수정 필요
        DeliveryManager deliveryManager = deliveryManagerRepository.findFirstByRole(DeliveryManagerRole.HUB_DELIVERY_MANAGER).get();

        validateRoleForHubAssignment(deliveryManager);

        for (int i = 0; i < hubMovementDatas.size(); i++) {
            HubMovementData hubMovementData = hubMovementDatas.get(i);
            DeliveryRouteRecord routeRecord = DeliveryRouteRecord.create(
                    deliveryManager,
                    hubMovementData.getDepartureHubId(),
                    hubMovementData.getArrivalHubId(),
                    i + 1,
                    DeliveryHubStatus.HUB_WAITING,
                    hubMovementData.getDistance(),
                    hubMovementData.getTimeTravel()
            );

            routeRecord.connectDelivery(delivery);

            delivery.addRouteRecord(routeRecord);
        }

        deliveryRepository.save(delivery);

        return DeliveryResponse.of(delivery).getId();
    }

    @Transactional
    public UUID updateRouteRecordState(UUID deliveryRouteId, String updateStatus) {
        DeliveryRouteRecord routeRecord = deliveryRouteRecordRepository.findById(deliveryRouteId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 경로를 찾을 수 없습니다."));

        Delivery delivery = routeRecord.getDelivery();

        DeliveryRouteRecord lastSequenceRouteRecord = delivery.getRouteRecords().stream()
                .max(Comparator.comparingInt(DeliveryRouteRecord::getSequence))
                .orElseThrow(() -> new IllegalArgumentException("배송 경로 레코드가 없습니다."));

        DeliveryHubStatus newStatus = DeliveryHubStatus.valueOf(updateStatus);
        validatePreviousDeliveryArrivalStatus(delivery.getRouteRecords(), deliveryRouteId, newStatus);
        if (newStatus == DeliveryHubStatus.HUB_ARRIVE) {
            DeliveryCompanyRouteRecord companyRouteRecord = deliveryCompanyRouteRecordRepository.findByDeliveryIdAndIsDeletedFalse(delivery.getId());

            if (companyRouteRecord != null && companyRouteRecord.getCurrentStatus() != DeliveryCompanyStatus.COMPANY_WAITING) {
                if (companyRouteRecord.getCurrentStatus() == DeliveryCompanyStatus.COMPANY_MOVING) {
                    throw new HubDeliveryCompletedException("현재 업체 배송이 시작되어 상태 수정이 불가능합니다.");
                } else if (companyRouteRecord.getCurrentStatus() == DeliveryCompanyStatus.DELIVERY_COMPLETED) {
                    throw new HubDeliveryCompletedException("현재 업체 배송이 완료되어 상태 수정이 불가능합니다.");
                }
            }

            if (lastSequenceRouteRecord.getId().equals(routeRecord.getId())) {
                createCompanyRouteRecord(delivery);
            }
        }

        delivery.updateRouteRecordState(deliveryRouteId, newStatus);

        return routeRecord.getId();
    }

    @Transactional
    public UUID updateCompanyRouteRecord(UUID deliveryRouteId, DeliveryCompanyRouteRecordUpdateDto request) {
        DeliveryCompanyRouteRecord routeRecord = deliveryCompanyRouteRecordRepository.findById(deliveryRouteId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 경로를 찾을 수 없습니다."));

        Delivery delivery = routeRecord.getDelivery();

        DeliveryManager deliveryManager = null;
        if (request.getDeliveryManagerId() != null) {
            deliveryManager = deliveryManagerRepository.findById(request.getDeliveryManagerId())
                    .orElseThrow(() -> new IllegalArgumentException("업체 배송 담당자를 찾을 수 없습니다."));
        }

        DeliveryCompanyStatus newStatus = null;
        if (request.getCurrentStatus() != null) {
            newStatus = DeliveryCompanyStatus.valueOf(request.getCurrentStatus());
        }

        delivery.updateCompanyRouteRecord(deliveryRouteId, deliveryManager, newStatus, request.getDeliveryOrderSequence(), request.getRealDistance(), request.getRealTime());

        return routeRecord.getId();
    }

    @Transactional
    public UUID updateCompanyRouteRecordState(UUID deliveryRouteId, String updateStatus) {
        DeliveryCompanyRouteRecord routeRecord = deliveryCompanyRouteRecordRepository.findById(deliveryRouteId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 경로를 찾을 수 없습니다."));

        Delivery delivery = routeRecord.getDelivery();

        DeliveryCompanyStatus newStatus = DeliveryCompanyStatus.valueOf(updateStatus);
        delivery.updateCompanyRouteRecordState(deliveryRouteId, newStatus);

        return routeRecord.getId();
    }

    @Transactional
    public void createCompanyRouteRecordByDeliveryId(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow();

        createCompanyRouteRecord(delivery);
    }

    @Transactional
    public void createCompanyRouteRecord(Delivery delivery) {
        if (delivery == null) {
            throw new IllegalArgumentException("배송 객체가 필요합니다.");
        }

        if (deliveryCompanyRouteRecordRepository.existsByDeliveryIdAndIsDeletedFalse(delivery.getId())) {
            throw new IllegalStateException("이미 해당 배송에 대한 업체 경로 레코드가 생성되었습니다. 배송 ID: " + delivery.getId());
        }

        DeliveryRouteRecord lastSequenceRouteRecord = delivery.getRouteRecords().stream()
                .max(Comparator.comparingInt(DeliveryRouteRecord::getSequence))
                .orElseThrow(() -> new IllegalArgumentException("배송 경로 레코드가 없습니다."));

        if (lastSequenceRouteRecord.getCurrentStatus() != DeliveryHubStatus.HUB_ARRIVE) {
            throw new IllegalStateException("현재 최종 허브에 도착하지 않았습니다.");
        }

        // 업체 담당자 배정
        // 생성 테스트 위해 임시로 첫 번째 업체 배송 담당자 데이터 1명만 이용
        // 추후 담당자 배정 로직 구현 후 수정 필요
        DeliveryManager deliveryManager = deliveryManagerRepository.findFirstByRole(DeliveryManagerRole.COMPANY_DELIVERY_MANAGER)
                .orElseThrow(() -> new IllegalArgumentException("업체 배송 담당자를 찾을 수 없습니다."));

        validateRoleForCompanyAssignment(deliveryManager);

        // 마지막 허브 -> 업체의 예상 거리, 시간 계산 필요
        DeliveryCompanyRouteRecord companyRouteRecord = DeliveryCompanyRouteRecord.create(
                delivery,
                deliveryManager,
                delivery.getArrivalHubId(),
                delivery.getRecipientCompanyId(),
                DeliveryCompanyStatus.COMPANY_WAITING,
                1,
                (double) 0,
                "1h"
        );

        delivery.addCompanyRouteRecord(companyRouteRecord);
    }

    @Transactional
    public void deleteCompanyRouteRecord(UUID deliveryRouteId) {
        DeliveryCompanyRouteRecord routeRecord = deliveryCompanyRouteRecordRepository.findById(deliveryRouteId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 경로를 찾을 수 없습니다."));

        Delivery delivery = routeRecord.getDelivery();
        delivery.deleteCompanyRouteRecordState(deliveryRouteId);
    }


    private List<HubMovementData> createHubMovementDataList() {
        UUID hubId1 = UUID.randomUUID();
        UUID hubId2 = UUID.randomUUID();
        UUID hubId3 = UUID.randomUUID();

        // 임시 데이터
        List<HubMovementData> hubMovementDatas = new ArrayList<>();
        hubMovementDatas.add(new HubMovementData(UUID.randomUUID(), hubId1, hubId2, "1h", 100.0));
        hubMovementDatas.add(new HubMovementData(UUID.randomUUID(), hubId2, hubId3, "2h", 200.0));

        return hubMovementDatas;
    }

    private void validateRoleForHubAssignment(DeliveryManager deliveryManager) {
        DeliveryManagerRole role = deliveryManager.getManagerRole();
        if (role != DeliveryManagerRole.HUB_DELIVERY_MANAGER) {
            throw new IllegalArgumentException("허브 배송에는 허브 배송 담당자만 배정할 수 있습니다.");
        }
    }

    private void validateRoleForCompanyAssignment(DeliveryManager deliveryManager) {
        DeliveryManagerRole role = deliveryManager.getManagerRole();
        if (role != DeliveryManagerRole.COMPANY_DELIVERY_MANAGER) {
            throw new IllegalArgumentException("업체 배송 담당자만 배정할 수 있습니다.");
        }
    }

    private void validatePreviousDeliveryArrivalStatus(List<DeliveryRouteRecord> routeRecords, UUID deliveryRouteId, DeliveryHubStatus newStatus) {
        routeRecords.sort(Comparator.comparingInt(DeliveryRouteRecord::getSequence));

        DeliveryRouteRecord currentRecord = routeRecords.stream()
                .filter(routeRecord -> routeRecord.getId().equals(deliveryRouteId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 경로를 찾을 수 없습니다. ID: " + deliveryRouteId));

        int currentSequence = currentRecord.getSequence();

        if (currentSequence == 1) {
            return;
        }

        DeliveryRouteRecord previousRecord = routeRecords.stream()
                .filter(routeRecord -> routeRecord.getSequence() == currentSequence - 1)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("이전 배송 경로를 찾을 수 없습니다. 현재 Sequence: " + currentSequence));

        if (newStatus != DeliveryHubStatus.HUB_WAITING && previousRecord.getCurrentStatus() != DeliveryHubStatus.HUB_ARRIVE) {
            throw new IllegalStateException("이전 허브의 배송이 완료되지 않았습니다. 이전 경로 ID: " + previousRecord.getId());
        }
    }
}
