package com.tech_nova.hub.infrastructure.repository;

import com.tech_nova.hub.application.dtos.req.HubSearchDto;
import com.tech_nova.hub.application.dtos.res.HubResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HubRepositoryCustom {

  Page<HubResponseDto> searchHubs(String role, HubSearchDto hubSearchDto, Pageable pageable);
}
