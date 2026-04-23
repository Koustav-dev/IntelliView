package com.intelliview.controller;

import com.intelliview.dto.ApiResponse;
import com.intelliview.model.CompanyPattern;
import com.intelliview.repository.CompanyPatternRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyPatternRepository companyRepo;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CompanyPattern>>> getAllCompanies() {
        List<CompanyPattern> companies = companyRepo.findByIsActiveTrueOrderByCompanyName();
        return ResponseEntity.ok(ApiResponse.success(companies));
    }

    @GetMapping("/{name}")
    public ResponseEntity<ApiResponse<CompanyPattern>> getCompany(@PathVariable String name) {
        CompanyPattern company = companyRepo.findByCompanyNameIgnoreCase(name)
                .orElseThrow(() -> new IllegalArgumentException("Company not found: " + name));
        return ResponseEntity.ok(ApiResponse.success(company));
    }
}
