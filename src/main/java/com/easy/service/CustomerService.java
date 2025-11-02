package com.easy.service;

import com.easy.dto.CustomerMapper;
import com.easy.dto.CustomerRequest;
import com.easy.dto.CustomerResponse;
import com.easy.entity.Customer;
import com.easy.entity.CompanyMaster;
import com.easy.repository.CustomerRepository;
import com.easy.repository.CompanyRepository;
import com.easy.request.customer.CustomRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final CompanyRepository companyRepository;

    public Page<Customer> getCustomersWithFilters(CustomRequest request) {

        Specification<Customer> specification = createSpecification(request.getFilterBy());
        Pageable pageable = createPageable(request);
        return customerRepository.findAll(specification, pageable);
    }

    public Optional<Customer> getCustomerById(UUID id) {
        return customerRepository.findById(id);
    }

    public List<Customer> getCustomersByCompany(UUID companyId) {
        return customerRepository.findByCompanyId(companyId);
    }

    public Optional<Customer> getCustomerByGstin(String gstin) {
        return customerRepository.findByGstin(gstin);
    }

    public Optional<Customer> getCustomerByPan(String pan) {
        return customerRepository.findByPan(pan);
    }

    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    // ...existing code...

    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request) {
        log.info("Creating new customer with name: {}", request.getCustomerName());

        // Validate business rules
        validateCustomerRequest(request);

        // Check for duplicate GSTIN if provided
        if (request.getGstin() != null && !request.getGstin().trim().isEmpty()) {
            if (customerRepository.findByGstin(request.getGstin()).isPresent()) {
                throw new IllegalArgumentException("Customer with GSTIN " + request.getGstin() + " already exists");
            }
        }

        // Check for duplicate PAN if provided
        if (request.getPan() != null && !request.getPan().trim().isEmpty()) {
            if (customerRepository.findByPan(request.getPan()).isPresent()) {
                throw new IllegalArgumentException("Customer with PAN " + request.getPan() + " already exists");
            }
        }

        Customer customer = customerMapper.toEntity(request);

        // Set company entity based on companyId
        if (request.getCompanyId() != null) {
            CompanyMaster company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + request.getCompanyId()));
            customer.setCompany(company);
        }

        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer created successfully with ID: {}", savedCustomer.getId());

        return customerMapper.toResponse(savedCustomer);
    }

    @Transactional
    public CustomerResponse updateCustomer(UUID id, CustomerRequest request) {
        log.info("Updating customer with ID: {}", id);

        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + id));

        // Validate business rules
        validateCustomerRequest(request);

        // Check for duplicate GSTIN if changed
        if (request.getGstin() != null && !request.getGstin().trim().isEmpty()
            && !request.getGstin().equals(existingCustomer.getGstin())) {
            if (customerRepository.findByGstin(request.getGstin()).isPresent()) {
                throw new IllegalArgumentException("Customer with GSTIN " + request.getGstin() + " already exists");
            }
        }

        // Check for duplicate PAN if changed
        if (request.getPan() != null && !request.getPan().trim().isEmpty()
            && !request.getPan().equals(existingCustomer.getPan())) {
            if (customerRepository.findByPan(request.getPan()).isPresent()) {
                throw new IllegalArgumentException("Customer with PAN " + request.getPan() + " already exists");
            }
        }

        customerMapper.updateEntity(request, existingCustomer);

        // Update company if companyId changed
        if (request.getCompanyId() != null &&
            (existingCustomer.getCompany() == null || !request.getCompanyId().equals(existingCustomer.getCompany().getId()))) {
            CompanyMaster company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + request.getCompanyId()));
            existingCustomer.setCompany(company);
        }

        Customer updatedCustomer = customerRepository.save(existingCustomer);
        log.info("Customer updated successfully with ID: {}", updatedCustomer.getId());

        return customerMapper.toResponse(updatedCustomer);
    }

    public CustomerResponse getCustomerResponseById(UUID id) {
        log.debug("Fetching customer with ID: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + id));
        return customerMapper.toResponse(customer);
    }

    public List<CustomerResponse> getCustomerResponsesByCompany(UUID companyId) {
        log.debug("Fetching customers for company ID: {}", companyId);
        List<Customer> customers = customerRepository.findByCompanyId(companyId);
        return customers.stream()
                .map(customerMapper::toResponse)
                .toList();
    }

    @Transactional
    public void deleteCustomer(UUID id) {
        log.info("Deleting customer with ID: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + id));

        // TODO: Add business validation - check if customer has pending invoices
        // List<InvoiceMaster> pendingInvoices = invoiceRepository.findByCustomerIdAndStatus(id, InvoiceStatus.PENDING);
        // if (!pendingInvoices.isEmpty()) {
        //     throw new IllegalStateException("Cannot delete customer with pending invoices");
        // }

        customerRepository.deleteById(id);
        log.info("Customer deleted successfully with ID: {}", id);
    }

    @Transactional
    public CustomerResponse updateCustomerStatus(UUID id, Boolean isActive) {
        log.info("Updating customer status for ID: {} to {}", id, isActive);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + id));

        customer.setIsActive(isActive);
        Customer updatedCustomer = customerRepository.save(customer);
        log.info("Customer status updated successfully for ID: {}", id);

        return customerMapper.toResponse(updatedCustomer);
    }

    private void validateCustomerRequest(CustomerRequest request) {
        // Additional business validation beyond bean validation
        if (request.getCreditLimit() != null && request.getCreditLimit().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Credit limit cannot be negative");
        }

        if (request.getOpeningBalance() != null && request.getOpeningBalance().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Opening balance cannot be negative");
        }
    }

    private Specification<Customer> createSpecification(CustomRequest.FilterBy filterBy) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // Default predicate to ensure valid WHERE clause)
            if (filterBy != null) {
                // Basic customer information filters
                if (filterBy.getName() != null && !filterBy.getName().trim().isEmpty()) {
                    predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("customerName")),
                        "%" + filterBy.getName().toLowerCase() + "%"
                    ));
                }

                if (filterBy.getGstin() != null && !filterBy.getGstin().trim().isEmpty()) {
                    predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("gstin")),
                        "%" + filterBy.getGstin().toLowerCase() + "%"
                    ));
                }

                if (filterBy.getPan() != null && !filterBy.getPan().trim().isEmpty()) {
                    predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("pan")),
                        "%" + filterBy.getPan().toLowerCase() + "%"
                    ));
                }

                if (filterBy.getCustomerType() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("customerType"), filterBy.getCustomerType()));
                }

                if (filterBy.getEmail() != null && !filterBy.getEmail().trim().isEmpty()) {
                    predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("email")),
                        "%" + filterBy.getEmail().toLowerCase() + "%"
                    ));
                }

                if (filterBy.getPhone() != null && !filterBy.getPhone().trim().isEmpty()) {
                    predicates.add(criteriaBuilder.like(root.get("phone"), "%" + filterBy.getPhone() + "%"));
                }

                if (filterBy.getMobile() != null && !filterBy.getMobile().trim().isEmpty()) {
                    predicates.add(criteriaBuilder.like(root.get("mobile"), "%" + filterBy.getMobile() + "%"));
                }

                // Location-based filters
                if (filterBy.getCity() != null && !filterBy.getCity().trim().isEmpty()) {
                    predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("city")),
                        "%" + filterBy.getCity().toLowerCase() + "%"
                    ));
                }

                if (filterBy.getState() != null && !filterBy.getState().trim().isEmpty()) {
                    predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("state")),
                        "%" + filterBy.getState().toLowerCase() + "%"
                    ));
                }

                if (filterBy.getStateCode() != null && !filterBy.getStateCode().trim().isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("stateCode"), filterBy.getStateCode()));
                }

                if (filterBy.getPincode() != null && !filterBy.getPincode().trim().isEmpty()) {
                    predicates.add(criteriaBuilder.equal(root.get("pincode"), filterBy.getPincode()));
                }
                // Status filters
                if (filterBy.getIsActive() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("isActive"), filterBy.getIsActive()));
                }

                if (filterBy.getIsCashCustomer() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("isCashCustomer"), filterBy.getIsCashCustomer()));
                }

                // Date range filters
                if (filterBy.getCreatedDateFrom() != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), filterBy.getCreatedDateFrom().atStartOfDay()));
                }

                if (filterBy.getCreatedDateTo() != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), filterBy.getCreatedDateTo().atTime(23, 59, 59)));
                }

                if (filterBy.getUpdatedDateFrom() != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("updatedAt"), filterBy.getUpdatedDateFrom().atStartOfDay()));
                }

                if (filterBy.getUpdatedDateTo() != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("updatedAt"), filterBy.getUpdatedDateTo().atTime(23, 59, 59)));
                }

                // Company context (essential for multi-tenant)
                if (filterBy.getCompanyId() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("company").get("id"), filterBy.getCompanyId()));
                }

                // Advanced filters
                if (filterBy.getCustomerCategory() != null && !filterBy.getCustomerCategory().trim().isEmpty()) {
                    predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("customerCategory")),
                        "%" + filterBy.getCustomerCategory().toLowerCase() + "%"
                    ));
                }

                if (filterBy.getCustomerSegment() != null && !filterBy.getCustomerSegment().trim().isEmpty()) {
                    predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("customerSegment")),
                        "%" + filterBy.getCustomerSegment().toLowerCase() + "%"
                    ));
                }

                // Invoice-related filters (requires join with InvoiceMaster)
                if (filterBy.getInvoiceNumber() != null && !filterBy.getInvoiceNumber().trim().isEmpty()) {
                    predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.join("invoices").get("invoiceNumber")),
                        "%" + filterBy.getInvoiceNumber().toLowerCase() + "%"
                    ));
                }

                if (filterBy.getInvoiceDateFrom() != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.join("invoices").get("invoiceDate"),
                        filterBy.getInvoiceDateFrom()
                    ));
                }

                if (filterBy.getInvoiceDateTo() != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.join("invoices").get("invoiceDate"),
                        filterBy.getInvoiceDateTo()
                    ));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Pageable createPageable(CustomRequest request) {

        Sort sort = Sort.by(Sort.Direction.ASC, "customerName"); // Default sort

        if (request.getFilterBy() != null && request.getFilterBy().getSortBy() != null) {
            Sort.Direction direction = "DESC".equalsIgnoreCase(request.getFilterBy().getSortDirection())
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
            sort = Sort.by(direction, request.getFilterBy().getSortBy());
        }

        return PageRequest.of(request.getPage(), request.getSize(), sort);
    }
}
