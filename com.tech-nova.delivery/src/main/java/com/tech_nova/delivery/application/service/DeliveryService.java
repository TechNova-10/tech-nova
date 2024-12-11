package com.tech_nova.delivery.application.service;

import com.tech_nova.delivery.application.dto.DeliveryDto;
import com.tech_nova.delivery.application.dto.DeliveryResponse;
import com.tech_nova.delivery.application.dto.HubMovementData;
import com.tech_nova.delivery.domain.model.delivery.Delivery;
import com.tech_nova.delivery.domain.model.delivery.DeliveryHubStatus;
import com.tech_nova.delivery.domain.model.delivery.DeliveryRouteRecord;
import com.tech_nova.delivery.domain.model.delivery.DeliveryStatus;
import com.tech_nova.delivery.domain.model.manager.DeliveryManager;
import com.tech_nova.delivery.domain.model.manager.DeliveryManagerRole;
import com.tech_nova.delivery.domain.repository.DeliveryManagerRepository;
import com.tech_nova.delivery.domain.repository.DeliveryRepository;
import com.tech_nova.delivery.domain.service.DeliveryManagerAssignmentService;
import com.tech_nova.delivery.presentation.exception.DuplicateDeliveryException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryManagerRepository deliveryManagerRepository;
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
        if (role == DeliveryManagerRole.COMPANY_DELIVERY_MANAGER) {
            throw new IllegalArgumentException("허브 배송에는 허브 배송 담당자만 배정할 수 있습니다.");
        }
    }
}
