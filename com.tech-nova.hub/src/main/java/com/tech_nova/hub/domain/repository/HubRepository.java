package com.tech_nova.hub.domain.repository;


import com.tech_nova.hub.domain.model.Hub;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HubRepository {

  void save(Hub hub);

  Optional<Hub> findById(UUID hubId);

  Optional<Hub> findByHubIdAndIsDeletedFalse(UUID hubId);

  List<Hub> findAll();
}
