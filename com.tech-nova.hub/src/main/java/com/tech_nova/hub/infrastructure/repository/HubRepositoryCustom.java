package com.tech_nova.hub.infrastructure.repository;

import com.tech_nova.hub.application.dtos.req.HubSearchDto;
import com.tech_nova.hub.domain.model.Hub;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HubRepositoryCustom {

  Page<Hub> searchHubs(String role, HubSearchDto hubSearchDto, Pageable pageable);
}
