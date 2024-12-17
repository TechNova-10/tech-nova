package com.tech_nova.hub.application.service;

import com.tech_nova.hub.application.dtos.req.HubSearchDto;
import com.tech_nova.hub.application.dtos.res.HubResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface HubSearchService {

  /**
   * 허브 search 메서드
   *
   * @param role 사용자 권한
   * @param hubSearchDto 허브 검색 조건을 담고 있는 DTO
   * @param pageable 페이지 정보 (페이지 번호, 크기, 정렬 정보 등)
   * @return 검색된 허브 목록에 대한 페이지 응답 DTO
   */
  Page<HubResponseDto> getHubs(String role, HubSearchDto hubSearchDto, Pageable pageable);
}
