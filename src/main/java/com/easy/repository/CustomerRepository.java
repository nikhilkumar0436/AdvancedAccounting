package com.easy.repository;

import com.easy.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID>, JpaSpecificationExecutor<Customer> {

    // Find all customers with specification support for complex filtering
    Page<Customer> findAll(Specification<Customer> specification, Pageable pageable);

    // Additional custom query methods
    Optional<Customer> findByGstin(String gstin);

    Optional<Customer> findByPan(String pan);

    @Query("SELECT c FROM Customer c WHERE c.company.id = :companyId")
    List<Customer> findByCompanyId(@Param("companyId") UUID companyId);

    @Query("SELECT c FROM Customer c WHERE c.company.id = :companyId AND c.isActive = :isActive")
    List<Customer> findByCompanyIdAndIsActive(@Param("companyId") UUID companyId, @Param("isActive") Boolean isActive);

    List<Customer> findByCustomerNameContainingIgnoreCase(String customerName);

    List<Customer> findByEmailIgnoreCase(String email);

    List<Customer> findByPhoneOrMobile(String phone, String mobile);

    List<Customer> findByCityIgnoreCase(String city);

    List<Customer> findByStateIgnoreCase(String state);

    List<Customer> findByPincode(String pincode);

    List<Customer> findByCustomerType(Customer.CustomerType customerType);

    List<Customer> findByIsCashCustomer(Boolean isCashCustomer);

    // Method to find customers by company and multiple criteria
    @Query("SELECT c FROM Customer c WHERE c.company.id = :companyId AND c.customerType = :customerType AND c.isActive = :isActive")
    List<Customer> findByCompanyIdAndCustomerTypeAndIsActive(
        @Param("companyId") UUID companyId,
        @Param("customerType") Customer.CustomerType customerType,
        @Param("isActive") Boolean isActive
    );
}

