package com.tech_nova.hub.infrastructure.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tech_nova.hub.application.dtos.req.HubSearchDto;
import com.tech_nova.hub.application.dtos.res.HubResponseDto;
import com.tech_nova.hub.domain.model.QHub;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class HubRepositoryImpl implements HubRepositoryCustom{

  private final JPAQueryFactory queryFactory;
  private final QHub hub = QHub.hub;

  @Override
  public Page<HubResponseDto> searchHubs(String role, HubSearchDto hubSearchDto, Pageable pageable) {

    return null;
  }
}
