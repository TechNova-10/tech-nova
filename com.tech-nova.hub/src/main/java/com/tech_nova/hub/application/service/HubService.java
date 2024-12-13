package com.tech_nova.hub.application.service;

import com.tech_nova.hub.application.dtos.req.HubRequestDto;
import com.tech_nova.hub.application.dtos.res.HubResponseDto;
import com.tech_nova.hub.domain.model.Hub;
import com.tech_nova.hub.domain.repository.HubRepository;
import com.tech_nova.hub.presentation.exception.HubNotFoundException;
import com.tech_nova.hub.presentation.exception.MasterRoleRequiredException;
import com.tech_nova.hub.presentation.exception.RoleNotAllowedException;
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

  @Transactional(readOnly = true)
  public HubResponseDto getHub(UUID hubId, String role) {

    Hub hub = switch (role) {
      case "MASTER" -> findHubById(hubId);
      case "COMPANY", "DELIVERY", "HUB" -> findByHubIdAndDeletedFalse(hubId);
      default -> throw new RoleNotAllowedException("유효하지 않은 권한입니다.");
    };

    return HubResponseDto.of(hub);
  }

  @Transactional
  public void updateHub(UUID hubId, HubRequestDto hubRequestDto, UUID userId, String role) {

    validateMasterRole(role);
    findHubById(hubId).updateHub(hubRequestDto, userId);
  }

  @Transactional
  public void deleteHub(UUID hubId, UUID userId, String role) {

    validateMasterRole(role);
    findHubById(hubId).deleteHub(userId);
  }

  private void validateMasterRole(String role) {
    if (!"MASTER".equals(role)) {
      throw new MasterRoleRequiredException("마스터 권한을 가진 사용자가 아닙니다.");
    }
  }

  private Hub findHubById(UUID hubId) {
    return hubRepository.findById(hubId)
        .orElseThrow(() -> new HubNotFoundException("해당 허브가 존재하지 않습니다."));
  }

  private Hub findByHubIdAndDeletedFalse(UUID hubId) {
    return hubRepository.findByHubIdAndIsDeletedFalse(hubId)
        .orElseThrow(() -> new HubNotFoundException("해당 허브가 존재하지 않습니다."));
  }
}
