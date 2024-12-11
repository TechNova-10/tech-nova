package com.tech_nova.company.application.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.tech_nova.company.application.dto.CompanyRequest;
import com.tech_nova.company.application.dto.CompanyResponse;
import com.tech_nova.company.domain.model.Company;
import com.tech_nova.company.domain.model.QCompany;
import com.tech_nova.company.domain.model.CompanyType;
import com.tech_nova.company.domain.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    @Transactional
    public CompanyResponse createCompany(CompanyRequest requestDto) {
        Company company = Company.builder()
                .name(requestDto.getName())
                .type(requestDto.getType())
                .hubId(requestDto.getHubId())
                .province(requestDto.getProvince())
                .city(requestDto.getCity())
                .district(requestDto.getDistrict())
                .street(requestDto.getStreet())
                .build();

        companyRepository.save(company);

        return new CompanyResponse(company);
    }

    @Transactional
    public CompanyResponse updateCompany(UUID companyId, CompanyRequest requestDto) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found with ID: " + companyId));

        company.setName(requestDto.getName());
        company.setType(requestDto.getType());
        company.setHubId(requestDto.getHubId());
        company.setProvince(requestDto.getProvince());
        company.setCity(requestDto.getCity());
        company.setDistrict(requestDto.getDistrict());
        company.setStreet(requestDto.getStreet());

        return new CompanyResponse(company);
    }

    @Transactional
    public void deleteCompany(UUID companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found with ID: " + companyId));

        company.softDelete();
        companyRepository.save(company);
    }

    @Transactional(readOnly = true)
    public List<CompanyResponse> getCompaniesByHubId(UUID hubId) {
        List<Company> companies = companyRepository.findAllByHubIdAndIsDeletedFalse(hubId);
        return companies.stream()
                .map(CompanyResponse::new)
                .collect(Collectors.toList());
    }

    // 동적 검색 및 페이징 처리
    @Transactional(readOnly = true)
    public Page<CompanyResponse> searchCompanies(String name, String type, String city, Pageable pageable) {
        BooleanExpression filter = buildFilter(name, type, city);

        Page<Company> companies = companyRepository.findAll(filter, pageable);

        return companies.map(CompanyResponse::new);
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
}
