package com.tech_nova.notificaion.application.dtos.res;

import com.tech_nova.notificaion.domain.model.Notification;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NotificationResponseDto {

  private UUID id;

  private String aiRequest;

  private String aiResponse;

  public static NotificationResponseDto of(Notification notification) {
    return NotificationResponseDto.builder()
        .id(notification.getId())
        .aiRequest(notification.getAiRequest())
        .aiResponse(notification.getAiResponse())
        .build();
  }
}
