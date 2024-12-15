package com.tech_nova.delivery.application.service;

import com.tech_nova.delivery.application.dto.*;
import com.tech_nova.delivery.application.dto.res.DeliveryCompanyRouteRecordResponse;
import com.tech_nova.delivery.application.dto.res.DeliveryResponse;
import com.tech_nova.delivery.application.dto.res.DeliveryRouteRecordResponse;
import com.tech_nova.delivery.domain.model.delivery.*;
import com.tech_nova.delivery.domain.model.manager.DeliveryManager;
import com.tech_nova.delivery.domain.model.manager.DeliveryManagerRole;
import com.tech_nova.delivery.domain.repository.*;
import com.tech_nova.delivery.domain.service.DeliveryManagerAssignmentService;
import com.tech_nova.delivery.infrastructure.dto.CompanyResponse;
import com.tech_nova.delivery.infrastructure.dto.HubSearchDto;
import com.tech_nova.delivery.infrastructure.dto.MovementRequestDto;
import com.tech_nova.delivery.infrastructure.dto.MovementResponse;
import com.tech_nova.delivery.presentation.dto.ApiResponseDto;
import com.tech_nova.delivery.presentation.exception.DuplicateDeliveryException;
import com.tech_nova.delivery.presentation.exception.HubDeliveryCompletedException;
import com.tech_nova.delivery.presentation.request.DeliverySearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final AuthService authService;
    private final CompanyService companyService;
    private final HubService hubService;
    private final HubMovementService hubMovementService;
    private final GeocodingApiService geocodingApiService;
    private final DirectionsApiService directionsApiService;

    private final DeliveryRepository deliveryRepository;
    private final DeliveryManagerRepository deliveryManagerRepository;
    private final DeliveryRouteRecordRepository deliveryRouteRecordRepository;
    private final DeliveryCompanyRouteRecordRepository deliveryCompanyRouteRecordRepository;
    private final DeliveryRepositoryCustom deliveryRepositoryCustom;

    private final DeliveryManagerAssignmentService deliveryManagerAssignmentService;

    @Transactional
    public UUID createDelivery(DeliveryDto request) {
        if (deliveryRepository.existsByOrderIdAndIsDeletedFalse(request.getOrderId())) {
            throw new DuplicateDeliveryException("해당 주문은 이미 배송이 등록되어 있습니다.");
        }

        UUID recipientCompanyId = request.getRecipientCompanyId();
        CompanyResponse recipientCompany = companyService.getCompanyById(recipientCompanyId).getData();
        UUID departureHubId = recipientCompany.getHubId();
        UUID arrivalHubId = validateHubExistence(request.getProvince(), request.getCity());

        Delivery delivery = Delivery.create(
                request.getOrderId(),
                departureHubId,
                arrivalHubId,
                DeliveryStatus.HUB_WAITING,
                request.getRecipientCompanyId(),
                request.getProvince(),
                request.getCity(),
                request.getDistrict(),
                request.getRoadName(),
                request.getDetailAddress(),
                new ArrayList<>()
        );

        // 이동 경로 생성
        MovementRequestDto movementRequestDto = new MovementRequestDto(departureHubId, arrivalHubId);
        MovementResponse movementResponse = hubMovementService.createMovement(movementRequestDto, UUID.randomUUID(), "MASTER").getData();
        List<HubMovementData> hubMovementDatas = createHubMovementDataList(movementResponse);

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

    public DeliveryResponse getDelivery(UUID deliveryId) {
        // TODO: 마스터와 허브 관리자이면 삭제된 배송도 볼 수 있게 수정
        Delivery delivery = deliveryRepository.findByIdAndIsDeletedFalse(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("배송 데이터를 찾을 수 없습니다."));

        return DeliveryResponse.of(delivery);
    }

    public DeliveryRouteRecordResponse getDeliveryRouteRecord(UUID deliveryId) {
        // TODO: 마스터와 허브 관리자이면 삭제된 배송도 볼 수 있게 수정
        DeliveryRouteRecord routeRecord = deliveryRouteRecordRepository.findByIdAndIsDeletedFalse(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("배송 경로를 찾을 수 없습니다."));

        return DeliveryRouteRecordResponse.of(routeRecord);
    }

    public DeliveryCompanyRouteRecordResponse getDeliveryCompanyRouteRecord(UUID deliveryId) {
        // TODO: 마스터와 허브 관리자이면 삭제된 배송도 볼 수 있게 수정
        DeliveryCompanyRouteRecord routeRecord = deliveryCompanyRouteRecordRepository.findByIdAndIsDeletedFalse(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("배송 경로를 찾을 수 없습니다."));

        return DeliveryCompanyRouteRecordResponse.of(routeRecord);
    }

    @Transactional
    public void updateDeliveryAddress(UUID deliveryId, DeliveryAddressUpdateDto request) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("배송 데이터를 찾을 수 없습니다."));

        if (delivery.isDeleted()) {
            throw new IllegalStateException("삭제된 배송은 수정할 수 없습니다.");
        }

        if (delivery.getCurrentStatus() == DeliveryStatus.HUB_MOVING) {
            throw new IllegalStateException("이동 중인 배송은 수정할 수 없습니다.");
        }

        if (delivery.getCurrentStatus() == DeliveryStatus.DELIVERY_COMPLETED) {
            throw new IllegalStateException("완료된 배송은 수정할 수 없습니다.");
        }

        if (delivery.getCurrentStatus() == DeliveryStatus.COMPANY_MOVING) {
            throw new IllegalStateException("이미 배송지로 출발해 수정할 수 없습니다.");
        }

        if (delivery.getCurrentStatus() == DeliveryStatus.COMPANY_WAITING) {
            throw new IllegalStateException("허브에 도착해 배송 준비 중이므로 수정할 수 없습니다.");
        }

        List<DeliveryRouteRecord> routeRecords = deliveryRouteRecordRepository.findByDeliveryIdAndIsDeletedFalse(delivery.getId());
        for (DeliveryRouteRecord record : routeRecords) {
            if (record.getCurrentStatus() != DeliveryHubStatus.HUB_WAITING) {
                throw new IllegalStateException("이미 배송이 시작되어 수정할 수 없습니다.");
            }
        }

        UUID deletedBy = getUserIdFromToken("");
        delivery.markAsDeleted(deletedBy);

        DeliveryDto dto = DeliveryDto.create(
                delivery.getOrderId(),
                request.getRecipientCompanyId(),
                request.getProvince(),
                request.getCity(),
                request.getDistrict(),
                request.getRoadName(),
                request.getDetailAddress()
        );

        createDelivery(dto);
    }

    @Transactional
    public void updateRecipient(UUID deliveryId, String recipient) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("배송 데이터를 찾을 수 없습니다."));

        if (delivery.isDeleted()) {
            throw new IllegalArgumentException("삭제된 배송에는 수령인을 입력할 수 없습니다.");
        }

        DeliveryCompanyRouteRecord companyRouteRecord =
                deliveryCompanyRouteRecordRepository.findByDeliveryIdAndIsDeletedFalse(delivery.getId());
        if (companyRouteRecord.getCurrentStatus() != DeliveryCompanyStatus.DELIVERY_COMPLETED) {
            throw new IllegalArgumentException("배송이 완료되지 않으면 수령인을 입력할 수 없습니다.");
        }

        UUID updatedBy = getUserIdFromToken("");
        delivery.updateRecipient(recipient, updatedBy);
    }

    @Transactional
    public void deleteDelivery(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("배송 데이터를 찾을 수 없습니다."));
        UUID deletedBy = getUserIdFromToken("");
        delivery.markAsDeleted(deletedBy);
    }

    @Transactional
    public UUID updateRouteRecord(UUID deliveryRouteId, DeliveryRouteRecordUpdateDto request) {
        DeliveryRouteRecord routeRecord = deliveryRouteRecordRepository.findById(deliveryRouteId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 경로를 찾을 수 없습니다."));

        Delivery delivery = routeRecord.getDelivery();

        DeliveryHubStatus newStatus = null;
        if (request.getCurrentStatus() != null) {
            newStatus = DeliveryHubStatus.valueOf(request.getCurrentStatus());
        }

        validateAdjacentRouteStatus(delivery.getRouteRecords(), routeRecord, newStatus);
        validateCompanyRouteStatus(delivery);

        DeliveryManager deliveryManager = null;
        if (request.getDeliveryManagerId() != null) {
            deliveryManager = deliveryManagerRepository.findByIdAndIsDeletedFalse(request.getDeliveryManagerId())
                    .orElseThrow(() -> new IllegalArgumentException("배송 담당자를 찾을 수 없습니다."));
        }

        UUID updatedBy = getUserIdFromToken("");
        delivery.updateRouteRecord(deliveryRouteId, deliveryManager, newStatus, request.getRealDistance(), request.getRealTime(), updatedBy);

        if (newStatus == DeliveryHubStatus.HUB_ARRIVE && routeRecord.getSequence() == delivery.getRouteRecords().size()) {
            createCompanyRouteRecord(delivery);
        }

        return routeRecord.getId();
    }

    @Transactional
    public UUID updateRouteRecordState(UUID deliveryRouteId, String updateStatus) {
        DeliveryRouteRecord routeRecord = deliveryRouteRecordRepository.findById(deliveryRouteId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 경로를 찾을 수 없습니다."));

        Delivery delivery = routeRecord.getDelivery();

        DeliveryHubStatus newStatus = DeliveryHubStatus.valueOf(updateStatus);

        validateAdjacentRouteStatus(delivery.getRouteRecords(), routeRecord, newStatus);
        validateCompanyRouteStatus(delivery);

        UUID updatedBy = getUserIdFromToken("");
        delivery.updateRouteRecordState(deliveryRouteId, newStatus, updatedBy);

        if (newStatus == DeliveryHubStatus.HUB_ARRIVE && routeRecord.getSequence() == delivery.getRouteRecords().size()) {
            createCompanyRouteRecord(delivery);
        }

        return routeRecord.getId();
    }

    @Transactional
    public UUID updateCompanyRouteRecordDeliveryManager(UUID deliveryRouteId, UUID deliveryManagerId) {
        DeliveryCompanyRouteRecord routeRecord = deliveryCompanyRouteRecordRepository.findById(deliveryRouteId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 경로를 찾을 수 없습니다."));

        Delivery delivery = routeRecord.getDelivery();

        if (delivery.getCurrentStatus().equals(DeliveryStatus.DELIVERY_COMPLETED)) {
            throw new IllegalArgumentException("완료된 배송은 배송 담당자를 변경할 수 없습니다.");
        }

        DeliveryManager deliveryManager = deliveryManagerRepository.findByIdAndIsDeletedFalse(deliveryManagerId)
                .orElseThrow(() -> new IllegalArgumentException("배송 담당자를 찾을 수 없습니다."));

        if (!deliveryManager.getManagerRole().equals(DeliveryManagerRole.COMPANY_DELIVERY_MANAGER)) {
            throw new IllegalArgumentException("허브 배송 담당자는 업체 배송에 배정할 수 없습니다.");
        }

        UUID updatedBy = getUserIdFromToken("");
        delivery.updateCompanyRouteRecordDeliveryManager(deliveryRouteId, deliveryManager, updatedBy);

        return routeRecord.getId();
    }

    @Transactional
    public UUID updateCompanyRouteRecord(UUID deliveryRouteId, DeliveryCompanyRouteRecordUpdateDto request) {
        DeliveryCompanyRouteRecord routeRecord = deliveryCompanyRouteRecordRepository.findById(deliveryRouteId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 경로를 찾을 수 없습니다."));

        Delivery delivery = routeRecord.getDelivery();

        DeliveryManager deliveryManager = null;
        if (request.getDeliveryManagerId() != null) {
            deliveryManager = deliveryManagerRepository.findByIdAndIsDeletedFalse(request.getDeliveryManagerId())
                    .orElseThrow(() -> new IllegalArgumentException("배송 담당자를 찾을 수 없습니다."));
        }

        DeliveryCompanyStatus newStatus = null;
        if (request.getCurrentStatus() != null) {
            newStatus = DeliveryCompanyStatus.valueOf(request.getCurrentStatus());
        }

        UUID updatedBy = getUserIdFromToken("");
        delivery.updateCompanyRouteRecord(deliveryRouteId, deliveryManager, newStatus, request.getDeliveryOrderSequence(), request.getRealDistance(), request.getRealTime(), updatedBy);

        return routeRecord.getId();
    }

    @Transactional
    public UUID updateCompanyRouteRecordState(UUID deliveryRouteId, String updateStatus) {
        DeliveryCompanyRouteRecord routeRecord = deliveryCompanyRouteRecordRepository.findById(deliveryRouteId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 경로를 찾을 수 없습니다."));

        Delivery delivery = routeRecord.getDelivery();

        DeliveryCompanyStatus newStatus = DeliveryCompanyStatus.valueOf(updateStatus);

        UUID updatedBy = getUserIdFromToken("");
        delivery.updateCompanyRouteRecordState(deliveryRouteId, newStatus, updatedBy);

        return routeRecord.getId();
    }

    @Transactional
    public UUID updateRouteRecordDeliveryManager(UUID deliveryRouteId, UUID deliveryManagerId) {
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

        UUID updatedBy = getUserIdFromToken("");
        delivery.updateRouteRecordDeliveryManager(deliveryRouteId, deliveryManager, updatedBy);

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

        String city = delivery.getCity();
        String roadName = delivery.getRoadName();
        LocationData coordinates = fetchCoordinates(city + " " + roadName);

        HubData departureHub = hubService.getHub(delivery.getArrivalHubId(), "MASTER").getData();

        String departureEstimate = departureHub.getLongitude() + "," + departureHub.getLatitude();
        String arrivalEstimate = coordinates.getLongitude() + "," + coordinates.getLatitude();
        RouteEstimateData routeEstimateData = fetchRouteEstimate(departureEstimate, arrivalEstimate);

        DeliveryCompanyRouteRecord companyRouteRecord = DeliveryCompanyRouteRecord.create(
                delivery,
                deliveryManager,
                delivery.getArrivalHubId(),
                delivery.getRecipientCompanyId(),
                delivery.getProvince(),
                delivery.getCity(),
                delivery.getDistrict(),
                delivery.getRoadName(),
                delivery.getDetailAddress(),
                DeliveryCompanyStatus.COMPANY_WAITING,
                (double) routeEstimateData.getDuration(),
                (double) routeEstimateData.getDistance()
        );

        delivery.addCompanyRouteRecord(companyRouteRecord);
    }

    @Transactional
    public void deleteRouteRecord(UUID deliveryRouteId) {
        DeliveryRouteRecord routeRecord = deliveryRouteRecordRepository.findById(deliveryRouteId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 경로를 찾을 수 없습니다."));

        Delivery delivery = routeRecord.getDelivery();
        UUID deletedBy = getUserIdFromToken("");
        delivery.deleteRouteRecordState(deliveryRouteId, deletedBy);
    }

    @Transactional
    public void deleteCompanyRouteRecord(UUID deliveryRouteId) {
        DeliveryCompanyRouteRecord routeRecord = deliveryCompanyRouteRecordRepository.findById(deliveryRouteId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 경로를 찾을 수 없습니다."));

        Delivery delivery = routeRecord.getDelivery();
        UUID deletedBy = getUserIdFromToken("");
        delivery.deleteCompanyRouteRecordState(deliveryRouteId, deletedBy);
    }

    private List<HubMovementData> createHubMovementDataList(MovementResponse movementResponse) {
        UUID departureHubId = movementResponse.getDepartureHubId();
        UUID intermediateHubId = movementResponse.getIntermediateHubId();
        UUID arrivalHubId = movementResponse.getArrivalHubId();

        List<HubMovementData> hubMovementDatas = new ArrayList<>();

        if (intermediateHubId.equals(arrivalHubId)) {
            hubMovementDatas.add(new HubMovementData(UUID.randomUUID(), departureHubId, arrivalHubId, movementResponse.getTimeTravel(), movementResponse.getDistance()));
        } else {
            // TODO 추후 role 변경 필요 현재 임의로 MASTER 적용
            HubData departureHub = hubService.getHub(departureHubId, "MASTER").getData();
            HubData intermediateHub = hubService.getHub(intermediateHubId, "MASTER").getData();
            HubData arrivalHub = hubService.getHub(arrivalHubId, "MASTER").getData();

            String departureEstimate = departureHub.getLongitude() + "," + departureHub.getLatitude();
            String intermediateEstimate = intermediateHub.getLongitude() + "," + intermediateHub.getLatitude();
            String arrivalEstimate = arrivalHub.getLongitude() + "," + arrivalHub.getLatitude();

            RouteEstimateData routeEstimateData1 = fetchRouteEstimate(departureEstimate, intermediateEstimate);
            RouteEstimateData routeEstimateData2 = fetchRouteEstimate(intermediateEstimate, arrivalEstimate);

            hubMovementDatas.add(new HubMovementData(UUID.randomUUID(), departureHubId, intermediateHubId, (double) routeEstimateData1.getDuration(), (double) routeEstimateData1.getDistance()));
            hubMovementDatas.add(new HubMovementData(UUID.randomUUID(), intermediateHubId, arrivalHubId, (double) routeEstimateData2.getDuration(), (double) routeEstimateData2.getDistance()));
        }

        return hubMovementDatas;
    }

    @Transactional(readOnly = true)
    public Page<DeliveryResponse> getDeliverys(DeliverySearchRequest deliveryRouteSearchRequest, Pageable pageable) {

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
        return deliveryRepositoryCustom.searchDelivery("MASTER", deliveryRouteSearchRequest, customPageable).map(DeliveryResponse::of);

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

    private void validateAdjacentRouteStatus(List<DeliveryRouteRecord> routeRecords, DeliveryRouteRecord routeRecord, DeliveryHubStatus newStatus) {
        routeRecords.sort(Comparator.comparingInt(DeliveryRouteRecord::getSequence));
        int currentSequence = routeRecord.getSequence();

        if (currentSequence > 1) {
            DeliveryRouteRecord previousRecord = routeRecords.stream()
                    .filter(record -> record.getSequence() == currentSequence - 1)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("이전 배송 경로를 찾을 수 없습니다. 현재 Sequence: " + currentSequence));

            boolean isInvalidStatus = newStatus != null
                    && newStatus != DeliveryHubStatus.HUB_WAITING
                    && previousRecord.getCurrentStatus() != DeliveryHubStatus.HUB_ARRIVE;

            if (isInvalidStatus) {
                throw new IllegalStateException("이전 허브의 배송이 완료되지 않았습니다.");
            }
        }

        if (currentSequence < routeRecords.size()) {
            DeliveryRouteRecord nextRecord = routeRecords.stream()
                    .filter(record -> record.getSequence() == currentSequence + 1)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("다음 배송 경로를 찾을 수 없습니다. 현재 Sequence: " + currentSequence));

            if (nextRecord.getCurrentStatus() != DeliveryHubStatus.HUB_WAITING) {
                throw new IllegalStateException("다음 허브의 배송이 시작되었습니다.");
            }
        }
    }

    private void validateCompanyRouteStatus(Delivery delivery) {
        DeliveryCompanyRouteRecord companyRouteRecord =
                deliveryCompanyRouteRecordRepository.findByDeliveryIdAndIsDeletedFalse(delivery.getId());

        if (companyRouteRecord != null && companyRouteRecord.getCurrentStatus() != DeliveryCompanyStatus.COMPANY_WAITING) {
            if (companyRouteRecord.getCurrentStatus() == DeliveryCompanyStatus.COMPANY_MOVING) {
                throw new HubDeliveryCompletedException("현재 업체 배송이 시작되어 수정이 불가능합니다.");
            } else if (companyRouteRecord.getCurrentStatus() == DeliveryCompanyStatus.DELIVERY_COMPLETED) {
                throw new HubDeliveryCompletedException("현재 업체 배송이 완료되어 수정이 불가능합니다.");
            }
        }
    }

    public LocationData fetchCoordinates(String address) {
        return geocodingApiService.getCoordinates(address);
    }

    public RouteEstimateData fetchRouteEstimate(String start, String goal) {
        return directionsApiService.getRouteEstimateData(start, goal);
    }

    // TODO: 추후 인증 완성 시 토큰 내 정보로 ID 가져올 예정
    private UUID getUserIdFromToken(String token) {
        return UUID.randomUUID();
    }

    // TODO: 각종 검증 서비스 추후 인증 서비스 구현 후 연결 예정
    private boolean validateDeliveryManagerAssignedHub(String token, Delivery delivery) {
        UUID userId = authService.getUserId(token);
        String userRole = authService.getUserRole(token);

        if (!"DELIVERY_MANAGER".equals(userRole)) {
            return true;
        }

        DeliveryManager manager = deliveryManagerRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new IllegalArgumentException("배송 담당자를 찾을 수 없습니다."));

        UUID managerAssignedHubId = manager.getAssignedHubId();
        UUID departureHubId = delivery.getDepartureHubId();
        UUID arrivalHubId = delivery.getArrivalHubId();

        return managerAssignedHubId.equals(departureHubId) || managerAssignedHubId.equals(arrivalHubId);
    }

    private boolean validateHubManagerAssignedHub(String token, Delivery delivery) {
        UUID userId = authService.getUserId(token);
        String userRole = authService.getUserRole(token);

        if (!"HUB_MANAGER".equals(userRole)) {
            return true;
        }

        UUID departureHubId = delivery.getDepartureHubId();
        UUID arrivalHubId = delivery.getArrivalHubId();

        HubData departureHub = hubService.getHub(departureHubId, userRole).getData();
        HubData arrivalHub = hubService.getHub(arrivalHubId, userRole).getData();

        return userId.equals(departureHub.getHubManagerId()) || userId.equals(arrivalHub.getHubManagerId());
    }

    private boolean validateMaster(String token, Delivery delivery) {
        UUID userId = authService.getUserId(token);
        String userRole = authService.getUserRole(token);

        return "HUB_MANAGER".equals(userRole);
    }

    private void validateHubExistence(String token, UUID hubId) {
        try {
            // TODO 추후 주석처리된 코드를 적용
            // String userRole = authService.getUserRole(token);
            String userRole = "MASTER";
            ApiResponseDto<HubData> response = hubService.getHub(hubId, userRole);
            HubData hubData = response.getData();

            if (hubData == null) {
                throw new IllegalArgumentException("허브 정보를 찾을 수 없습니다.");
            }

        } catch (Exception e) {
            throw new IllegalArgumentException("허브 검증에 실패했습니다.", e);
        }
    }

    private UUID validateHubExistence(String province, String city) {
        HubSearchDto hubSearchDto = new HubSearchDto();

        if ("경기도".equals(province)) {
            String[] gyeonggiNorthCities = {
                    "고양시", "김포시", "부천시", "구리시", "남양주시", "동두천시", "양주시", "의정부시", "파주시",
                    "포천시", "가평군", "연쳔군"
            };

            String[] gyeonggiSouthCities = {
                    "과천시", "광명시", "광주시", "군포시", "부천시", "성남시", "수원시", "시흥시", "안산시", "안성시",
                    "안양시", "여주시", "오산시", "용인시", "의왕시", "이천시", "평택시", "하남시", "화성시"
            };

            if (Arrays.asList(gyeonggiNorthCities).contains(city)) {
                hubSearchDto.setName("경기도 북부 센터");
            } else if (Arrays.asList(gyeonggiSouthCities).contains(city)) {
                hubSearchDto.setName("경기도 남부 센터");
            } else {
                hubSearchDto.setProvince("경기도");
            }
        } else {
            hubSearchDto.setProvince(province);
        }

        // TODO 추후 role 변경 필요 현재 임의로 MASTER 적용
        return Optional.ofNullable(hubService.getHubs(hubSearchDto, "MASTER", 0, 10).getData())
                .filter(hubsPage -> !hubsPage.getContent().isEmpty())
                .map(hubsPage -> hubsPage.getContent().get(0).getHubId())
                .orElse(null);
    }
}
