package com.tech_nova.company.presentation.controller;

import com.tech_nova.company.application.dto.CompanyRequest;
import com.tech_nova.company.application.dto.CompanyResponse;
import com.tech_nova.company.application.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    // 업체 생성
    @PostMapping
    public ResponseEntity<CompanyResponse> createCompany(@RequestBody CompanyRequest requestDto) {
        CompanyResponse response = companyService.createCompany(requestDto);
        return ResponseEntity.ok(response); // 200 OK와 함께 생성된 업체 반환
    }

    // 업체 수정
    @PutMapping("/{companyId}")
    public ResponseEntity<CompanyResponse> updateCompany(
            @PathVariable UUID companyId,
            @RequestBody CompanyRequest requestDto) {
        CompanyResponse response = companyService.updateCompany(companyId, requestDto);
        return ResponseEntity.ok(response); // 200 OK와 함께 수정된 업체 반환
    }

    // 업체 삭제 (논리적 삭제)
    @DeleteMapping("/{companyId}")
    public ResponseEntity<Void> deleteCompany(@PathVariable UUID companyId) {
        companyService.deleteCompany(companyId);
        return ResponseEntity.noContent().build(); // 204 No Content 반환
    }

    // 허브 ID로 업체 조회
    @GetMapping("/hub/{hubId}")
    public ResponseEntity<List<CompanyResponse>> getCompaniesByHubId(@PathVariable UUID hubId) {
        List<CompanyResponse> responseList = companyService.getCompaniesByHubId(hubId);
        return ResponseEntity.ok(responseList); // 200 OK와 함께 결과 반환
    }

    // 검색 및 페이징 처리
    @GetMapping
    public ResponseEntity<Page<CompanyResponse>> searchCompanies(
            @RequestParam(required = false) String name, // 검색 조건: 이름
            @RequestParam(required = false) String type, // 검색 조건: 타입
            @RequestParam(required = false) String city, // 검색 조건: 도시
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable // 기본 페이지 크기 및 정렬 설정
    ) {
        Page<CompanyResponse> responsePage = companyService.searchCompanies(name, type, city, pageable);
        return ResponseEntity.ok(responsePage); // 200 OK와 함께 결과 반환
    }
}
