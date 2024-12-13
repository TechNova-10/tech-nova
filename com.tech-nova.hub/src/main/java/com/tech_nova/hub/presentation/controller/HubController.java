package com.tech_nova.hub.presentation.controller;

import com.tech_nova.hub.application.dtos.req.HubRequestDto;
import com.tech_nova.hub.application.dtos.req.HubSearchDto;
import com.tech_nova.hub.application.dtos.res.HubClientResponseDto;
import com.tech_nova.hub.application.dtos.res.HubResponseDto;
import com.tech_nova.hub.application.service.HubSearchService;
import com.tech_nova.hub.application.service.HubService;
import com.tech_nova.hub.presentation.dto.ApiResponseDto;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hubs")
public class HubController {

  private final HubService hubService;
  private final HubSearchService hubSearchService;

  @PostMapping
  public ResponseEntity<ApiResponseDto<Void>> createHub(
      @RequestBody HubRequestDto hubRequestDto,
      @RequestHeader(value = "user_id", required = true) UUID userId,
      @RequestHeader(value = "role", required = true) String role
  ) {
    hubService.createHub(hubRequestDto, userId, role);
    return new ResponseEntity<>(
        ApiResponseDto.<Void>builder()
            .code(201)
            .message("허브 생성 완료")
            .build(),
        HttpStatus.CREATED
    );
  }

  @GetMapping("/{hubId}")
  public ResponseEntity<ApiResponseDto<HubResponseDto>> getHub(
      @PathVariable UUID hubId,
      @RequestHeader(value = "role", required = false) String role
  ) {
    return new ResponseEntity<>(
        ApiResponseDto.<HubResponseDto>builder()
            .code(200)
            .message("허브 단일 조회 완료")
            .data(hubService.getHub(hubId, role))
            .build(),
        HttpStatus.OK
    );
  }

  @GetMapping
  public ResponseEntity<ApiResponseDto<Page<HubResponseDto>>> getHubs(
      HubSearchDto hubSearchDto,
      @RequestHeader(value = "role", required = false) String role,
      Pageable pageable
  ) {
    return new ResponseEntity<>(
        ApiResponseDto.<Page<HubResponseDto>>builder()
            .code(200)
            .message("허브 검색 조회 완료")
            .data(hubSearchService.getHubs(role, hubSearchDto, pageable))
            .build(),
        HttpStatus.OK
    );
  }


  @PutMapping("/{hubId}")
  public ResponseEntity<ApiResponseDto<Void>> updateHub(
      @PathVariable UUID hubId,
      @RequestBody HubRequestDto hubRequestDto,
      @RequestHeader(value = "user_id", required = true) UUID userId,
      @RequestHeader(value = "role", required = true) String role
  ) {
    hubService.updateHub(hubId, hubRequestDto, userId, role);
    return new ResponseEntity<>(
        ApiResponseDto.<Void>builder()
            .code(200)
            .message("허브 수정 완료")
            .build(),
        HttpStatus.OK
    );
  }

  @DeleteMapping("/{hubId}")
  public ResponseEntity<ApiResponseDto<Void>> deleteHub(
      @PathVariable UUID hubId,
      @RequestHeader(value = "user_id", required = true) UUID userId,
      @RequestHeader(value = "role", required = true) String role
  ) {
    hubService.deleteHub(hubId, userId, role);
    return new ResponseEntity<>(
        ApiResponseDto.<Void>builder()
            .code(204)
            .message("허브 삭제 완료")
            .build(),
        HttpStatus.OK
    );
  }

  @GetMapping("/client")
  public List<HubClientResponseDto> getHubList() {
    return hubService.getHubList();
  }
}
