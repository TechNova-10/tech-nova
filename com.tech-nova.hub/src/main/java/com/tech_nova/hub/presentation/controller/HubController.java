package com.tech_nova.hub.presentation.controller;

import com.tech_nova.hub.application.dtos.req.HubRequestDto;
import com.tech_nova.hub.application.dtos.req.HubSearchDto;
import com.tech_nova.hub.application.dtos.res.HubClientResponseDto;
import com.tech_nova.hub.application.dtos.res.HubResponseDto;
import com.tech_nova.hub.application.service.HubSearchService;
import com.tech_nova.hub.application.service.HubService;
import com.tech_nova.hub.presentation.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Hub Management", description = "허브 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hubs")
public class HubController {

  private final HubService hubService;
  private final HubSearchService hubSearchService;

  @Operation(
      summary = "허브 생성",
      description = "사용자가 주어진 세부 정보를 통해 새 허브를 생성할 수 있습니다.",
      responses = {
          @ApiResponse(
              responseCode = "201",
              description = "허브가 성공적으로 생성되었습니다.",
              content = @Content(schema = @Schema(implementation = HubResponseDto.class))
          ),
          @ApiResponse(
              responseCode = "400",
              description = "입력 값이 잘못되었습니다."
          )
      }
  )
  @PostMapping
  public ResponseEntity<ApiResponseDto<HubResponseDto>> createHub(
      @RequestBody HubRequestDto hubRequestDto,
      @RequestHeader(value = "X-User-Id", required = true) UUID userId,
      @RequestHeader(value = "X-Role", required = true) String role
  ) {
    return new ResponseEntity<>(
        ApiResponseDto.<HubResponseDto>builder()
            .code(201)
            .message("허브 생성 완료")
            .data(hubService.createHub(hubRequestDto, userId, role))
            .build(),
        HttpStatus.CREATED
    );
  }

  @Operation(
      summary = "허브 단일 조회",
      description = "허브 ID를 사용하여 단일 허브의 세부 정보를 조회합니다.",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "허브가 성공적으로 조회되었습니다.",
              content = @Content(schema = @Schema(implementation = HubResponseDto.class))
          ),
          @ApiResponse(
              responseCode = "404",
              description = "허브를 찾을 수 없습니다."
          )
      }
  )
  @GetMapping("/{hubId}")
  public ResponseEntity<ApiResponseDto<HubResponseDto>> getHub(
      @PathVariable UUID hubId,
      @RequestHeader(value = "X-Role", required = false) String role
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

  @Operation(
      summary = "허브 검색 조회",
      description = "검색 조건에 따라 허브 목록을 페이징 처리하여 조회합니다.",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "허브가 성공적으로 조회되었습니다.",
              content = @Content(schema = @Schema(implementation = Page.class))
          ),
          @ApiResponse(
              responseCode = "400",
              description = "검색 조건이 잘못되었습니다."
          )
      }
  )
  @GetMapping
  public ResponseEntity<ApiResponseDto<Page<HubResponseDto>>> getHubs(
      HubSearchDto hubSearchDto,
      @RequestHeader(value = "X-Role", required = false) String role,
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

  @Operation(
      summary = "허브 수정",
      description = "허브 ID를 사용하여 기존 허브의 세부 정보를 수정합니다.",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "허브가 성공적으로 수정되었습니다.",
              content = @Content(schema = @Schema(implementation = HubResponseDto.class))
          ),
          @ApiResponse(
              responseCode = "404",
              description = "허브를 찾을 수 없습니다."
          )
      }
  )
  @PutMapping("/{hubId}")
  public ResponseEntity<ApiResponseDto<HubResponseDto>> updateHub(
      @PathVariable UUID hubId,
      @RequestBody HubRequestDto hubRequestDto,
      @RequestHeader(value = "X-User-Id", required = true) UUID userId,
      @RequestHeader(value = "X-Role", required = true) String role
  ) {
    return new ResponseEntity<>(
        ApiResponseDto.<HubResponseDto>builder()
            .code(200)
            .message("허브 수정 완료")
            .data(hubService.updateHub(hubId, hubRequestDto, userId, role))
            .build(),
        HttpStatus.OK
    );
  }

  @Operation(
      summary = "허브 삭제",
      description = "허브 ID를 사용하여 기존 허브를 삭제합니다.",
      responses = {
          @ApiResponse(
              responseCode = "204",
              description = "허브가 성공적으로 삭제되었습니다."
          ),
          @ApiResponse(
              responseCode = "404",
              description = "허브를 찾을 수 없습니다."
          )
      }
  )
  @DeleteMapping("/{hubId}")
  public ResponseEntity<ApiResponseDto<Void>> deleteHub(
      @PathVariable UUID hubId,
      @RequestHeader(value = "X-User-Id", required = true) UUID userId,
      @RequestHeader(value = "X-Role", required = true) String role
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

  @Operation(
      summary = "허브 리스트 조회",
      description = "클라이언트를 위해 사용 가능한 허브 목록을 조회합니다.",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "허브 목록이 성공적으로 조회되었습니다.",
              content = @Content(schema = @Schema(implementation = List.class))
          )
      }
  )
  @GetMapping("/clients")
  public List<HubClientResponseDto> getHubList() {
    return hubService.getHubList();
  }
}
