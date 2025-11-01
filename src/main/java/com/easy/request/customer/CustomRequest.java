package com.easy.request.customer;

import com.easy.entity.Customer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomRequest implements Serializable {

    private int page=0;
    private int size=100;
    private FilterBy filterBy;

    @Data
    public static class FilterBy {
        // Basic customer information
        private String name;
        private String gstin;
        private String pan;
        private Customer.CustomerType customerType;
        private String email;
        private String phone;
        private String mobile;

        // Location-based filters
        private String city;
        private String state;
        private String stateCode;
        private String pincode;


        // Status filters
        private Boolean isActive;
        private Boolean isCashCustomer;

        // Invoice-related filters
        private String invoiceNumber;
        private LocalDate invoiceDateFrom;
        private LocalDate invoiceDateTo;
        private String invoiceAmountFrom;
        private String invoiceAmountTo;

        // Date range filters
        private LocalDate createdDateFrom;
        private LocalDate createdDateTo;
        private LocalDate updatedDateFrom;
        private LocalDate updatedDateTo;

        // Company context (essential for multi-tenant)
        private UUID companyId;

        // Advanced filters
        private String customerCategory;
        private String customerSegment;

        // Sorting
        private String sortBy = "invoiceNumber"; // Default sort by customer name
        private String sortDirection = "DESC";
        // ASC or DESC

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGstin() {
            return gstin;
        }

        public void setGstin(String gstin) {
            this.gstin = gstin;
        }

        public String getPan() {
            return pan;
        }

        public void setPan(String pan) {
            this.pan = pan;
        }

        public Customer.CustomerType getCustomerType() {
            return customerType;
        }

        public void setCustomerType(Customer.CustomerType customerType) {
            this.customerType = customerType;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getStateCode() {
            return stateCode;
        }

        public void setStateCode(String stateCode) {
            this.stateCode = stateCode;
        }

        public String getPincode() {
            return pincode;
        }

        public void setPincode(String pincode) {
            this.pincode = pincode;
        }

        public Boolean getActive() {
            return isActive;
        }

        public void setActive(Boolean active) {
            isActive = active;
        }

        public Boolean getCashCustomer() {
            return isCashCustomer;
        }

        public void setCashCustomer(Boolean cashCustomer) {
            isCashCustomer = cashCustomer;
        }

        public String getInvoiceNumber() {
            return invoiceNumber;
        }

        public void setInvoiceNumber(String invoiceNumber) {
            this.invoiceNumber = invoiceNumber;
        }

        public LocalDate getInvoiceDateFrom() {
            return invoiceDateFrom;
        }

        public void setInvoiceDateFrom(LocalDate invoiceDateFrom) {
            this.invoiceDateFrom = invoiceDateFrom;
        }

        public LocalDate getInvoiceDateTo() {
            return invoiceDateTo;
        }

        public void setInvoiceDateTo(LocalDate invoiceDateTo) {
            this.invoiceDateTo = invoiceDateTo;
        }

        public String getInvoiceAmountFrom() {
            return invoiceAmountFrom;
        }

        public void setInvoiceAmountFrom(String invoiceAmountFrom) {
            this.invoiceAmountFrom = invoiceAmountFrom;
        }

        public String getInvoiceAmountTo() {
            return invoiceAmountTo;
        }

        public void setInvoiceAmountTo(String invoiceAmountTo) {
            this.invoiceAmountTo = invoiceAmountTo;
        }

        public LocalDate getCreatedDateFrom() {
            return createdDateFrom;
        }

        public void setCreatedDateFrom(LocalDate createdDateFrom) {
            this.createdDateFrom = createdDateFrom;
        }

        public LocalDate getCreatedDateTo() {
            return createdDateTo;
        }

        public void setCreatedDateTo(LocalDate createdDateTo) {
            this.createdDateTo = createdDateTo;
        }

        public LocalDate getUpdatedDateFrom() {
            return updatedDateFrom;
        }

        public void setUpdatedDateFrom(LocalDate updatedDateFrom) {
            this.updatedDateFrom = updatedDateFrom;
        }

        public LocalDate getUpdatedDateTo() {
            return updatedDateTo;
        }

        public void setUpdatedDateTo(LocalDate updatedDateTo) {
            this.updatedDateTo = updatedDateTo;
        }

        public UUID getCompanyId() {
            return companyId;
        }

        public void setCompanyId(UUID companyId) {
            this.companyId = companyId;
        }

        public String getCustomerCategory() {
            return customerCategory;
        }

        public void setCustomerCategory(String customerCategory) {
            this.customerCategory = customerCategory;
        }

        public String getCustomerSegment() {
            return customerSegment;
        }

        public void setCustomerSegment(String customerSegment) {
            this.customerSegment = customerSegment;
        }

        public String getSortBy() {
            return sortBy;
        }

        public void setSortBy(String sortBy) {
            this.sortBy = sortBy;
        }

        public String getSortDirection() {
            return sortDirection;
        }

        public void setSortDirection(String sortDirection) {
            this.sortDirection = sortDirection;
        }
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public FilterBy getFilterBy() {
        return filterBy;
    }

    public void setFilterBy(FilterBy filterBy) {
        this.filterBy = filterBy;
    }
}
