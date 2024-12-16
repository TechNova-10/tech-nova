package com.tech_nova.notificaion.infrastructure.client;

import com.tech_nova.notificaion.application.dtos.req.GeminiRequestDto;
import com.tech_nova.notificaion.application.dtos.res.GeminiResponseDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class NotificationClient {

  private final RestTemplate restTemplate;

  @Value("${gemini.api.url}")
  private String apiUrl;

  @Value("${gemini.api.key}")
  private String geminiApiKey;

  public NotificationClient(@Qualifier("geminiRestTemplate") RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public GeminiResponseDto getResponse(String prompt) {
    // Gemini에 요청 전송
    String requestUrl = apiUrl + "?key=" + geminiApiKey;

    GeminiRequestDto request = new GeminiRequestDto(
        prompt + " 다음 배송 요청 정보를 참고하여 최종 발송 기한을 계산하세요, 그리고 입력한 배송 요청 정보와 함께 최종 발송 기한을 보여주세요");
    return restTemplate.postForObject(requestUrl, request, GeminiResponseDto.class);
  }
}
