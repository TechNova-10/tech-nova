package com.tech_nova.hub.application.service;

import com.tech_nova.hub.application.dtos.req.HubSearchDto;
import com.tech_nova.hub.application.dtos.res.HubResponseDto;
import com.tech_nova.hub.infrastructure.repository.HubRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HubSearchServiceImpl implements HubSearchService {

  private final HubRepositoryCustom hubRepositoryCustom;

  @Override
  @Transactional(readOnly = true)
  public Page<HubResponseDto> getHubs(String role, HubSearchDto hubSearchDto, Pageable pageable) {

    int pageSize =
        (pageable.getPageSize() == 30
            || pageable.getPageSize() == 50)
            ? pageable.getPageSize() : 10;

    Pageable customPageable = PageRequest.of(
        pageable.getPageNumber(),
        pageSize,
        pageable.getSort()
    );

    Page<HubResponseDto> response =
        hubRepositoryCustom.searchHubs(role, hubSearchDto, customPageable).map(HubResponseDto::of);

    return response;
  }
}
