package com.easy.dto;

import com.easy.entity.InvoiceMaster;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class InvoiceRequest {

    private UUID companyId;

    @NotNull(message = "Customer ID is required")
    private UUID customerId;

    @NotBlank(message = "Invoice number is required")
    @Size(max = 50, message = "Invoice number cannot exceed 50 characters")
    private String invoiceNumber;

    @NotNull(message = "Invoice date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate invoiceDate;

    @NotNull(message = "Invoice type is required")
    private InvoiceMaster.InvoiceType invoiceType = InvoiceMaster.InvoiceType.TAX_INVOICE;

    @NotBlank(message = "Financial year is required")
    @Size(min = 9, max = 9, message = "Financial year must be in format YYYY-YYYY")
    private String financialYear;

    @NotBlank(message = "Place of supply is required")
    @Size(max = 50, message = "Place of supply cannot exceed 50 characters")
    private String placeOfSupply;

    @NotBlank(message = "Place of supply state code is required")
    @Size(min = 2, max = 2, message = "State code must be exactly 2 characters")
    private String placeOfSupplyStateCode;

    private Boolean isInterState = false;

    private Boolean reverseChargeApplicable = false;

    // Amounts
    @NotNull(message = "Total taxable amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total taxable amount must be greater than 0")
    @Digits(integer = 15, fraction = 2, message = "Total taxable amount format is invalid")
    private BigDecimal totalTaxableAmount;

    @DecimalMin(value = "0.0", message = "Total discount amount cannot be negative")
    @Digits(integer = 15, fraction = 2, message = "Total discount amount format is invalid")
    private BigDecimal totalDiscountAmount = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "CGST amount cannot be negative")
    @Digits(integer = 15, fraction = 2, message = "CGST amount format is invalid")
    private BigDecimal cgstAmount = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "SGST amount cannot be negative")
    @Digits(integer = 15, fraction = 2, message = "SGST amount format is invalid")
    private BigDecimal sgstAmount = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "IGST amount cannot be negative")
    @Digits(integer = 15, fraction = 2, message = "IGST amount format is invalid")
    private BigDecimal igstAmount = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Cess amount cannot be negative")
    @Digits(integer = 15, fraction = 2, message = "Cess amount format is invalid")
    private BigDecimal cessAmount = BigDecimal.ZERO;

    @Digits(integer = 10, fraction = 2, message = "Round off format is invalid")
    private BigDecimal roundOff = BigDecimal.ZERO;

    @NotNull(message = "Total invoice amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total invoice amount must be greater than 0")
    @Digits(integer = 15, fraction = 2, message = "Total invoice amount format is invalid")
    private BigDecimal totalInvoiceAmount;

    // Payment info
    @NotNull(message = "Payment type is required")
    private InvoiceMaster.PaymentType paymentType = InvoiceMaster.PaymentType.CASH;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    @DecimalMin(value = "0.0", message = "Paid amount cannot be negative")
    @Digits(integer = 15, fraction = 2, message = "Paid amount format is invalid")
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Balance amount cannot be negative")
    @Digits(integer = 15, fraction = 2, message = "Balance amount format is invalid")
    private BigDecimal balanceAmount = BigDecimal.ZERO;

    // Additional info
    @Size(max = 2000, message = "Notes cannot exceed 2000 characters")
    private String notes;

    @Size(max = 2000, message = "Terms and conditions cannot exceed 2000 characters")
    private String termsConditions;

    @Size(max = 500, message = "Transport details cannot exceed 500 characters")
    private String transportDetails;

    @Size(max = 20, message = "Vehicle number cannot exceed 20 characters")
    private String vehicleNumber;

    @Size(max = 20, message = "E-way bill number cannot exceed 20 characters")
    private String ewayBillNumber;

    // Currency support
    @Size(min = 3, max = 3, message = "Currency must be exactly 3 characters")
    private String currency = "INR";

    @DecimalMin(value = "0.000001", message = "Exchange rate must be greater than 0")
    @Digits(integer = 15, fraction = 6, message = "Exchange rate format is invalid")
    private BigDecimal exchangeRate = new BigDecimal("1.000000");

    @Digits(integer = 15, fraction = 2, message = "Base currency amount format is invalid")
    private BigDecimal baseCurrencyAmount;

    // E-invoice fields
    @Size(max = 100, message = "E-invoice IRN cannot exceed 100 characters")
    private String einvoiceIrn;

    @Size(max = 50, message = "E-invoice acknowledgement number cannot exceed 50 characters")
    private String einvoiceAckNo;

    // Approval workflow
    private String approvalStatus = "DRAFT";

    // Recurring invoices
    private Boolean isRecurring = false;

    private String recurringFrequency;

    private UUID recurringParentId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextInvoiceDate;

    // Sales and analytics
    private UUID salesPersonId;

    @Size(max = 50, message = "Sales channel cannot exceed 50 characters")
    private String salesChannel;

    @Size(max = 100, message = "Order reference cannot exceed 100 characters")
    private String orderReference;

    // Shipping and logistics
    private String shippingAddress; // JSON string

    @DecimalMin(value = "0.0", message = "Shipping cost cannot be negative")
    @Digits(integer = 15, fraction = 2, message = "Shipping cost format is invalid")
    private BigDecimal shippingCost = BigDecimal.ZERO;

    @Size(max = 100, message = "Shipping tracking number cannot exceed 100 characters")
    private String shippingTrackingNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expectedDeliveryDate;

    // Custom fields
    private String customFields; // JSON string

    // Invoice Items
    private List<InvoiceItemRequest> invoiceItems;

    @Data
    public static class InvoiceItemRequest {

        private UUID productId;

        @NotBlank(message = "Item description is required")
        private String itemDescription;

        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
        private BigDecimal quantity;

        @NotBlank(message = "Unit is required")
        private String unit;

        @NotNull(message = "Rate is required")
        @DecimalMin(value = "0.0", message = "Rate cannot be negative")
        private BigDecimal rate;

        @DecimalMin(value = "0.0", message = "Discount cannot be negative")
        private BigDecimal discountAmount = BigDecimal.ZERO;

        @NotNull(message = "Taxable amount is required")
        @DecimalMin(value = "0.0", message = "Taxable amount cannot be negative")
        private BigDecimal taxableAmount;

        @DecimalMin(value = "0.0", message = "GST rate cannot be negative")
        @DecimalMax(value = "100.0", message = "GST rate cannot exceed 100%")
        private BigDecimal gstRate = BigDecimal.ZERO;

        private BigDecimal cgstAmount = BigDecimal.ZERO;
        private BigDecimal sgstAmount = BigDecimal.ZERO;
        private BigDecimal igstAmount = BigDecimal.ZERO;
        private BigDecimal cessRate = BigDecimal.ZERO;
        private BigDecimal cessAmount = BigDecimal.ZERO;

        @NotNull(message = "Total amount is required")
        @DecimalMin(value = "0.0", message = "Total amount cannot be negative")
        private BigDecimal totalAmount;

        private String hsnCode;
        private String sacCode;
    }
}
