package com.tech_nova.delivery.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech_nova.delivery.application.service.GoogleApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class GoogleApiClient implements GoogleApiService {

    private final RestTemplate restTemplate;

    public GoogleApiClient(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    @Value("${google.api.key}")
    private String apiKey;

    public String generateContent(String text) {
        URI uri = UriComponentsBuilder
                .fromUriString("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent")
                .queryParam("key", apiKey)
                .build()
                .toUri();

        Map<String, Object> requestBodyMap = new HashMap<>();
        Map<String, Object> partsMap = new HashMap<>();
        Map<String, Object> contentMap = new HashMap<>();

        partsMap.put("text", text);
        contentMap.put("parts", Collections.singletonList(partsMap));
        requestBodyMap.put("contents", Collections.singletonList(contentMap));

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = "";
        try {
            requestBody = objectMapper.writeValueAsString(requestBodyMap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

        return responseEntity.getBody();
    }

}