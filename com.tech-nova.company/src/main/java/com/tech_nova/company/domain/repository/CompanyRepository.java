package com.tech_nova.company.domain.repository;

import com.tech_nova.company.domain.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID>, QuerydslPredicateExecutor<Company> {

    // 허브 ID에 속하는 업체만 조회
    List<Company> findAllByHubIdAndIsDeletedFalse(UUID hubId);

    Optional<Company> findByCompanyIdAndIsDeletedFalse(UUID companyId);

}