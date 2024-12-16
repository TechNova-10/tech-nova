package com.tech_nova.notificaion.application.service;

import com.tech_nova.notificaion.application.dtos.res.GeminiResponseDto;
import com.tech_nova.notificaion.application.dtos.res.NotificationResponseDto;
import com.tech_nova.notificaion.domain.model.Notification;
import com.tech_nova.notificaion.domain.repository.NotificationRepository;
import com.tech_nova.notificaion.infrastructure.client.NotificationClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationRepository notificationRepository;
  private final NotificationClient notificationClient;

  @Transactional
  public NotificationResponseDto createNotification(String prompt) {

    GeminiResponseDto response = notificationClient.getResponse(prompt);

    String message = response.getCandidates().get(0).getContent().getParts().get(0).getText();

    Notification notification = Notification.createNotification(prompt, message);

    notificationRepository.save(notification);

    return NotificationResponseDto.of(notification);
  }
}
