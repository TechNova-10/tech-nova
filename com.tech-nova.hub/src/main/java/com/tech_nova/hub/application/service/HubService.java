package com.tech_nova.hub.application.service;

import com.tech_nova.hub.application.dtos.req.HubRequestDto;
import com.tech_nova.hub.application.dtos.res.HubClientResponseDto;
import com.tech_nova.hub.application.dtos.res.HubResponseDto;
import java.util.List;
import java.util.UUID;

public interface HubService {

  /**
   * 허브 생성
   *
   * @param hubRequestDto 생성할 허브의 세부 사항을 담고 있는 DTO
   * @param userId        요청을 하는 사용자 ID
   * @param role          요청을 하는 사용자의 역할
   * @return 생성된 허브의 응답 DTO
   */
  HubResponseDto createHub(HubRequestDto hubRequestDto, UUID userId, String role);

  /**
   * 허드 단일 조회
   *
   * @param hubId 조회할 허브의 ID
   * @param role  요청을 하는 사용자의 권한
   * @return 조회된 허브의 응답 DTO
   */
  HubResponseDto getHub(UUID hubId, String role);

  /**
   * 허브 업데이트
   *
   * @param hubId         업데이트할 허브의 ID
   * @param hubRequestDto 업데이트된 허브의 세부 사항을 담고 있는 DTO
   * @param userId        요청을 하는 사용자 ID
   * @param role          요청을 하는 사용자의 권한
   * @return 업데이트된 허브의 응답 DTO
   */
  HubResponseDto updateHub(UUID hubId, HubRequestDto hubRequestDto, UUID userId, String role);

  /**
   * 허브 삭제
   *
   * @param hubId  삭제할 허브의 ID
   * @param userId 요청을 하는 사용자 ID
   * @param role   요청을 하는 사용자의 권한
   */
  void deleteHub(UUID hubId, UUID userId, String role);

  /**
   * 모든 허브의 목록을 조회
   *
   * @return 허브 클라이언트 응답 DTO 목록
   */
  List<HubClientResponseDto> getHubList();
}
