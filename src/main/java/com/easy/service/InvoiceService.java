package com.easy.service;

import com.easy.dto.InvoiceMapper;
import com.easy.dto.InvoiceRequest;
import com.easy.dto.InvoiceResponse;
import com.easy.entity.CompanyMaster;
import com.easy.entity.Customer;
import com.easy.entity.InvoiceMaster;
import com.easy.repository.CompanyRepository;
import com.easy.repository.CustomerRepository;
import com.easy.repository.InvoiceRepository;
import com.easy.repository.InvoiceItemsRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemsRepository invoiceItemsRepository;
    private final InvoiceMapper invoiceMapper;
    private final CompanyRepository companyRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public InvoiceResponse createInvoice(InvoiceRequest request) {
        log.info("Creating new invoice with number: {}", request.getInvoiceNumber());

        // Validate companyId is provided
        if (request.getCompanyId() == null) {
            throw new IllegalArgumentException("Company ID is required");
        }

        // Validate unique invoice number
        if (invoiceRepository.findByInvoiceNumber(request.getInvoiceNumber()).isPresent()) {
            throw new IllegalArgumentException("Invoice number already exists: " + request.getInvoiceNumber());
        }

        // Validate invoice items
        if (request.getInvoiceItems() == null || request.getInvoiceItems().isEmpty()) {
            throw new IllegalArgumentException("Invoice must contain at least one item");
        }

        InvoiceMaster invoice = invoiceMapper.toEntity(request);

        // Set company and customer
        CompanyMaster company = companyRepository.findById(request.getCompanyId())
            .orElseThrow(() -> new EntityNotFoundException("Company not found with ID: " + request.getCompanyId()));
        Customer customer = customerRepository.findById(request.getCustomerId())
            .orElseThrow(() -> new EntityNotFoundException("Customer not found with ID: " + request.getCustomerId()));
        invoice.setCompany(company);
        invoice.setCustomer(customer);

        // Set audit fields
        invoice.setCreatedBy(getCurrentUserId()); // Implement getCurrentUserId()
        invoice.setUpdatedBy(getCurrentUserId());

        // Ensure bidirectional relationship for invoice items
        if (invoice.getInvoiceItems() != null) {
            invoice.getInvoiceItems().forEach(item -> item.setInvoice(invoice));
        }

        InvoiceMaster savedInvoice = invoiceRepository.save(invoice);
        log.info("Invoice created successfully with ID: {} with {} items",
                 savedInvoice.getId(),
                 savedInvoice.getInvoiceItems() != null ? savedInvoice.getInvoiceItems().size() : 0);

        return invoiceMapper.toResponse(savedInvoice);
    }

    @Transactional
    public InvoiceResponse updateInvoice(UUID id, InvoiceRequest request) {
        log.info("Updating invoice with ID: {}", id);

        InvoiceMaster existingInvoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found with ID: " + id));

        // Validate companyId is provided
        if (request.getCompanyId() == null) {
            throw new IllegalArgumentException("Company ID is required");
        }

        // Check if invoice number is being changed and if it's unique
        if (!existingInvoice.getInvoiceNumber().equals(request.getInvoiceNumber())) {
            Optional<InvoiceMaster> duplicateInvoice = invoiceRepository.findByInvoiceNumber(request.getInvoiceNumber());
            if (duplicateInvoice.isPresent() && !duplicateInvoice.get().getId().equals(id)) {
                throw new IllegalArgumentException("Invoice number already exists: " + request.getInvoiceNumber());
            }
        }

        // Check if invoice can be updated (not cancelled, approved, etc.)
        if (Boolean.TRUE.equals(existingInvoice.getIsCancelled())) {
            throw new IllegalStateException("Cannot update cancelled invoice");
        }

        // Validate invoice items
        if (request.getInvoiceItems() == null || request.getInvoiceItems().isEmpty()) {
            throw new IllegalArgumentException("Invoice must contain at least one item");
        }

        // Update company if changed
        if (!existingInvoice.getCompany().getId().equals(request.getCompanyId())) {
            CompanyMaster company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new EntityNotFoundException("Company not found with ID: " + request.getCompanyId()));
            existingInvoice.setCompany(company);
        }

        // Update customer if changed
        if (!existingInvoice.getCustomer().getId().equals(request.getCustomerId())) {
            Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with ID: " + request.getCustomerId()));
            existingInvoice.setCustomer(customer);
        }

        invoiceMapper.updateEntityFromRequest(existingInvoice, request);
        existingInvoice.setUpdatedBy(getCurrentUserId());

        // Ensure bidirectional relationship for updated invoice items
        if (existingInvoice.getInvoiceItems() != null) {
            existingInvoice.getInvoiceItems().forEach(item -> item.setInvoice(existingInvoice));
        }

        InvoiceMaster updatedInvoice = invoiceRepository.save(existingInvoice);
        log.info("Invoice updated successfully with ID: {} with {} items",
                 updatedInvoice.getId(),
                 updatedInvoice.getInvoiceItems() != null ? updatedInvoice.getInvoiceItems().size() : 0);

        return invoiceMapper.toResponse(updatedInvoice);
    }

    public InvoiceResponse getInvoiceById(UUID id) {
        log.debug("Fetching invoice with ID: {}", id);

        InvoiceMaster invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found with ID: " + id));

        return invoiceMapper.toResponse(invoice);
    }

    public InvoiceResponse getInvoiceByNumber(String invoiceNumber) {
        log.debug("Fetching invoice with number: {}", invoiceNumber);

        InvoiceMaster invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found with number: " + invoiceNumber));

        return invoiceMapper.toResponse(invoice);
    }

    public Page<InvoiceResponse> getAllInvoices(int page, int size, String sortBy, String sortDirection) {
        log.debug("Fetching all invoices - page: {}, size: {}, sortBy: {}, sortDirection: {}",
                  page, size, sortBy, sortDirection);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<InvoiceMaster> invoicesPage = invoiceRepository.findAll(pageable);

        return invoicesPage.map(invoiceMapper::toResponse);
    }

    public Page<InvoiceResponse> getInvoicesByCompany(UUID companyId, int page, int size) {
        log.debug("Fetching invoices for company ID: {}", companyId);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "invoiceDate"));
        Page<InvoiceMaster> invoicesPage = invoiceRepository.findByCompanyIdOrderByInvoiceDateDesc(companyId, pageable);

        return invoicesPage.map(invoiceMapper::toResponse);
    }

    public Page<InvoiceResponse> getInvoicesByCustomer(UUID customerId, int page, int size) {
        log.debug("Fetching invoices for customer ID: {}", customerId);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "invoiceDate"));
        Page<InvoiceMaster> invoicesPage = invoiceRepository.findByCustomerIdOrderByInvoiceDateDesc(customerId, pageable);

        return invoicesPage.map(invoiceMapper::toResponse);
    }

    public List<InvoiceResponse> getInvoicesByDateRange(LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching invoices between {} and {}", startDate, endDate);

        List<InvoiceMaster> invoices = invoiceRepository.findByInvoiceDateBetween(startDate, endDate);

        return invoiceMapper.toResponseList(invoices);
    }

    public List<InvoiceResponse> getInvoicesByCompanyAndDateRange(UUID companyId, LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching invoices for company {} between {} and {}", companyId, startDate, endDate);

        List<InvoiceMaster> invoices = invoiceRepository.findByCompanyIdAndInvoiceDateBetween(companyId, startDate, endDate);

        return invoiceMapper.toResponseList(invoices);
    }

    public List<InvoiceResponse> getOverdueInvoices() {
        log.debug("Fetching overdue invoices");

        List<InvoiceMaster> overdueInvoices = invoiceRepository.findOverdueInvoices(LocalDate.now());

        return invoiceMapper.toResponseList(overdueInvoices);
    }

    public List<InvoiceResponse> getOverdueInvoicesByCompany(UUID companyId) {
        log.debug("Fetching overdue invoices for company ID: {}", companyId);

        List<InvoiceMaster> overdueInvoices = invoiceRepository.findOverdueInvoicesByCompany(companyId, LocalDate.now());

        return invoiceMapper.toResponseList(overdueInvoices);
    }

    public List<InvoiceResponse> getUnpaidInvoices() {
        log.debug("Fetching unpaid invoices");

        List<InvoiceMaster> unpaidInvoices = invoiceRepository.findUnpaidInvoices();

        return invoiceMapper.toResponseList(unpaidInvoices);
    }

    public List<InvoiceResponse> getUnpaidInvoicesByCompany(UUID companyId) {
        log.debug("Fetching unpaid invoices for company ID: {}", companyId);

        List<InvoiceMaster> unpaidInvoices = invoiceRepository.findUnpaidInvoicesByCompany(companyId);

        return invoiceMapper.toResponseList(unpaidInvoices);
    }

    @Transactional
    public InvoiceResponse cancelInvoice(UUID id, String cancellationReason) {
        log.info("Cancelling invoice with ID: {}", id);

        InvoiceMaster invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found with ID: " + id));

        if (Boolean.TRUE.equals(invoice.getIsCancelled())) {
            throw new IllegalStateException("Invoice is already cancelled");
        }

        invoice.setIsCancelled(true);
        invoice.setCancelledAt(LocalDateTime.now());
        invoice.setCancelledBy(getCurrentUserId());
        invoice.setCancellationReason(cancellationReason);
        invoice.setUpdatedBy(getCurrentUserId());

        InvoiceMaster updatedInvoice = invoiceRepository.save(invoice);
        log.info("Invoice cancelled successfully with ID: {}", updatedInvoice.getId());

        return invoiceMapper.toResponse(updatedInvoice);
    }

    @Transactional
    public InvoiceResponse approveInvoice(UUID id) {
        log.info("Approving invoice with ID: {}", id);

        InvoiceMaster invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found with ID: " + id));

        if (Boolean.TRUE.equals(invoice.getIsCancelled())) {
            throw new IllegalStateException("Cannot approve cancelled invoice");
        }

        if ("APPROVED".equals(invoice.getApprovalStatus())) {
            throw new IllegalStateException("Invoice is already approved");
        }

        invoice.setApprovalStatus("APPROVED");
        invoice.setApprovedBy(getCurrentUserId());
        invoice.setApprovedAt(LocalDateTime.now());
        invoice.setUpdatedBy(getCurrentUserId());

        InvoiceMaster updatedInvoice = invoiceRepository.save(invoice);
        log.info("Invoice approved successfully with ID: {}", updatedInvoice.getId());

        return invoiceMapper.toResponse(updatedInvoice);
    }

    @Transactional
    public InvoiceResponse rejectInvoice(UUID id, String rejectionReason) {
        log.info("Rejecting invoice with ID: {}", id);

        InvoiceMaster invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found with ID: " + id));

        if (Boolean.TRUE.equals(invoice.getIsCancelled())) {
            throw new IllegalStateException("Cannot reject cancelled invoice");
        }

        invoice.setApprovalStatus("REJECTED");
        invoice.setRejectionReason(rejectionReason);
        invoice.setUpdatedBy(getCurrentUserId());

        InvoiceMaster updatedInvoice = invoiceRepository.save(invoice);
        log.info("Invoice rejected successfully with ID: {}", updatedInvoice.getId());

        return invoiceMapper.toResponse(updatedInvoice);
    }

    @Transactional
    public void deleteInvoice(UUID id) {
        log.info("Deleting invoice with ID: {}", id);

        InvoiceMaster invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found with ID: " + id));

        // Check if invoice can be deleted (only draft invoices)
        if (!"DRAFT".equals(invoice.getApprovalStatus())) {
            throw new IllegalStateException("Only draft invoices can be deleted");
        }

        if (Boolean.TRUE.equals(invoice.getIsCancelled())) {
            throw new IllegalStateException("Cannot delete cancelled invoice");
        }

        invoiceRepository.delete(invoice);
        log.info("Invoice deleted successfully with ID: {}", id);
    }

    // Statistics methods
    public Long getInvoiceCountByCompanyAndDateRange(UUID companyId, LocalDate startDate, LocalDate endDate) {
        return invoiceRepository.countInvoicesByCompanyAndDateRange(companyId, startDate, endDate);
    }

    public BigDecimal getTotalInvoiceAmountByCompanyAndDateRange(UUID companyId, LocalDate startDate, LocalDate endDate) {
        return invoiceRepository.sumInvoiceAmountByCompanyAndDateRange(companyId, startDate, endDate);
    }

    public BigDecimal getOutstandingAmountByCompany(UUID companyId) {
        return invoiceRepository.sumOutstandingAmountByCompany(companyId);
    }

    // Advanced filtering with specifications
    public Page<InvoiceResponse> getInvoicesWithFilters(InvoiceFilterRequest filterRequest, int page, int size, String sortBy, String sortDirection) {
        log.debug("Fetching invoices with filters: {}", filterRequest);

        Specification<InvoiceMaster> specification = createInvoiceSpecification(filterRequest);
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<InvoiceMaster> invoicesPage = invoiceRepository.findAll(specification, pageable);

        return invoicesPage.map(invoiceMapper::toResponse);
    }

    private Specification<InvoiceMaster> createInvoiceSpecification(InvoiceFilterRequest filterRequest) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filterRequest.getCompanyId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("company").get("id"), filterRequest.getCompanyId()));
            }

            if (filterRequest.getCustomerId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("customer").get("id"), filterRequest.getCustomerId()));
            }

            if (filterRequest.getInvoiceType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("invoiceType"), filterRequest.getInvoiceType()));
            }

            if (filterRequest.getPaymentType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("paymentType"), filterRequest.getPaymentType()));
            }

            if (filterRequest.getStartDate() != null && filterRequest.getEndDate() != null) {
                predicates.add(criteriaBuilder.between(root.get("invoiceDate"),
                    filterRequest.getStartDate(), filterRequest.getEndDate()));
            }

            if (filterRequest.getMinAmount() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("totalInvoiceAmount"),
                    filterRequest.getMinAmount()));
            }

            if (filterRequest.getMaxAmount() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("totalInvoiceAmount"),
                    filterRequest.getMaxAmount()));
            }

            if (filterRequest.getApprovalStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("approvalStatus"), filterRequest.getApprovalStatus()));
            }

            if (filterRequest.getIsCancelled() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isCancelled"), filterRequest.getIsCancelled()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    // Helper method to get current user ID (implement based on security context)
    private UUID getCurrentUserId() {
        // Placeholder - implement based on your security setup
        return UUID.randomUUID(); // This should return the actual current user ID
    }

    // Inner class for filter request
    @lombok.Data
    public static class InvoiceFilterRequest {
        private UUID companyId;
        private UUID customerId;
        private InvoiceMaster.InvoiceType invoiceType;
        private InvoiceMaster.PaymentType paymentType;
        private LocalDate startDate;
        private LocalDate endDate;
        private BigDecimal minAmount;
        private BigDecimal maxAmount;
        private String approvalStatus;
        private Boolean isCancelled;
        private String financialYear;
    }
}
