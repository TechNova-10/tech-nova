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
}