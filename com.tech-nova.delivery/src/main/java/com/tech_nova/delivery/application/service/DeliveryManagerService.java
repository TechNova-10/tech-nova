package com.tech_nova.delivery.application.service;

import com.tech_nova.delivery.application.dto.DeliveryManagerDto;
import com.tech_nova.delivery.application.dto.res.DeliveryManagerResponse;
import com.tech_nova.delivery.domain.model.manager.DeliveryManager;
import com.tech_nova.delivery.domain.model.manager.DeliveryManagerRole;
import com.tech_nova.delivery.domain.repository.DeliveryManagerRepository;
import com.tech_nova.delivery.presentation.exception.DeliveryOrderSequenceAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryManagerService {

    private final DeliveryManagerRepository deliveryManagerRepository;

    @Transactional
    public UUID createDeliveryManager(DeliveryManagerDto request) {
        if (deliveryManagerRepository.existsByManagerUserId(request.getManagerUserId())) {
            throw new IllegalArgumentException("해당 사용자는 이미 등록되어 있습니다.");
        }

        validateRoleAndHubAssignment(request);

        validateDeliveryManagerLimits(request);

        Integer deliveryOrderSequence = request.getDeliveryOrderSequence();

        if (deliveryOrderSequence == null) {
            deliveryOrderSequence = deliveryManagerRepository.findMaxDeliveryOrderByHubId(request.getAssignedHubId()) + 1;
        } else if (deliveryOrderSequence <= 0) {
            throw new IllegalArgumentException("배송 순서 번호는 0보다 커야 합니다.");
        } else {
            checkDeliveryOrderSequenceExists(request.getAssignedHubId(), deliveryOrderSequence);
        }

        DeliveryManager deliveryManager = DeliveryManager.create(
                request.getAssignedHubId(),
                request.getManagerUserId(),
                request.getManagerRole(),
                deliveryOrderSequence
        );

        deliveryManagerRepository.save(deliveryManager);

        return DeliveryManagerResponse.of(deliveryManager).getId();
    }

    public DeliveryManagerResponse getDeliveryManager(UUID deliveryManagerId) {
        DeliveryManager manager = deliveryManagerRepository.findByIdAndIsDeletedFalse(deliveryManagerId)
                .orElseThrow(() -> new IllegalArgumentException("배송 담당자를 찾을 수 없습니다."));

        return DeliveryManagerResponse.of(manager);
    }

    private void checkDeliveryOrderSequenceExists(UUID assignedHubId, Integer deliveryOrderSequence) {
        if (deliveryManagerRepository.existsByAssignedHubIdAndDeliveryOrderSequence(assignedHubId, deliveryOrderSequence)) {
            throw new DeliveryOrderSequenceAlreadyExistsException("해당 순번은 이미 존재합니다.");
        }
    }

    private void validateRoleAndHubAssignment(DeliveryManagerDto request) {
        DeliveryManagerRole role = DeliveryManagerRole.fromString(request.getManagerRole());
        if (role == DeliveryManagerRole.HUB_DELIVERY_MANAGER) {
            if (request.getAssignedHubId() != null) {
                throw new IllegalArgumentException("허브 배송 담당자는 허브에 소속될 수 없습니다.");
            }
        } else if (role == DeliveryManagerRole.COMPANY_DELIVERY_MANAGER) {
            if (request.getAssignedHubId() == null) {
                throw new IllegalArgumentException("업체 배송 담당자는 반드시 허브에 소속되어야 합니다.");
            }
        }
    }

    private void validateDeliveryManagerLimits(DeliveryManagerDto request) {
        DeliveryManagerRole role = DeliveryManagerRole.fromString(request.getManagerRole());
        if (role == DeliveryManagerRole.HUB_DELIVERY_MANAGER) {
            int count = deliveryManagerRepository.countHubManagers(role);
            if (count >= 10) {
                throw new IllegalArgumentException("허브 배송 담당자는 물류 시스템 전체에서 10명만 존재할 수 있습니다..");
            }
        } else if (role == DeliveryManagerRole.COMPANY_DELIVERY_MANAGER) {
            int count = deliveryManagerRepository.countCompanyManagers(request.getAssignedHubId(), role);
            if (count >= 10) {
                throw new IllegalArgumentException("업체 배송 담당자는 각 허브에 10명만 존재할 수 있습니다.");
            }
        }
    }
}
