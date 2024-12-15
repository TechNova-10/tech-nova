package com.tech_nova.movementInfo.application.service;

import com.tech_nova.movementInfo.application.dtos.req.MovementRequestDto;
import com.tech_nova.movementInfo.application.dtos.res.MovementResponseDto;
import java.util.UUID;

public interface MovementService {

  /**
   * 허브 간 이동정보 생성
   *
   * @param movementRequestDto Movement 생성에 필요한 정보를 담고 있는 DTO
   * @param userId             요청을 하는 사용자 ID
   * @param role               요청을 하는 사용자의 역할 (예: MASTER, COMPANY 등)
   * @return 생성된 Movement 응답 DTO
   */
  MovementResponseDto createMovement(MovementRequestDto movementRequestDto, UUID userId,
      String role);

  /**
   * 허브 간 이동정보 단일 조회
   *
   * @param movementId 조회할 Movement의 ID
   * @return 조회된 Movement 응답 DTO
   */
  MovementResponseDto getMovement(UUID movementId);

  /**
   * 허브 간 이동정보 삭제
   *
   * @param movementId 삭제할 Movement의 ID
   * @param userId     요청을 하는 사용자 ID
   * @param role       요청을 하는 사용자의 역할 (예: MASTER, COMPANY 등)
   */
  void deleteMovement(UUID movementId, UUID userId, String role);
}
