package com.tech_nova.delivery.infrastructure.api;

import com.tech_nova.delivery.application.dto.LocationData;
import com.tech_nova.delivery.application.service.GeocodingApiService;
import com.tech_nova.delivery.infrastructure.dto.GeocodingApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class GeocodingApiClient implements GeocodingApiService {

    private final WebClient webClient;

    private final String baseUrl;
    private final String apiKeyId;
    private final String apiKey;

    public GeocodingApiClient(WebClient.Builder webClientBuilder,
                              @Value("${naver.map.geocoding-url}") String baseUrl,
                              @Value("${naver.map.api-key-id}") String apiKeyId,
                              @Value("${naver.map.api-key}") String apiKey) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.baseUrl = baseUrl;
        this.apiKeyId = apiKeyId;
        this.apiKey = apiKey;
    }


    @Override
    @Cacheable(value = "locationCache", key = "#address")
    public LocationData getCoordinates(String address) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/geocode")
                            .queryParam("query", address)
                            .build())
                    .header("x-ncp-apigw-api-key-id", apiKeyId)
                    .header("x-ncp-apigw-api-key", apiKey)
                    .header("Accept", "application/json")
                    .retrieve()
                    .bodyToMono(GeocodingApiResponse.class)
                    .map(response -> {
                        if (!response.getAddresses().isEmpty()) {
                            var addressData = response.getAddresses().get(0);
                            double x = Double.parseDouble(addressData.getX());
                            double y = Double.parseDouble(addressData.getY());
                            return new LocationData(x, y);
                        } else {
                            throw new RuntimeException("API 응답에 주소가 없습니다.");
                        }
                    })
                    .block();
        } catch (WebClientResponseException e) {
            System.out.println("Error Status: " + e.getStatusCode());
            System.out.println("Error Body: " + e.getResponseBodyAsString());
            throw new RuntimeException("Naver Map API에서 좌표를 가져오지 못했습니다.", e);
        }
    }
}