package com.tech_nova.product.domain.repository;

import com.tech_nova.product.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

@Query("SELECT p FROM Product p WHERE " +
        "(:name IS NULL OR p.name LIKE %:name%) AND " +
        "(:companyId IS NULL OR p.companyId = :companyId) AND " +
        "p.isDeleted = false")
Page<Product> searchByNameAndCompanyId(@Param("name") String name, @Param("companyId") UUID companyId, Pageable pageable);

}
