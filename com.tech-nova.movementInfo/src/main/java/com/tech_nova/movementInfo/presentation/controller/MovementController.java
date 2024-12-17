package com.tech_nova.movementInfo.presentation.controller;

import com.tech_nova.movementInfo.application.dtos.req.MovementRequestDto;
import com.tech_nova.movementInfo.application.dtos.res.MovementResponseDto;
import com.tech_nova.movementInfo.application.service.MovementService;
import com.tech_nova.movementInfo.presentation.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "MovementInfo", description = "허브간 이동 정보 관리 API")

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/movements")
public class MovementController {

  private final MovementService movementService;

  @Operation(
      summary = "허브간 이동 정보 생성",
      description = "사용자가 허브간 이동 정보를 생성할 수 있습니다.",
      responses = {
          @ApiResponse(
              responseCode = "201",
              description = "허브간 이동 정보가 성공적으로 생성되었습니다.",
              content = @Content(schema = @Schema(implementation = MovementResponseDto.class))
          ),
          @ApiResponse(
              responseCode = "400",
              description = "입력 값이 잘못되었습니다."
          )
      }
  )
  @PostMapping
  public ResponseEntity<ApiResponseDto<MovementResponseDto>> createMovement(
      @RequestBody MovementRequestDto movementRequestDto,
      @RequestHeader(value = "X-User-Id", required = true) UUID userId,
      @RequestHeader(value = "X-Role", required = true) String role
  ) {
    return new ResponseEntity<>(
        ApiResponseDto.<MovementResponseDto>builder()
            .code(201)
            .message("허브간 이동 정보 생성 완료")
            .data(movementService.createMovement(movementRequestDto, userId, role))
            .build(),
        HttpStatus.CREATED
    );
  }

  @Operation(
      summary = "허브간 이동 정보 단일 조회",
      description = "허브간 이동 정보의 UUID를 사용하여 허브간 이동 정보를 조회합니다.",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "허브간 이동 정보가 성공적으로 조회되었습니다.",
              content = @Content(schema = @Schema(implementation = MovementResponseDto.class))
          ),
          @ApiResponse(
              responseCode = "404",
              description = "이동 정보를 찾을 수 없습니다."
          )
      }
  )
  @GetMapping("/{movementId}")
  public ResponseEntity<ApiResponseDto<MovementResponseDto>> getMovement(
      @PathVariable UUID movementId
  ) {
    return new ResponseEntity<>(
        ApiResponseDto.<MovementResponseDto>builder()
            .code(200)
            .message("허브간 이동 정보 단일 조회 완료")
            .data(movementService.getMovement(movementId))
            .build(),
        HttpStatus.OK
    );
  }

  @Operation(
      summary = "허브간 이동 정보 삭제",
      description = "이동 ID를 사용하여 허브간 이동 정보를 삭제합니다.",
      responses = {
          @ApiResponse(
              responseCode = "204",
              description = "허브간 이동 정보가 성공적으로 삭제되었습니다."
          ),
          @ApiResponse(
              responseCode = "404",
              description = "삭제할 이동 정보를 찾을 수 없습니다."
          )
      }
  )
  @DeleteMapping("/{movementId}")
  public ResponseEntity<ApiResponseDto<Void>> deleteHub(
      @PathVariable UUID movementId,
      @RequestHeader(value = "X-User-Id", required = true) UUID userId,
      @RequestHeader(value = "X-Role", required = true) String role
  ) {
    movementService.deleteMovement(movementId, userId, role);
    return new ResponseEntity<>(
        ApiResponseDto.<Void>builder()
            .code(204)
            .message("허브간 이동 정보 삭제 완료")
            .build(),
        HttpStatus.OK
    );
  }
}
