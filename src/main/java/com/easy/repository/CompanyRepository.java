package com.easy.repository;

import com.easy.entity.CompanyMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyMaster, UUID> {

    List<CompanyMaster> findByIsActiveTrue();

    CompanyMaster findByGstin(String gstin);

    CompanyMaster findByPan(String pan);

    List<CompanyMaster> findByCompanyNameContainingIgnoreCase(String companyName);
}
