package com.tech_nova.delivery.application.service;

import com.tech_nova.delivery.application.dto.DeliveryManagerDto;
import com.tech_nova.delivery.application.dto.HubData;
import com.tech_nova.delivery.application.dto.res.DeliveryManagerResponse;
import com.tech_nova.delivery.application.dto.res.HubResponseDto;
import com.tech_nova.delivery.domain.model.manager.DeliveryManager;
import com.tech_nova.delivery.domain.model.manager.DeliveryManagerRole;
import com.tech_nova.delivery.domain.repository.DeliveryManagerRepository;
import com.tech_nova.delivery.domain.repository.DeliveryManagerRepositoryCustom;
import com.tech_nova.delivery.infrastructure.dto.HubSearchDto;
import com.tech_nova.delivery.presentation.dto.ApiResponseDto;
import com.tech_nova.delivery.presentation.exception.AuthenticationException;
import com.tech_nova.delivery.presentation.request.DeliveryManagerRequest;
import com.tech_nova.delivery.presentation.request.DeliveryManagerSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryManagerService {

    private final AuthService authService;
    private final HubService hubService;

    private final DeliveryManagerRepository deliveryManagerRepository;
    private final DeliveryManagerRepositoryCustom deliveryManagerRepositoryCustom;

    @Transactional
    public UUID createDeliveryManager(DeliveryManagerDto request, UUID userId, String role) {
        if (!role.equals("HUB_MANAGER") && !role.equals("MASTER")) {
            throw new AuthenticationException("배송담당자를 생성할 권한이 없습니다.");
        }

        if (role.equals("HUB_MANAGER")) {
            ApiResponseDto<HubData> response = hubService.getHub(request.getAssignedHubId(), "MASTER");
            HubData hubData = response.getData();
            if (!hubData.getHubManagerId().equals(userId)) {
                throw new AuthenticationException("담당 허브 외 다른 허브에 속하는 배송 담당자를 생성할 권한이 없습니다.");
            }
        }

        if (deliveryManagerRepository.existsByManagerUserId(request.getManagerUserId())) {
            throw new IllegalArgumentException("해당 사용자는 이미 등록되어 있습니다.");
        }

        if (request.getManagerRole().equals("COMPANY_DELIVERY_MANAGER")) {
            validateHubExistence(request.getAssignedHubId());
        }

        validateRoleAndHubAssignment(request);

        validateDeliveryManagerLimits(request);

        DeliveryManager deliveryManager = DeliveryManager.create(
                request.getAssignedHubId(),
                request.getManagerUserId(),
                request.getManagerRole()
        );

        deliveryManagerRepository.save(deliveryManager);

        authService.updateUserRole(request.getManagerUserId(), request.getManagerRole(), userId, role);

        return DeliveryManagerResponse.of(deliveryManager).getId();
    }

    @Cacheable(cacheNames = "deliveryManagerCache", key = "#deliveryManagerId")
    @Transactional(readOnly = true)
    public DeliveryManagerResponse getDeliveryManager(UUID deliveryManagerId, UUID userId, String role) {
        if (role.equals("MASTER")) {
            DeliveryManager manager = deliveryManagerRepository.findById(deliveryManagerId)
                    .orElseThrow(() -> new IllegalArgumentException("배송 담당자를 찾을 수 없습니다."));

            return DeliveryManagerResponse.of(manager);
        }

        DeliveryManager manager = deliveryManagerRepository.findByIdAndIsDeletedFalse(deliveryManagerId)
                .orElseThrow(() -> new IllegalArgumentException("배송 담당자를 찾을 수 없습니다."));

        return DeliveryManagerResponse.of(manager);
    }

    @Cacheable(cacheNames = "deliveryManagerListCache")
    @Transactional(readOnly = true)
    public Page<DeliveryManagerResponse> getDeliveryManagers(DeliveryManagerSearchRequest request, Pageable pageable, UUID userId, String role) {
        if (role.equals("COMPANY_MANAGER")) {
            throw new AuthenticationException("권한이 없는 작업입니다.");
        }

        int pageSize = (pageable.getPageSize() == 30 || pageable.getPageSize() == 50)
                ? pageable.getPageSize() : 10;

        Pageable customPageable = PageRequest.of(pageable.getPageNumber(), pageSize, pageable.getSort());

        if (role.equals("HUB_MANAGER")) {
            List<UUID> hubIdList = Optional.ofNullable(hubService.getHubs(new HubSearchDto(), "MASTER", 0, 10).getData())
                    .map(hubsPage -> hubsPage.getContent())
                    .orElse(Collections.emptyList())
                    .stream()
                    .filter(hub -> hub.getHubManagerId().equals(userId))
                    .map(HubResponseDto::getHubId)
                    .toList();
            request.setManageHubIds(hubIdList);
        }

        if (role.equals("COMPANY_DELIVERY_MANAGER") || role.equals("HUB_DELIVERY_MANAGER")) {
            Optional<DeliveryManager> managerOptional = deliveryManagerRepository.findByManagerUserIdAndIsDeletedFalse(userId);
            if (managerOptional.isPresent()) {
                DeliveryManager manager = managerOptional.get();
                DeliveryManagerResponse response = DeliveryManagerResponse.of(manager);

                return new PageImpl<>(List.of(response), customPageable, 1);
            }
            return new PageImpl<>(Collections.emptyList(), customPageable, 0);
        }

        return deliveryManagerRepositoryCustom.searchDeliveryManager(role, request, customPageable)
                .map(DeliveryManagerResponse::of);
    }

    @Transactional
    public void updateDeliveryManager(UUID deliveryManagerId, DeliveryManagerRequest request, UUID userId, String role) {
        if (!role.equals("HUB_MANAGER") && !role.equals("MASTER")) {
            throw new AuthenticationException("배송담당자를 수정할 권한이 없습니다.");
        }

        DeliveryManager deliveryManager = deliveryManagerRepository.findByIdAndIsDeletedFalse(deliveryManagerId)
                .orElseThrow(() -> new IllegalArgumentException("배송 담당자를 찾을 수 없습니다."));

        if (role.equals("HUB_MANAGER")) {
            ApiResponseDto<HubData> response = hubService.getHub(request.getAssignedHubId(), "MASTER");
            HubData hubData = response.getData();
            if (!hubData.getHubManagerId().equals(userId)) {
                throw new AuthenticationException("담당 허브 외 다른 허브에 속하는 배송 담당자를 수정할 권한이 없습니다.");
            }
        }

        deliveryManager.update(
                request.getAssignedHubId(),
                DeliveryManagerRole.valueOf(request.getManagerRole()),
                request.getDeliveryOrderSequence()
        );
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

    private void validateHubExistence(UUID hubId) {
        try {
            ApiResponseDto<HubData> response = hubService.getHub(hubId, "MASTER");
            HubData hubData = response.getData();

            if (hubData == null) {
                throw new IllegalArgumentException("허브 정보를 찾을 수 없습니다.");
            }

        } catch (Exception e) {
            throw new IllegalArgumentException("허브 검증에 실패했습니다.", e);
        }
    }
}
