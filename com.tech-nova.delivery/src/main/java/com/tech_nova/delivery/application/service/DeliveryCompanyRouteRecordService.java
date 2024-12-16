package com.tech_nova.delivery.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech_nova.delivery.application.dto.DeliveryCompanyRouteRecordUpdateDto;
import com.tech_nova.delivery.application.dto.LocationData;
import com.tech_nova.delivery.application.dto.RouteEstimateData;
import com.tech_nova.delivery.application.dto.res.DeliveryCompanyRouteRecordResponse;
import com.tech_nova.delivery.application.dto.res.HubResponseDto;
import com.tech_nova.delivery.domain.model.delivery.*;
import com.tech_nova.delivery.domain.model.manager.DeliveryManager;
import com.tech_nova.delivery.domain.model.manager.DeliveryManagerRole;
import com.tech_nova.delivery.domain.repository.DeliveryCompanyRouteRecordRepository;
import com.tech_nova.delivery.domain.repository.DeliveryCompanyRouteRecordRepositoryCustom;
import com.tech_nova.delivery.domain.repository.DeliveryManagerRepository;
import com.tech_nova.delivery.infrastructure.dto.HubSearchDto;
import com.tech_nova.delivery.presentation.request.DeliveryRouteSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class DeliveryCompanyRouteRecordService {
    private final HubService hubService;

    private final GeocodingApiService geocodingApiService;
    private final DirectionsApiService directionsApiService;
    private final GoogleApiService googleApiService;

    private final DeliveryManagerRepository deliveryManagerRepository;
    private final DeliveryCompanyRouteRecordRepository deliveryCompanyRouteRecordRepository;
    private final DeliveryCompanyRouteRecordRepositoryCustom deliveryCompanyRouteRecordRepositoryCustom;

    @Transactional
    @Scheduled(cron = "0 0 6 * * ?")
    public void setOrderSequence() {
        List<DeliveryCompanyRouteRecord> deliveryCompanyRouteRecords = deliveryCompanyRouteRecordRepository.findAllByIsDeletedFalseGroupedByDeliveryManager();

        Map<UUID, List<DeliveryCompanyRouteRecord>> groupedByDeliveryManager = deliveryCompanyRouteRecords.stream()
                .collect(Collectors.groupingBy(record -> record.getDeliveryManager().getId()));

        for (Map.Entry<UUID, List<DeliveryCompanyRouteRecord>> entry : groupedByDeliveryManager.entrySet()) {
            List<DeliveryCompanyRouteRecord> records = entry.getValue();

            List<Mono<LocationData>> locationDataMonoList = new ArrayList<>();

            for (DeliveryCompanyRouteRecord record : records) {
                String city = record.getCity();
                String roadName = record.getRoadName();

                locationDataMonoList.add(Mono.fromCallable(() -> fetchCoordinates(city + " " + roadName)));
            }

            Flux.merge(locationDataMonoList)
                    .collectList()
                    .doOnTerminate(() -> {
                        List<LocationData> locationDatas = locationDataMonoList.stream()
                                .map(Mono::block)
                                .collect(Collectors.toList());

                        List<Integer> optimizedOrder = getWaypointsOrder(locationDatas);
                        StringBuilder slackMessage = updateDeliveryRouteOrder(records, optimizedOrder);

                        String[] startAndGoal = getCoordinatesForOrderOneAndLast(locationDatas, optimizedOrder);
                        String start = startAndGoal[0];
                        String goal = startAndGoal[1];
                        String waypoints = "";

                        RouteEstimateData routeEstimateData;

                        if (locationDatas.size() > 2) {
                            waypoints = generateIntermediateWaypoints(locationDatas, optimizedOrder);
                            routeEstimateData = directionsApiService.getRouteEstimateData(start, goal, waypoints);
                        } else {
                            routeEstimateData = directionsApiService.getRouteEstimateData(start, goal);
                        }

                        long distanceInMeters = routeEstimateData.getDistance();
                        double distanceInKilometers = distanceInMeters / 1000.0;

                        long durationInMillis = routeEstimateData.getDuration();
                        long durationInMinutes = durationInMillis / 60000;

                        slackMessage.append("예상 거리: ").append(String.format("%.2f", distanceInKilometers)).append(" km, 예상 시간: ").append(durationInMinutes).append(" 분");

                        // TODO 슬랙메시지 발송
                    })
                    .subscribe();
        }
    }

    private List<Integer> getWaypointsOrder(List<LocationData> locationDatas) {
        String waypointsString = buildWaypointsString(locationDatas);

        String requestBody = "내가 다음과 같이 여러 장소들을 경도,위도로 보냈어. 각 장소는 |로 구분돼. 차로 이동했을 때 가장 빠르게 이동할 수 있을지 순서를 알려줘. 만약 내가 준 장소가 두개라면 임의적으로 너가 1번 2번을 정해. 다른 부가 설명없이 다음과 같이 방문 순서만 응답해줘, 그리고 각 방문 순서는 ,로 구분해줘. 예시 보여줄게. 1,2,3,5,4" + waypointsString;

        String response = googleApiService.generateContent(requestBody);

        return parseOptimizedOrder(response);
    }

    private String buildWaypointsString(List<LocationData> locationDatas) {
        if (locationDatas == null || locationDatas.isEmpty()) {
            throw new IllegalArgumentException("Location data list cannot be null or empty");
        }

        return locationDatas.stream()
                .map(locationData -> locationData.getLongitude() + "," + locationData.getLatitude())
                .collect(Collectors.joining("|"));
    }

    public List<Integer> parseOptimizedOrder(String jsonResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            String text = rootNode.at("/candidates/0/content/parts/0/text").asText().trim();

            return Arrays.stream(text.split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse optimized order from response text", e);
        }
    }

    private String[] getCoordinatesForOrderOneAndLast(List<LocationData> locationDatas, List<Integer> optimizedOrder) {
        String[] coordinates = new String[2];

        int orderOneIndex = optimizedOrder.indexOf(1);
        if (orderOneIndex != -1 && orderOneIndex < locationDatas.size()) {
            LocationData location = locationDatas.get(orderOneIndex);
            coordinates[0] = location.getLongitude() + "," + location.getLatitude();
        } else {
            coordinates[0] = "";
        }

        int orderLastIndex = optimizedOrder.indexOf(optimizedOrder.size());
        if (orderLastIndex != -1 && orderLastIndex < locationDatas.size()) {
            LocationData location = locationDatas.get(orderLastIndex);
            coordinates[1] = location.getLongitude() + "," + location.getLatitude();
        } else {
            coordinates[1] = "";
        }

        return coordinates;
    }

    private static String generateIntermediateWaypoints(List<LocationData> locationDatas, List<Integer> optimizedOrder) {
        StringBuilder waypoints = new StringBuilder();

        Set<Integer> excludedIndexes = new HashSet<>();
        int orderOneIndex = optimizedOrder.indexOf(1);
        int orderLastIndex = optimizedOrder.indexOf(optimizedOrder.size());

        if (orderOneIndex != -1) {
            excludedIndexes.add(orderOneIndex);
        }
        if (orderLastIndex != -1) {
            excludedIndexes.add(orderLastIndex);
        }

        List<LocationData> filteredLocationDatas = new ArrayList<>();
        for (int i = 0; i < locationDatas.size(); i++) {
            if (!excludedIndexes.contains(i)) {
                filteredLocationDatas.add(locationDatas.get(i));
            }
        }

        for (int i = 0; i < filteredLocationDatas.size(); i++) {
            LocationData current = filteredLocationDatas.get(i);
            String waypoint = current.getLongitude() + "," + current.getLatitude();

            if (i > 0) {
                waypoints.append("|");
            }
            waypoints.append(waypoint);
        }

        return waypoints.toString();
    }

    public LocationData fetchCoordinates(String address) {
        return geocodingApiService.getCoordinates(address);
    }

    private StringBuilder updateDeliveryRouteOrder(List<DeliveryCompanyRouteRecord> records, List<Integer> optimizedOrder) {
        StringBuilder slackMessage = new StringBuilder();
        for (int i = 0; i < records.size(); i++) {
            DeliveryCompanyRouteRecord record = records.get(i);
            Delivery delivery = record.getDelivery();
            delivery.updateCompanyRouteRecordOrderSequence(record.getId(), optimizedOrder.get(i));
            slackMessage.append(i + 1)
                    .append("번 배송지: ")
                    .append(record.getProvince()).append(" ")
                    .append(record.getCity()).append(" ")
                    .append(record.getDistrict()).append(" ")
                    .append(record.getRoadName());

            if (record.getDetailAddress() != null) {
                slackMessage.append(" ").append(record.getDetailAddress());
            }

            slackMessage.append("\n");
        }
        return slackMessage;
    }

    @Transactional(readOnly = true)
    public Page<DeliveryCompanyRouteRecordResponse> getDeliveryCompanyRouteRecords(DeliveryRouteSearchRequest deliveryRouteSearchRequest, Pageable pageable) {

        int pageSize =
                (pageable.getPageSize() == 30
                        || pageable.getPageSize() == 50)
                        ? pageable.getPageSize() : 10;

        Pageable customPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageSize,
                pageable.getSort()
        );

        return deliveryCompanyRouteRecordRepositoryCustom.searchDeliveryCompanyRouteRecords("MASTER", deliveryRouteSearchRequest, customPageable).map(DeliveryCompanyRouteRecordResponse::of);

    }

    @Transactional
    public UUID updateCompanyRouteRecord(UUID deliveryRouteId, DeliveryCompanyRouteRecordUpdateDto request, UUID userId, String role) {
        if (role.equals("COMPANY_MANAGER")) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        DeliveryCompanyRouteRecord routeRecord = deliveryCompanyRouteRecordRepository.findById(deliveryRouteId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 경로를 찾을 수 없습니다."));

        Delivery delivery = routeRecord.getDelivery();

        if (role.equals("HUB_MANAGER")) {
            validateManagedHub(delivery, userId);
        }

        if (role.equals("COMPANY_DELIVERY_MANAGER") || role.equals("HUB_DELIVERY_MANAGER")) {
            validateDeliveryManagerManagedDelivery(delivery.getRouteRecords(), delivery.getCompanyRouteRecords(), userId);
        }

        DeliveryManager deliveryManager = null;
        if (request.getDeliveryManagerId() != null) {
            deliveryManager = deliveryManagerRepository.findByIdAndIsDeletedFalse(request.getDeliveryManagerId())
                    .orElseThrow(() -> new IllegalArgumentException("배송 담당자를 찾을 수 없습니다."));
        }

        DeliveryCompanyStatus newStatus = null;
        if (request.getCurrentStatus() != null) {
            newStatus = DeliveryCompanyStatus.valueOf(request.getCurrentStatus());
        }

        delivery.updateCompanyRouteRecord(deliveryRouteId, deliveryManager, newStatus, request.getDeliveryOrderSequence(), request.getRealDistance(), request.getRealTime(), userId);

        return routeRecord.getId();
    }

    @Transactional
    public UUID updateCompanyRouteRecordDeliveryManager(UUID deliveryRouteId, UUID deliveryManagerId, UUID userId, String role) {
        if (role.equals("COMPANY_MANAGER")) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        DeliveryCompanyRouteRecord routeRecord = deliveryCompanyRouteRecordRepository.findById(deliveryRouteId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 경로를 찾을 수 없습니다."));

        Delivery delivery = routeRecord.getDelivery();

        if (role.equals("HUB_MANAGER")) {
            validateManagedHub(delivery, userId);
        }

        if (role.equals("COMPANY_DELIVERY_MANAGER") || role.equals("HUB_DELIVERY_MANAGER")) {
            validateDeliveryManagerManagedDelivery(delivery.getRouteRecords(), delivery.getCompanyRouteRecords(), userId);
        }

        if (delivery.getCurrentStatus().equals(DeliveryStatus.DELIVERY_COMPLETED)) {
            throw new IllegalArgumentException("완료된 배송은 배송 담당자를 변경할 수 없습니다.");
        }

        DeliveryManager deliveryManager = deliveryManagerRepository.findByIdAndIsDeletedFalse(deliveryManagerId)
                .orElseThrow(() -> new IllegalArgumentException("배송 담당자를 찾을 수 없습니다."));

        if (!deliveryManager.getManagerRole().equals(DeliveryManagerRole.COMPANY_DELIVERY_MANAGER)) {
            throw new IllegalArgumentException("허브 배송 담당자는 업체 배송에 배정할 수 없습니다.");
        }

        delivery.updateCompanyRouteRecordDeliveryManager(deliveryRouteId, deliveryManager, userId);

        return routeRecord.getId();
    }

    @Transactional
    public UUID updateCompanyRouteRecordState(UUID deliveryRouteId, String updateStatus, UUID userId, String role) {
        if (role.equals("COMPANY_MANAGER")) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        DeliveryCompanyRouteRecord routeRecord = deliveryCompanyRouteRecordRepository.findById(deliveryRouteId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 경로를 찾을 수 없습니다."));

        Delivery delivery = routeRecord.getDelivery();

        if (role.equals("HUB_MANAGER")) {
            validateManagedHub(delivery, userId);
        }

        if (role.equals("COMPANY_DELIVERY_MANAGER") || role.equals("HUB_DELIVERY_MANAGER")) {
            validateDeliveryManagerManagedDelivery(delivery.getRouteRecords(), delivery.getCompanyRouteRecords(), userId);
        }

        DeliveryCompanyStatus newStatus = DeliveryCompanyStatus.valueOf(updateStatus);

        delivery.updateCompanyRouteRecordState(deliveryRouteId, newStatus, userId);

        return routeRecord.getId();
    }

    @Transactional(readOnly = true)
    public DeliveryCompanyRouteRecordResponse getDeliveryCompanyRouteRecord(UUID deliveryId, UUID userId, String role) {
        if (role.equals("HUB_MANAGER") || role.equals("MASTER")) {
            DeliveryCompanyRouteRecord routeRecord = deliveryCompanyRouteRecordRepository.findById(deliveryId)
                    .orElseThrow(() -> new IllegalArgumentException("배송 경로를 찾을 수 없습니다."));

            return DeliveryCompanyRouteRecordResponse.of(routeRecord);

        }
        DeliveryCompanyRouteRecord routeRecord = deliveryCompanyRouteRecordRepository.findByIdAndIsDeletedFalse(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("배송 경로를 찾을 수 없습니다."));

        return DeliveryCompanyRouteRecordResponse.of(routeRecord);
    }

    @Transactional
    public void deleteCompanyRouteRecord(UUID deliveryRouteId, UUID userId, String role) {
        DeliveryCompanyRouteRecord routeRecord = deliveryCompanyRouteRecordRepository.findById(deliveryRouteId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배송 경로를 찾을 수 없습니다."));

        Delivery delivery = routeRecord.getDelivery();
        delivery.deleteCompanyRouteRecordState(deliveryRouteId, userId);
    }

    private void validateManagedHub(Delivery delivery, UUID userId) {
        List<UUID> hubIdList = Optional.ofNullable(hubService.getHubs(new HubSearchDto(), "MASTER", 0, 10).getData())
                .map(hubsPage -> hubsPage.getContent())
                .orElse(Collections.emptyList())
                .stream()
                .filter(hub -> hub.getHubManagerId().equals(userId))
                .map(HubResponseDto::getHubId)
                .toList();

        if (!hubIdList.contains(delivery.getDepartureHubId()) && !hubIdList.contains(delivery.getArrivalHubId())) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
    }

    private void validateDeliveryManagerManagedDelivery(List<DeliveryRouteRecord> routeRecords, List<DeliveryCompanyRouteRecord> companyRouteRecords, UUID userId) {
        boolean hasPermission = false;

        for (DeliveryRouteRecord routeRecord : routeRecords) {
            if (routeRecord.getDeliveryManager() != null && routeRecord.getDeliveryManager().getId().equals(userId)) {
                hasPermission = true;
                break;
            }
        }

        if (!hasPermission) {
            for (DeliveryCompanyRouteRecord companyRouteRecord : companyRouteRecords) {
                if (companyRouteRecord.getDeliveryManager() != null && companyRouteRecord.getDeliveryManager().getId().equals(userId)) {
                    hasPermission = true;
                    break;
                }
            }
        }

        if (!hasPermission) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
    }
}
