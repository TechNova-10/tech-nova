package com.tech_nova.company.application.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.tech_nova.company.application.dto.HubResponse;
import com.tech_nova.company.domain.model.QCompany;
import com.tech_nova.company.infrastructure.client.AuthServiceClient;
import com.tech_nova.company.infrastructure.client.HubServiceClient;
import com.tech_nova.company.application.dto.CompanyRequest;
import com.tech_nova.company.application.dto.CompanyResponse;
import com.tech_nova.company.domain.model.Company;
import com.tech_nova.company.domain.model.CompanyType;
import com.tech_nova.company.domain.repository.CompanyRepository;
import com.tech_nova.company.presentation.dto.ApiResponseDto;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final HubServiceClient hubServiceClient;
    private final AuthServiceClient authServiceClient;

    @Transactional
    public ApiResponseDto<Void> createCompany(CompanyRequest requestDto, String token) {

        validateHubId(requestDto.getHubId());

        validateHubManager(token, requestDto.getHubId());

        Company company = Company.builder()
                .hubId(requestDto.getHubId())
                .hubManagerId(requestDto.getHubManagerId())
                .name(requestDto.getName())
                .type(requestDto.getType())
                .province(requestDto.getProvince())
                .city(requestDto.getCity())
                .district(requestDto.getDistrict())
                .street(requestDto.getStreet())
                .build();

        companyRepository.save(company);

        return ApiResponseDto.success("업체 생성 성공", null);
    }


    @Transactional
    public ApiResponseDto<CompanyResponse> updateCompany(UUID companyId, CompanyRequest requestDto, String token) {
        Company existingCompany = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 업체 아이디"));

        validateHubId(requestDto.getHubId());

//        validateUpdatePermission(existingCompany, requestDto, token);

        Company updatedCompany = existingCompany.toBuilder()
                .hubId(requestDto.getHubId())
//                .hubManagerId(requestDto.getHubManagerId())
                .name(requestDto.getName())
                .type(requestDto.getType())
                .province(requestDto.getProvince())
                .city(requestDto.getCity())
                .district(requestDto.getDistrict())
                .street(requestDto.getStreet())
                .build();

        companyRepository.save(updatedCompany);

        return ApiResponseDto.success("업체 수정 성공", new CompanyResponse(updatedCompany));
    }

    @Transactional
    public ApiResponseDto<Void> deleteCompany(UUID companyId, String token) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 업체 아이디"));

        validateHubManager(token, company.getHubId());

        company.softDelete();
        companyRepository.save(company);

        return ApiResponseDto.successDelete();
    }

    // 업체 단건 조회
    @Transactional(readOnly = true)
    public ApiResponseDto<CompanyResponse> getCompanyById(UUID companyId) {
        Company company = companyRepository.findByIdAndIsDeletedFalse(companyId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 업체 아이디"));

        return ApiResponseDto.success("업체 단건 조회 성공", new CompanyResponse(company));
    }

    @Transactional(readOnly = true)
    public ApiResponseDto<List<CompanyResponse>> getCompaniesByHubId(UUID hubId) {
        List<Company> companies = companyRepository.findAllByHubIdAndIsDeletedFalse(hubId);
        List<CompanyResponse> response = companies.stream()
                .map(CompanyResponse::new)
                .collect(Collectors.toList());

        return ApiResponseDto.success("허브 ID로 업체 조회 성공", response);
    }

    //동적 검색 및 페이징 처리
    @Transactional(readOnly = true)
    public ApiResponseDto<Page<CompanyResponse>> searchCompanies(String name, String type, String city, Pageable pageable) {
        BooleanExpression filter = buildFilter(name, type, city);

        Page<Company> companies = companyRepository.findAll(filter, pageable);

        Page<CompanyResponse> response = companies.map(CompanyResponse::new);

        return ApiResponseDto.success("업체 검색 성공", response);
    }

    // 동적 필터 조건 생성
    private BooleanExpression buildFilter(String name, String type, String city) {
        QCompany company = QCompany.company;

        BooleanExpression filter = company.isDeleted.eq(false); // 기본 조건: 삭제되지 않은 데이터만

        if (name != null && !name.isEmpty()) {
            filter = filter.and(company.name.containsIgnoreCase(name)); // 이름 필터 추가
        }

        if (type != null && !type.isEmpty()) {
            try {
                CompanyType companyType = CompanyType.valueOf(type.toUpperCase()); // Enum 변환
                filter = filter.and(company.type.eq(companyType)); // Enum 타입으로 비교
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid company type: " + type);
            }
        }
        if (city != null && !city.isEmpty()) {
            filter = filter.and(company.city.containsIgnoreCase(city)); // city 검색 추가
        }

        return filter;
    }

    private void validateHubId(UUID hubId) {
        boolean isValid = hubServiceClient.isHubIdValid(hubId.toString());
        if (!isValid) {
            throw new IllegalArgumentException("유효하지 않은 hubId!");
        }
    }
//    private void validateHubId(UUID hubId) {
//        try {
//            ResponseEntity<ApiResponseDto<HubResponse>> response = hubServiceClient.getHub(hubId);
//            response.getBody();
//        } catch (Exception e) {
//            throw new IllegalArgumentException("유효하지 않은 hubId!", e);
//        }
//    }


//    private void validateUpdatePermission(Company existingCompany, CompanyRequest requestDto, String token) {
//        String userRole = authServiceClient.getUserRole(token);
//        String userId = authServiceClient.getUserId(token);
//
//        if ("MASTER".equals(userRole)) {
//            // 마스터는 모든 업체 수정 가능
//            return;
//        } else if ("HUB_MANAGER".equals(userRole)) {
//            // 허브 관리자는 해당 허브의 업체만 수정 가능
//            validateHubManager(token, requestDto.getHubId());
//        } else if ("COMPANY_MANAGER".equals(userRole)) {
//            // 업체 관리자는 자신의 업체만 수정 가능
//            if (!existingCompany.getHubManagerId().toString().equals(userId)) {
//                throw new IllegalArgumentException("해당 업체를 수정할 권한이 없습니다.");
//            }
//        } else {
//            throw new IllegalArgumentException("수정 권한이 없는 사용자입니다.");
//        }
//    }


    private void validateHubManager(String token, UUID hubId) {
        String userRole = authServiceClient.getUserRole(token);
        if ("MASTER".equals(userRole)) {
            return;
        }

        String userHubId = authServiceClient.getUserHubId(token);
        if (!hubId.toString().equals(userHubId)) {
            throw new IllegalArgumentException("해당 허브의 업체들 관련해서 관리할 권한이 없습니다.");
        }
    }
}

