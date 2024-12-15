package com.tech_nova.delivery.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech_nova.delivery.application.dto.LocationData;
import com.tech_nova.delivery.application.dto.RouteEstimateData;
import com.tech_nova.delivery.domain.model.delivery.Delivery;
import com.tech_nova.delivery.domain.model.delivery.DeliveryCompanyRouteRecord;
import com.tech_nova.delivery.domain.repository.DeliveryCompanyRouteRecordRepository;
import lombok.RequiredArgsConstructor;
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
    private final AuthService authService;
    private final GeocodingApiService geocodingApiService;
    private final DirectionsApiService directionsApiService;
    private final GoogleApiService googleApiService;

    private final DeliveryCompanyRouteRecordRepository deliveryCompanyRouteRecordRepository;

    @Transactional
    @Scheduled(cron = "0 0 6 * * ?")
    public void setOrderSequence() {
        // TODO 추후 권한 검증 추가
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

    private boolean validateMaster(String token, Delivery delivery) {
        UUID userId = authService.getUserId(token);
        String userRole = authService.getUserRole(token);

        return "HUB_MANAGER".equals(userRole);
    }
}
