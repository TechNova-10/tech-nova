package com.tech_nova.delivery.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech_nova.delivery.application.dto.LocationData;
import com.tech_nova.delivery.domain.model.delivery.Delivery;
import com.tech_nova.delivery.domain.model.delivery.DeliveryCompanyRouteRecord;
import com.tech_nova.delivery.domain.repository.DeliveryCompanyRouteRecordRepository;
import com.tech_nova.delivery.domain.repository.DeliveryManagerRepository;
import com.tech_nova.delivery.domain.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryCompanyRouteRecordService {
    private final AuthService authService;
    private final GeocodingApiService geocodingApiService;
    private final DirectionsApiService directionsApiService;
    private final GoogleApiService googleApiService;

    private final DeliveryRepository deliveryRepository;
    private final DeliveryCompanyRouteRecordRepository deliveryCompanyRouteRecordRepository;
    private final DeliveryManagerRepository deliveryManagerRepository;

    @Transactional
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
                        updateDeliveryRouteOrder(records, optimizedOrder);

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


    private static String getString(List<LocationData> locationDatas) {
        StringBuilder waypoints = new StringBuilder();

        if (locationDatas.size() > 2) {
            for (int i = 1; i < locationDatas.size() - 1; i++) {
                LocationData current = locationDatas.get(i);
                String waypoint = current.getLongitude() + "," + current.getLatitude();

                if (i < locationDatas.size() - 1 && current.equals(locationDatas.get(i + 1))) {
                    waypoint = current.getLongitude() + "," + current.getLatitude() + ":" +
                            locationDatas.get(i + 1).getLongitude() + "," + locationDatas.get(i + 1).getLatitude();
                    i++;
                }

                if (i > 1) {
                    waypoints.append("|");
                }
                waypoints.append(waypoint);
            }
        } else if (locationDatas.size() == 2) {
            waypoints.append(locationDatas.get(0).getLongitude() + "," + locationDatas.get(0).getLatitude());
            waypoints.append("|");
            waypoints.append(locationDatas.get(1).getLongitude() + "," + locationDatas.get(1).getLatitude());
        } else if (locationDatas.size() == 1) {
            waypoints.append(locationDatas.get(0).getLongitude() + "," + locationDatas.get(0).getLatitude());
        }

        return waypoints.toString();
    }


    public LocationData fetchCoordinates(String address) {
        return geocodingApiService.getCoordinates(address);
    }

    private void updateDeliveryRouteOrder(List<DeliveryCompanyRouteRecord> records, List<Integer> optimizedOrder) {
        for (int i = 0; i < records.size(); i++) {
            DeliveryCompanyRouteRecord record = records.get(i);
            Delivery delivery = record.getDelivery();
            delivery.updateCompanyRouteRecordOrderSequence(record.getId(), optimizedOrder.get(i));
        }
    }

    private boolean validateMaster(String token, Delivery delivery) {
        UUID userId = authService.getUserId(token);
        String userRole = authService.getUserRole(token);

        return "HUB_MANAGER".equals(userRole);
    }
}
