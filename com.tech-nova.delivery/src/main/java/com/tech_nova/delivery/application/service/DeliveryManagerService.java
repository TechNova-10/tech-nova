package com.tech_nova.delivery.application.service;

import com.tech_nova.delivery.application.dto.DeliveryManagerDto;
import com.tech_nova.delivery.application.dto.DeliveryManagerResponse;
import com.tech_nova.delivery.domain.model.manager.DeliveryManager;
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
            throw new IllegalArgumentException("The manager user ID is already registered.");
        }

        Integer deliveryOrderSequence = request.getDeliveryOrderSequence();

        if (deliveryOrderSequence == null) {
            deliveryOrderSequence = deliveryManagerRepository.findMaxDeliveryOrderByHubId(request.getAssignedHubId()) + 1;
        } else if (deliveryOrderSequence <= 0) {
            throw new IllegalArgumentException("Delivery order sequence must be greater than 0.");
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

    private void checkDeliveryOrderSequenceExists(UUID assignedHubId, Integer deliveryOrderSequence) {
        if (deliveryManagerRepository.existsByAssignedHubIdAndDeliveryOrderSequence(assignedHubId, deliveryOrderSequence)) {
            throw new DeliveryOrderSequenceAlreadyExistsException("해당 순번은 이미 존재합니다.");
        }
    }
}
