package com.intelliview.repository;

import com.intelliview.model.CompanyPattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyPatternRepository extends JpaRepository<CompanyPattern, UUID> {
    List<CompanyPattern> findByIsActiveTrueOrderByCompanyName();
    Optional<CompanyPattern> findByCompanyNameIgnoreCase(String companyName);
}
