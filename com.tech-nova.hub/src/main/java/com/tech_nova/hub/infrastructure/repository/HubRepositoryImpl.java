package com.tech_nova.hub.infrastructure.repository;

import com.tech_nova.hub.domain.model.Hub;
import com.tech_nova.hub.domain.repository.HubRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HubRepositoryImpl implements HubRepository {

  private final HubJpaRepository hubJpaRepository;

  @Override
  public void save(Hub hub) {
    hubJpaRepository.save(hub);
  }

  @Override
  public Optional<Hub> findById(UUID hubId) {
    return hubJpaRepository.findById(hubId);
  }

  @Override
  public Optional<Hub> findByHubIdAndIsDeletedFalse(UUID hubId) {
    return hubJpaRepository.findByHubIdAndIsDeletedFalse(hubId);
  }
}
