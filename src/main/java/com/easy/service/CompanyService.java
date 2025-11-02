package com.easy.service;

import com.easy.entity.CompanyMaster;
import com.easy.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CompanyService {

    private final CompanyRepository companyRepository;

    public List<CompanyMaster> getAllActiveCompanies() {
        log.debug("Fetching all active companies");
        return companyRepository.findByIsActiveTrue();
    }

    public List<CompanyMaster> getAllCompanies() {
        log.debug("Fetching all companies");
        return companyRepository.findAll();
    }

    public Optional<CompanyMaster> getCompanyById(UUID id) {
        log.debug("Fetching company with ID: {}", id);
        return companyRepository.findById(id);
    }

    public CompanyMaster getCompanyByGstin(String gstin) {
        log.debug("Fetching company with GSTIN: {}", gstin);
        return companyRepository.findByGstin(gstin);
    }

    public CompanyMaster getCompanyByPan(String pan) {
        log.debug("Fetching company with PAN: {}", pan);
        return companyRepository.findByPan(pan);
    }

    public List<CompanyMaster> searchCompaniesByName(String companyName) {
        log.debug("Searching companies with name containing: {}", companyName);
        return companyRepository.findByCompanyNameContainingIgnoreCase(companyName);
    }
}
