package com.easy.controller;

import com.easy.dto.CustomerRequest;
import com.easy.dto.CustomerResponse;
import com.easy.entity.Customer;
import com.easy.request.customer.CustomRequest;
import com.easy.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Customer Management", description = "APIs for managing customers")
//@CrossOrigin(origins = {"http://localhost:3000", "https://yourdomain.com"})
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/search")
    @Operation(summary = "Get customers with filters and pagination",
            description = "Retrieve a paginated list of customers with optional filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customers retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    public ResponseEntity<Page<Customer>> getCustomers(
            @Parameter(description = "Filter and pagination request") @Valid @RequestBody CustomRequest request) {

        log.info("Fetching customers with filters: page={}, size={}", request.getPage(), request.getSize());
        Page<Customer> customers = customerService.getCustomersWithFilters(request);
        log.info("Retrieved {} customers", customers.getTotalElements());

        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID", description = "Retrieve a specific customer by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer found",
                    content = @Content(schema = @Schema(implementation = CustomerResponse.class))),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CustomerResponse> getCustomerById(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable @NotNull UUID id) {

        log.info("Fetching customer with ID: {}", id);
        CustomerResponse customer = customerService.getCustomerResponseById(id);
        log.info("Customer found: {}", customer.getCustomerName());

        return ResponseEntity.ok(customer);
    }

    @GetMapping("/company/{companyId}")
    @Operation(summary = "Get customers by company", description = "Retrieve all customers for a specific company")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customers retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Company not found"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    public ResponseEntity<List<CustomerResponse>> getCustomersByCompany(
            @Parameter(description = "Company ID", required = true)
            @PathVariable @NotNull UUID companyId) {

        log.info("Fetching customers for company ID: {}", companyId);
        List<CustomerResponse> customers = customerService.getCustomerResponsesByCompany(companyId);
        log.info("Retrieved {} customers for company", customers.size());

        return ResponseEntity.ok(customers);
    }

    @GetMapping("/gstin/{gstin}")
    @Operation(summary = "Get customer by GSTIN", description = "Retrieve a customer by their GSTIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer found"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    public ResponseEntity<Customer> getCustomerByGstin(
            @Parameter(description = "GSTIN", required = true)
            @PathVariable String gstin) {

        log.info("Fetching customer with GSTIN: {}", gstin);
        return customerService.getCustomerByGstin(gstin)
                .map(customer -> {
                    log.info("Customer found with GSTIN: {}", gstin);
                    return ResponseEntity.ok(customer);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pan/{pan}")
    @Operation(summary = "Get customer by PAN", description = "Retrieve a customer by their PAN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer found"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    public ResponseEntity<Customer> getCustomerByPan(
            @Parameter(description = "PAN", required = true)
            @PathVariable String pan) {

        log.info("Fetching customer with PAN: {}", pan);
        return customerService.getCustomerByPan(pan)
                .map(customer -> {
                    log.info("Customer found with PAN: {}", pan);
                    return ResponseEntity.ok(customer);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create new customer", description = "Create a new customer with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Customer created successfully",
                    content = @Content(schema = @Schema(implementation = CustomerResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body or validation errors"),
            @ApiResponse(responseCode = "409", description = "Customer already exists (duplicate GSTIN/PAN)"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CustomerResponse> createCustomer(
            @Parameter(description = "Customer creation request", required = true)
            @Valid @RequestBody CustomerRequest request) {

        log.info("Creating new customer: {}", request.getCustomerName());
        CustomerResponse createdCustomer = customerService.createCustomer(request);
        log.info("Customer created successfully with ID: {}", createdCustomer.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update customer", description = "Update an existing customer with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer updated successfully",
                    content = @Content(schema = @Schema(implementation = CustomerResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body or validation errors"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "409", description = "Duplicate GSTIN/PAN"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CustomerResponse> updateCustomer(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable @NotNull UUID id,
            @Parameter(description = "Customer update request", required = true)
            @Valid @RequestBody CustomerRequest request) {

        log.info("Updating customer with ID: {}", id);
        CustomerResponse updatedCustomer = customerService.updateCustomer(id, request);
        log.info("Customer updated successfully: {}", updatedCustomer.getCustomerName());

        return ResponseEntity.ok(updatedCustomer);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update customer status", description = "Update the active status of a customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer status updated successfully",
                    content = @Content(schema = @Schema(implementation = CustomerResponse.class))),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CustomerResponse> updateCustomerStatus(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable @NotNull UUID id,
            @Parameter(description = "Active status", required = true)
            @RequestParam @NotNull Boolean isActive) {

        log.info("Updating status for customer ID: {} to {}", id, isActive);
        CustomerResponse updatedCustomer = customerService.updateCustomerStatus(id, isActive);
        log.info("Customer status updated successfully for ID: {}", id);

        return ResponseEntity.ok(updatedCustomer);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete customer", description = "Delete a customer by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "409", description = "Cannot delete customer with existing dependencies"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable @NotNull UUID id) {

        log.info("Deleting customer with ID: {}", id);
        customerService.deleteCustomer(id);
        log.info("Customer deleted successfully with ID: {}", id);

        return ResponseEntity.noContent().build();
    }
}