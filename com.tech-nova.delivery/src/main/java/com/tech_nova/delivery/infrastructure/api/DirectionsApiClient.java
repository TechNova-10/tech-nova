package com.tech_nova.delivery.infrastructure.api;

import com.tech_nova.delivery.application.dto.RouteEstimateData;
import com.tech_nova.delivery.application.service.DirectionsApiService;
import com.tech_nova.delivery.infrastructure.dto.NaverApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class DirectionsApiClient implements DirectionsApiService {

    private final WebClient webClient;

    private final String baseUrl;
    private final String apiKeyId;
    private final String apiKey;

    public DirectionsApiClient(WebClient.Builder webClientBuilder,
                               @Value("${naver.map.directions-url}") String baseUrl,
                               @Value("${naver.map.api-key-id}") String apiKeyId,
                               @Value("${naver.map.api-key}") String apiKey) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.baseUrl = baseUrl;
        this.apiKeyId = apiKeyId;
        this.apiKey = apiKey;
    }

    @Override
    @Cacheable(value = "directionsCache", key = "#start + '-' + #goal")
    public RouteEstimateData getRouteEstimateData(String start, String goal) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/driving")
                            .queryParam("start", start)
                            .queryParam("goal", goal)
                            .build())
                    .header("x-ncp-apigw-api-key-id", apiKeyId)
                    .header("x-ncp-apigw-api-key", apiKey)
                    .header("Accept", "application/json")
                    .retrieve()
                    .bodyToMono(NaverApiResponse.class)
                    .map(response -> {
                        if (response != null && response.getRoute() != null &&
                                !response.getRoute().getTraoptimal().isEmpty()) {
                            var traoptimal = response.getRoute().getTraoptimal().get(0);
                            int distance = traoptimal.getSummary().getDistance();
                            int duration = traoptimal.getSummary().getDuration();

                            return new RouteEstimateData(distance, duration);
                        } else {
                            throw new RuntimeException("API 응답에 경로 정보가 없습니다.");
                        }
                    })
                    .block();
        } catch (WebClientResponseException e) {
            System.out.println("Error Status: " + e.getStatusCode());
            System.out.println("Error Body: " + e.getResponseBodyAsString());
            throw new RuntimeException("Naver Map API에서 경로 정보를 가져오지 못했습니다.", e);
        }
    }

    @Override
    public void getWaypointsSequence(String start, String goal, String waypoints) {
        System.out.println(">> start" + start);
        System.out.println(">> goal" + goal);
        System.out.println(">> waypoints" + waypoints);

        // WebClient 호출 및 결과 처리
        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/driving")
                        .queryParam("start", start)
                        .queryParam("goal", goal)
                        .queryParam("waypoints", waypoints)
                        .build())
                .header("x-ncp-apigw-api-key-id", apiKeyId)
                .header("x-ncp-apigw-api-key", apiKey)
                .header("Accept", "application/json")
                .retrieve()
                .bodyToMono(NaverApiResponse.class)
                .doOnTerminate(() -> System.out.println("Request Completed"))
                .doOnError(e -> {
                    // 오류 발생 시 처리
                    System.out.println("Error Status: " + e.getClass());
                    System.out.println("Error Message: " + e.getMessage());
                })
                .map(response -> {
                    // 응답이 있을 경우 처리
                    System.out.println(response);
                    if (response == null) {
                        throw new RuntimeException("API 응답에 경로 정보가 없습니다.");
                    }

                    // trafast 경로의 첫 번째 최적 경로를 사용

                    // List<LocationData> locationDataList = new ArrayList<>();
                    // // 경로 상에 있는 각 지점의 위치를 추가
                    // List<List<Double>> path = optimalRoute.getPath();
                    // for (List<Double> location : path) {
                    //     locationDataList.add(new LocationData(location.get(0), location.get(1))); // 경도, 위도
                    // }

                    // 리스트 처리 완료 후 추가 작업이 필요하다면 여기에 넣기
                    // return locationDataList;
                    // 응답만 출력하고 다른 처리는 생략
                    return response;  // 응답 객체를 그대로 반환
                })
                .doOnTerminate(() -> {
                    // 데이터 처리가 끝난 후에 해야 할 작업이 있다면 여기에
                    System.out.println("Processing completed");
                })
                .block();  // 비동기 처리를 동기 방식으로 기다리기 위해 사용 (예시에서는 동기 처리로 바꿈)
    }

}