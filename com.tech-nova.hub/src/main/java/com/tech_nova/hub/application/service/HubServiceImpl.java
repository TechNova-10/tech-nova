package com.tech_nova.hub.application.service;

import com.tech_nova.hub.application.dtos.req.HubRequestDto;
import com.tech_nova.hub.application.dtos.res.HubClientResponseDto;
import com.tech_nova.hub.application.dtos.res.HubResponseDto;
import com.tech_nova.hub.domain.model.Hub;
import com.tech_nova.hub.domain.repository.HubRepository;
import com.tech_nova.hub.presentation.exception.HubNotFoundException;
import com.tech_nova.hub.presentation.exception.MasterRoleRequiredException;
import com.tech_nova.hub.presentation.exception.RoleNotAllowedException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HubServiceImpl implements HubService {

  private final HubRepository hubRepository;

  @Override
  @CachePut(cacheNames = "HubCache", key = "#result.hubId")
  @Transactional
  public HubResponseDto createHub(HubRequestDto hubRequestDto, UUID userId, String role) {

    validateMasterRole(role);

    Hub hub = Hub.createHub(hubRequestDto, userId);
    hubRepository.save(hub);

    return HubResponseDto.of(hub);
  }

  @Override
  @Transactional(readOnly = true)
  public HubResponseDto getHub(UUID hubId, String role) {

    Hub hub = switch (role) {
      case "MASTER" -> findHubById(hubId);
      case "COMPANY", "DELIVERY", "HUB" -> findByHubIdAndDeletedFalse(hubId);
      default -> throw new RoleNotAllowedException("유효하지 않은 권한입니다.");
    };

    return HubResponseDto.of(hub);
  }

  @Override
  @Caching(
      put = @CachePut(cacheNames = "HubCache", key = "#hubId"),
      evict = @CacheEvict(cacheNames = "HubAllCache", allEntries = true)
  )
  @Transactional
  public HubResponseDto updateHub(UUID hubId, HubRequestDto hubRequestDto, UUID userId, String role) {

    validateMasterRole(role);

    Hub hub = findHubById(hubId);
    hub.updateHub(hubRequestDto, userId);

    return HubResponseDto.of(hub);
  }

  @Override
  @CacheEvict(cacheNames = {"HubCache", "HubAllCache"}, allEntries = true)
  @Transactional
  public void deleteHub(UUID hubId, UUID userId, String role) {

    validateMasterRole(role);
    findHubById(hubId).deleteHub(userId);
  }

  @Override
  @Cacheable(cacheNames = "HubAllCache", key = "methodName")
  @Transactional(readOnly = true)
  public List<HubClientResponseDto> getHubList() {
    return hubRepository.findAll()
        .stream()
        .map(HubClientResponseDto::of)
        .toList();
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
