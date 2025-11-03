package com.easy.controller;

import com.easy.dto.InvoiceRequest;
import com.easy.dto.InvoiceResponse;
import com.easy.entity.InvoiceMaster;
import com.easy.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Invoice Management", description = "APIs for managing invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    @Operation(summary = "Create a new invoice", description = "Creates a new invoice with the provided details")
    public ResponseEntity<InvoiceResponse> createInvoice(@Valid @RequestBody InvoiceRequest request) {
        log.info("Creating new invoice with number: {}", request.getInvoiceNumber());
        InvoiceResponse response = invoiceService.createInvoice(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing invoice", description = "Updates an existing invoice with the provided details")
    public ResponseEntity<InvoiceResponse> updateInvoice(
            @Parameter(description = "Invoice ID") @PathVariable UUID id,
            @Valid @RequestBody InvoiceRequest request) {
        log.info("Updating invoice with ID: {}", id);
        InvoiceResponse response = invoiceService.updateInvoice(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get invoice by ID", description = "Retrieves an invoice by its ID")
    public ResponseEntity<InvoiceResponse> getInvoiceById(
            @Parameter(description = "Invoice ID") @PathVariable UUID id) {
        log.info("Fetching invoice with ID: {}", id);
        InvoiceResponse response = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/number/{invoiceNumber}")
    @Operation(summary = "Get invoice by number", description = "Retrieves an invoice by its invoice number")
    public ResponseEntity<InvoiceResponse> getInvoiceByNumber(
            @Parameter(description = "Invoice Number") @PathVariable String invoiceNumber) {
        log.info("Fetching invoice with number: {}", invoiceNumber);
        InvoiceResponse response = invoiceService.getInvoiceByNumber(invoiceNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all invoices with pagination", description = "Retrieves all invoices with pagination and sorting")
    public ResponseEntity<Page<InvoiceResponse>> getAllInvoices(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "invoiceDate") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "DESC") String sortDirection) {
        log.info("Fetching all invoices - page: {}, size: {}", page, size);
        Page<InvoiceResponse> response = invoiceService.getAllInvoices(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/company/{companyId}")
    @Operation(summary = "Get invoices by company", description = "Retrieves invoices for a specific company")
    public ResponseEntity<Page<InvoiceResponse>> getInvoicesByCompany(
            @Parameter(description = "Company ID") @PathVariable UUID companyId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        log.info("Fetching invoices for company: {}", companyId);
        Page<InvoiceResponse> response = invoiceService.getInvoicesByCompany(companyId, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get invoices by customer", description = "Retrieves invoices for a specific customer")
    public ResponseEntity<Page<InvoiceResponse>> getInvoicesByCustomer(
            @Parameter(description = "Customer ID") @PathVariable UUID customerId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        log.info("Fetching invoices for customer: {}", customerId);
        Page<InvoiceResponse> response = invoiceService.getInvoicesByCustomer(customerId, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get invoices by date range", description = "Retrieves invoices within a specific date range")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByDateRange(
            @Parameter(description = "Start date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Fetching invoices between {} and {}", startDate, endDate);
        List<InvoiceResponse> response = invoiceService.getInvoicesByDateRange(startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/company/{companyId}/date-range")
    @Operation(summary = "Get invoices by company and date range", description = "Retrieves invoices for a company within a date range")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByCompanyAndDateRange(
            @Parameter(description = "Company ID") @PathVariable UUID companyId,
            @Parameter(description = "Start date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Fetching invoices for company {} between {} and {}", companyId, startDate, endDate);
        List<InvoiceResponse> response = invoiceService.getInvoicesByCompanyAndDateRange(companyId, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue invoices", description = "Retrieves all overdue invoices")
    public ResponseEntity<List<InvoiceResponse>> getOverdueInvoices() {
        log.info("Fetching overdue invoices");
        List<InvoiceResponse> response = invoiceService.getOverdueInvoices();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/company/{companyId}/overdue")
    @Operation(summary = "Get overdue invoices by company", description = "Retrieves overdue invoices for a specific company")
    public ResponseEntity<List<InvoiceResponse>> getOverdueInvoicesByCompany(
            @Parameter(description = "Company ID") @PathVariable UUID companyId) {
        log.info("Fetching overdue invoices for company: {}", companyId);
        List<InvoiceResponse> response = invoiceService.getOverdueInvoicesByCompany(companyId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unpaid")
    @Operation(summary = "Get unpaid invoices", description = "Retrieves all unpaid invoices")
    public ResponseEntity<List<InvoiceResponse>> getUnpaidInvoices() {
        log.info("Fetching unpaid invoices");
        List<InvoiceResponse> response = invoiceService.getUnpaidInvoices();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/company/{companyId}/unpaid")
    @Operation(summary = "Get unpaid invoices by company", description = "Retrieves unpaid invoices for a specific company")
    public ResponseEntity<List<InvoiceResponse>> getUnpaidInvoicesByCompany(
            @Parameter(description = "Company ID") @PathVariable UUID companyId) {
        log.info("Fetching unpaid invoices for company: {}", companyId);
        List<InvoiceResponse> response = invoiceService.getUnpaidInvoicesByCompany(companyId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel an invoice", description = "Cancels an existing invoice")
    public ResponseEntity<InvoiceResponse> cancelInvoice(
            @Parameter(description = "Invoice ID") @PathVariable UUID id,
            @Parameter(description = "Cancellation reason") @RequestParam String cancellationReason) {
        log.info("Cancelling invoice with ID: {}", id);
        InvoiceResponse response = invoiceService.cancelInvoice(id, cancellationReason);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/approve")
    @Operation(summary = "Approve an invoice", description = "Approves a pending invoice")
    public ResponseEntity<InvoiceResponse> approveInvoice(
            @Parameter(description = "Invoice ID") @PathVariable UUID id) {
        log.info("Approving invoice with ID: {}", id);
        InvoiceResponse response = invoiceService.approveInvoice(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "Reject an invoice", description = "Rejects a pending invoice")
    public ResponseEntity<InvoiceResponse> rejectInvoice(
            @Parameter(description = "Invoice ID") @PathVariable UUID id,
            @Parameter(description = "Rejection reason") @RequestParam String rejectionReason) {
        log.info("Rejecting invoice with ID: {}", id);
        InvoiceResponse response = invoiceService.rejectInvoice(id, rejectionReason);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an invoice", description = "Deletes a draft invoice")
    public ResponseEntity<Void> deleteInvoice(
            @Parameter(description = "Invoice ID") @PathVariable UUID id) {
        log.info("Deleting invoice with ID: {}", id);
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }

    // Statistics APIs
    @GetMapping("/statistics/count")
    @Operation(summary = "Get invoice count by company and date range", description = "Get count of invoices for a company within date range")
    public ResponseEntity<Long> getInvoiceCount(
            @Parameter(description = "Company ID") @RequestParam UUID companyId,
            @Parameter(description = "Start date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long count = invoiceService.getInvoiceCountByCompanyAndDateRange(companyId, startDate, endDate);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/statistics/total-amount")
    @Operation(summary = "Get total invoice amount by company and date range", description = "Get total amount of invoices for a company within date range")
    public ResponseEntity<BigDecimal> getTotalInvoiceAmount(
            @Parameter(description = "Company ID") @RequestParam UUID companyId,
            @Parameter(description = "Start date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (YYYY-MM-DD)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        BigDecimal totalAmount = invoiceService.getTotalInvoiceAmountByCompanyAndDateRange(companyId, startDate, endDate);
        return ResponseEntity.ok(totalAmount);
    }

    @GetMapping("/statistics/outstanding-amount")
    @Operation(summary = "Get outstanding amount by company", description = "Get total outstanding amount for a company")
    public ResponseEntity<BigDecimal> getOutstandingAmount(
            @Parameter(description = "Company ID") @RequestParam UUID companyId) {
        BigDecimal outstandingAmount = invoiceService.getOutstandingAmountByCompany(companyId);
        return ResponseEntity.ok(outstandingAmount);
    }

    // Advanced filtering API
    @PostMapping("/search")
    @Operation(summary = "Search invoices with filters", description = "Search invoices using advanced filters")
    public ResponseEntity<Page<InvoiceResponse>> searchInvoices(
            @Valid @RequestBody InvoiceService.InvoiceFilterRequest filterRequest,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "invoiceDate") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "DESC") String sortDirection) {
        log.info("Searching invoices with filters");
        Page<InvoiceResponse> response = invoiceService.getInvoicesWithFilters(filterRequest, page, size, sortBy, sortDirection);
        return ResponseEntity.ok(response);
    }

    // Invoice Types and Payment Types endpoints for dropdown values
    @GetMapping("/invoice-types")
    @Operation(summary = "Get all invoice types", description = "Get all available invoice types")
    public ResponseEntity<InvoiceMaster.InvoiceType[]> getInvoiceTypes() {
        return ResponseEntity.ok(InvoiceMaster.InvoiceType.values());
    }

    @GetMapping("/payment-types")
    @Operation(summary = "Get all payment types", description = "Get all available payment types")
    public ResponseEntity<InvoiceMaster.PaymentType[]> getPaymentTypes() {
        return ResponseEntity.ok(InvoiceMaster.PaymentType.values());
    }

    @GetMapping("/{id}/items")
    @Operation(summary = "Get invoice items", description = "Get all items for a specific invoice")
    public ResponseEntity<List<InvoiceResponse.InvoiceItemResponse>> getInvoiceItems(
            @Parameter(description = "Invoice ID") @PathVariable UUID id) {
        log.info("Fetching items for invoice: {}", id);
        InvoiceResponse invoice = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(invoice.getInvoiceItems());
    }
}
