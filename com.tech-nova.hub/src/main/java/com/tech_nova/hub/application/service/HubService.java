package com.tech_nova.hub.application.service;

import com.tech_nova.hub.application.dtos.res.HubRequestDto;
import com.tech_nova.hub.domain.model.Hub;
import com.tech_nova.hub.domain.repository.HubRepository;
import com.tech_nova.hub.presentation.exception.MasterRoleRequiredException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HubService {

  private final HubRepository hubRepository;

  @Transactional
  public void createHub(HubRequestDto hubRequestDto, UUID userId, String role) {

    validateMasterRole(role);
    hubRepository.save(Hub.createHub(hubRequestDto, userId));
  }

  private void validateMasterRole(String role) {
    if (!"MASTER".equals(role)) {
      throw new MasterRoleRequiredException("마스터 권한을 가진 사용자가 아닙니다.");
    }
  }
}
