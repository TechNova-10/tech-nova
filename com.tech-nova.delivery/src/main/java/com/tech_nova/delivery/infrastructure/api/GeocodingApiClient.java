package com.tech_nova.delivery.infrastructure.api;

import com.tech_nova.delivery.application.dto.LocationData;
import com.tech_nova.delivery.application.service.MapService;
import com.tech_nova.delivery.infrastructure.dto.NaverApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class GeocodingApiClient implements MapService {

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

        // Log to verify values
        System.out.println("BaseUrl: " + baseUrl);
        System.out.println("API Key ID: " + apiKeyId);
        System.out.println("API Key: " + apiKey);
    }


    @Override
    public LocationData getCoordinates(String address) {
        System.out.println("Request URL: " + baseUrl + "/geocode?query=" + address);
        System.out.println("Request Headers: ");
        System.out.println("x-ncp-apigw-api-key-id: " + apiKeyId);
        System.out.println("x-ncp-apigw-api-key: " + apiKey);

        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/geocode")
                            .queryParam("query", address)    // 주소 쿼리 파라미터 추가
                            .build())
                    .header("x-ncp-apigw-api-key-id", apiKeyId)  // API Key ID 헤더
                    .header("x-ncp-apigw-api-key", apiKey)      // API Key 헤더
                    .header("Accept", "application/json")       // 응답 형식 설정
                    .retrieve()
                    .bodyToMono(NaverApiResponse.class)
                    .map(response -> {
                        System.out.println(response);
                        if (!response.getAddresses().isEmpty()) {
                            var addressData = response.getAddresses().get(0);
                            double x = Double.parseDouble(addressData.getX());
                            double y = Double.parseDouble(addressData.getY());
                            return new LocationData(x, y);  // 좌표 반환
                        } else {
                            throw new RuntimeException("API 응답에 주소가 없습니다.");
                        }
                    })
                    .block();  // 블로킹 방식으로 결과 받아오기
        } catch (WebClientResponseException e) {
            System.out.println("Error Status: " + e.getStatusCode());
            System.out.println("Error Body: " + e.getResponseBodyAsString());
            throw new RuntimeException("Naver Map API에서 좌표를 가져오지 못했습니다.", e);
        }
    }
}