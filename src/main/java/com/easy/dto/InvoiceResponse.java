package com.easy.dto;

import com.easy.entity.InvoiceMaster;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class InvoiceResponse {

    private UUID id;

    // Company and Customer details
    private CompanyInfo company;
    private CustomerInfo customer;

    private String invoiceNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate invoiceDate;

    private InvoiceMaster.InvoiceType invoiceType;

    private String financialYear;

    private String placeOfSupply;

    private String placeOfSupplyStateCode;

    private Boolean isInterState;

    private Boolean reverseChargeApplicable;

    // Amounts
    private BigDecimal totalTaxableAmount;
    private BigDecimal totalDiscountAmount;
    private BigDecimal cgstAmount;
    private BigDecimal sgstAmount;
    private BigDecimal igstAmount;
    private BigDecimal cessAmount;
    private BigDecimal roundOff;
    private BigDecimal totalInvoiceAmount;

    // Payment info
    private InvoiceMaster.PaymentType paymentType;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    private BigDecimal paidAmount;
    private BigDecimal balanceAmount;

    // Additional info
    private String notes;
    private String termsConditions;
    private String transportDetails;
    private String vehicleNumber;
    private String ewayBillNumber;

    // Currency support
    private String currency;
    private BigDecimal exchangeRate;
    private BigDecimal baseCurrencyAmount;

    // E-invoice fields
    private String einvoiceIrn;
    private String einvoiceAckNo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime einvoiceAckDate;

    private String einvoiceQrCode;

    // Approval workflow
    private String approvalStatus;
    private UUID approvedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approvedAt;

    private String rejectionReason;

    // Recurring invoices
    private Boolean isRecurring;
    private String recurringFrequency;
    private UUID recurringParentId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextInvoiceDate;

    // Document management
    private String pdfUrl;
    private String attachments;

    // Sales and analytics
    private UUID salesPersonId;
    private String salesChannel;
    private String orderReference;

    // Shipping and logistics
    private String shippingAddress;
    private BigDecimal shippingCost;
    private String shippingTrackingNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expectedDeliveryDate;

    // Custom fields
    private String customFields;

    // Status
    private Boolean isCancelled;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime cancelledAt;

    private UUID cancelledBy;
    private String cancellationReason;

    // Audit fields
    private UUID createdBy;
    private UUID updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // Invoice Items
    private List<InvoiceItemResponse> invoiceItems;

    @Data
    public static class CompanyInfo {
        private UUID id;
        private String companyName;
        private String gstin;
        private String address;
        private String city;
        private String state;
        private String pincode;
        private String phone;
        private String email;
    }

    @Data
    public static class CustomerInfo {
        private UUID id;
        private String customerName;
        private String gstin;
        private String address;
        private String city;
        private String state;
        private String pincode;
        private String phone;
        private String mobile;
        private String email;
        private String customerType; // Changed to String to accommodate Customer.CustomerType
    }

    @Data
    public static class InvoiceItemResponse {
        private UUID id;
        private UUID productId;
        private String itemDescription;
        private BigDecimal quantity;
        private String unit;
        private BigDecimal rate;
        private BigDecimal discountAmount;
        private BigDecimal taxableAmount;
        private BigDecimal gstRate;
        private BigDecimal cgstAmount;
        private BigDecimal sgstAmount;
        private BigDecimal igstAmount;
        private BigDecimal cessRate;
        private BigDecimal cessAmount;
        private BigDecimal totalAmount;
        private String hsnCode;
        private String sacCode;

        // Product details (if available)
        private ProductInfo product;
    }

    @Data
    public static class ProductInfo {
        private UUID id;
        private String productName;
        private String productCode;
        private String hsnCode;
        private String sacCode;
        private String unit;
        private BigDecimal gstRate;
    }
}
