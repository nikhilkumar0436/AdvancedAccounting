package com.easy.controller;

import com.easy.entity.CompanyMaster;
import com.easy.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Company Management", description = "APIs for managing companies")
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping
    @Operation(summary = "Get all companies", description = "Retrieve all companies")
    public ResponseEntity<List<CompanyMaster>> getAllCompanies() {
        log.info("Fetching all companies");
        List<CompanyMaster> companies = companyService.getAllCompanies();
        return ResponseEntity.ok(companies);
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active companies", description = "Retrieve all active companies")
    public ResponseEntity<List<CompanyMaster>> getAllActiveCompanies() {
        log.info("Fetching all active companies");
        List<CompanyMaster> companies = companyService.getAllActiveCompanies();
        return ResponseEntity.ok(companies);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get company by ID", description = "Retrieve a company by its ID")
    public ResponseEntity<CompanyMaster> getCompanyById(@PathVariable UUID id) {
        log.info("Fetching company with ID: {}", id);
        return companyService.getCompanyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/gstin/{gstin}")
    @Operation(summary = "Get company by GSTIN", description = "Retrieve a company by its GSTIN")
    public ResponseEntity<CompanyMaster> getCompanyByGstin(@PathVariable String gstin) {
        log.info("Fetching company with GSTIN: {}", gstin);
        CompanyMaster company = companyService.getCompanyByGstin(gstin);
        return company != null ? ResponseEntity.ok(company) : ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search companies by name", description = "Search companies by name")
    public ResponseEntity<List<CompanyMaster>> searchCompaniesByName(@RequestParam String name) {
        log.info("Searching companies with name: {}", name);
        List<CompanyMaster> companies = companyService.searchCompaniesByName(name);
        return ResponseEntity.ok(companies);
    }
}
