package com.easy.example;

import com.easy.entity.Customer;
import com.easy.request.customer.CustomRequest;
import com.easy.request.customer.CustomRequest;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;

/**
 * Example class showing how to use CustomRequest with various filters
 */
public class CustomerFilterExamples {

    /**
     * Example 1: Basic search by name and company
     */
    public static CustomRequest basicSearchExample() {
        CustomRequest request = new CustomRequest();
        request.setPage(0);
        request.setSize(20);

        CustomRequest.FilterBy filter = new CustomRequest.FilterBy();
        filter.setName("John");
        filter.setCompanyId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        filter.setIsActive(true);

        request.setFilterBy(filter);
        return request;
    }

    /**
     * Example 2: Advanced search with multiple criteria
     */
    public static CustomRequest advancedSearchExample() {
        CustomRequest request = new CustomRequest();
        request.setPage(0);
        request.setSize(50);

        CustomRequest.FilterBy filter = new CustomRequest.FilterBy();

        // Basic filters
        filter.setCustomerType(Customer.CustomerType.REGISTERED);
        filter.setCity("Mumbai");
        filter.setState("Maharashtra");
        filter.setStateCode("MH");



        // Status filters
        filter.setIsActive(true);
        filter.setIsCashCustomer(false);

        // Date range
        filter.setCreatedDateFrom(LocalDate.of(2024, 1, 1));
        filter.setCreatedDateTo(LocalDate.of(2024, 12, 31));

        // Company context
        filter.setCompanyId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));

        // Sorting
        filter.setSortBy("customerName");
        filter.setSortDirection("ASC");

        request.setFilterBy(filter);
        return request;
    }

    /**
     * Example 3: Search by contact information
     */
    public static CustomRequest contactSearchExample() {
        CustomRequest request = new CustomRequest();

        CustomRequest.FilterBy filter = new CustomRequest.FilterBy();
        filter.setEmail("@gmail.com");
        filter.setPhone("9876543210");
        filter.setCompanyId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));

        request.setFilterBy(filter);
        return request;
    }

    /**
     * Example 4: Tax/GST related search
     */
    public static CustomRequest gstSearchExample() {
        CustomRequest request = new CustomRequest();

        CustomRequest.FilterBy filter = new CustomRequest.FilterBy();
        filter.setGstin("27AAACG9711K1ZV");
        filter.setPan("AAACG9711K");
        filter.setCustomerType(Customer.CustomerType.REGISTERED);
        filter.setCompanyId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));

        request.setFilterBy(filter);
        return request;
    }

    /**
     * Example 5: Invoice-related customer search
     */
    public static CustomRequest invoiceRelatedSearchExample() {
        CustomRequest request = new CustomRequest();

        CustomRequest.FilterBy filter = new CustomRequest.FilterBy();
        filter.setInvoiceNumber("INV-2024-001");
        filter.setInvoiceDateFrom(LocalDate.of(2024, 1, 1));
        filter.setInvoiceDateTo(LocalDate.of(2024, 12, 31));
        filter.setCompanyId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));

        request.setFilterBy(filter);
        return request;
    }

    /**
     * Example 6: General text search across multiple fields
     */
    public static CustomRequest generalSearchExample() {
        CustomRequest request = new CustomRequest();

        CustomRequest.FilterBy filter = new CustomRequest.FilterBy();
        filter.setCompanyId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        filter.setIsActive(true);

        // Custom sorting
        filter.setSortBy("createdAt");
        filter.setSortDirection("DESC");

        request.setFilterBy(filter);
        return request;
    }

    /**
     * Example 7: Location-based search
     */
    public static CustomRequest locationSearchExample() {
        CustomRequest request = new CustomRequest();

        CustomRequest.FilterBy filter = new CustomRequest.FilterBy();
        filter.setCity("Mumbai");
        filter.setState("Maharashtra");
        filter.setPincode("400001");
        filter.setCompanyId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));

        request.setFilterBy(filter);
        return request;
    }

    /**
     * Example 8: Business categorization search
     */
    public static CustomRequest businessCategorySearchExample() {
        CustomRequest request = new CustomRequest();

        CustomRequest.FilterBy filter = new CustomRequest.FilterBy();
        filter.setCustomerCategory("VIP");
        filter.setCustomerSegment("WHOLESALE");

        filter.setCompanyId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));

        request.setFilterBy(filter);
        return request;
    }
}
