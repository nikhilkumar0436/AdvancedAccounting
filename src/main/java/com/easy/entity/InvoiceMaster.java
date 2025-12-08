package com.easy.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "invoice_master")
@EntityListeners(AuditingEntityListener.class)
@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class InvoiceMaster extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private CompanyMaster company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "invoice_number", length = 50, unique = true, nullable = false)
    private String invoiceNumber;

    @Column(name = "invoice_date", nullable = false)
    private LocalDate invoiceDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "invoice_type", nullable = false)
    private InvoiceType invoiceType = InvoiceType.TAX_INVOICE;

    @Column(name = "financial_year", length = 9, nullable = false)
    private String financialYear; // Format: 2024-2025

    @Column(name = "place_of_supply", length = 50, nullable = false)
    private String placeOfSupply;

    @Column(name = "place_of_supply_state_code", length = 2, nullable = false)
    private String placeOfSupplyStateCode;

    @Column(name = "is_inter_state", nullable = false)
    private Boolean isInterState = false;

    @Column(name = "reverse_charge_applicable")
    private Boolean reverseChargeApplicable = false;

    // Amounts
    @Column(name = "total_taxable_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalTaxableAmount = BigDecimal.ZERO;

    @Column(name = "total_discount_amount", precision = 15, scale = 2)
    private BigDecimal totalDiscountAmount = BigDecimal.ZERO;

    @Column(name = "cgst_amount", precision = 15, scale = 2)
    private BigDecimal cgstAmount = BigDecimal.ZERO;

    @Column(name = "sgst_amount", precision = 15, scale = 2)
    private BigDecimal sgstAmount = BigDecimal.ZERO;

    @Column(name = "igst_amount", precision = 15, scale = 2)
    private BigDecimal igstAmount = BigDecimal.ZERO;

    @Column(name = "cess_amount", precision = 15, scale = 2)
    private BigDecimal cessAmount = BigDecimal.ZERO;

    @Column(name = "round_off", precision = 10, scale = 2)
    private BigDecimal roundOff = BigDecimal.ZERO;

    @Column(name = "total_invoice_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalInvoiceAmount = BigDecimal.ZERO;

    // Payment info
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentType paymentType = PaymentType.CASH;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "paid_amount", precision = 15, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(name = "balance_amount", precision = 15, scale = 2)
    private BigDecimal balanceAmount = BigDecimal.ZERO;

    // Additional info
    @Lob
    @Column(name = "notes")
    private String notes;

    @Lob
    @Column(name = "terms_conditions")
    private String termsConditions;

    @Column(name = "transport_details", length = 500)
    private String transportDetails;

    @Column(name = "vehicle_number", length = 20)
    private String vehicleNumber;

    @Column(name = "eway_bill_number", length = 20)
    private String ewayBillNumber;

    // Future enhancements: Multi-currency support
    @Column(name = "currency", length = 3)
    private String currency = "INR";

    @Column(name = "exchange_rate", precision = 15, scale = 6)
    private BigDecimal exchangeRate = new BigDecimal("1.000000");

    @Column(name = "base_currency_amount", precision = 15, scale = 2)
    private BigDecimal baseCurrencyAmount;

    // Future enhancements: E-invoice and digital signatures
    @Column(name = "einvoice_irn", length = 100)
    private String einvoiceIrn; // Invoice Reference Number for e-invoicing

    @Column(name = "einvoice_ack_no", length = 50)
    private String einvoiceAckNo; // Acknowledgement number

    @Column(name = "einvoice_ack_date")
    private LocalDateTime einvoiceAckDate;

    @Lob
    @Column(name = "einvoice_qr_code")
    private String einvoiceQrCode; // QR code for e-invoice

    @Lob
    @Column(name = "digital_signature")
    private String digitalSignature; // Digital signature if required

    // Future enhancements: Approval workflow
    @Column(name = "approval_status", length = 50)
    private String approvalStatus = "DRAFT"; // DRAFT, PENDING_APPROVAL, APPROVED, REJECTED

    @Column(name = "approved_by", columnDefinition = "UUID")
    private UUID approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Lob
    @Column(name = "rejection_reason")
    private String rejectionReason;

    // Future enhancements: Recurring invoices
    @Column(name = "is_recurring")
    private Boolean isRecurring = false;

    @Column(name = "recurring_frequency", length = 20)
    private String recurringFrequency; // MONTHLY, QUARTERLY, YEARLY

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recurring_parent_id")
    private InvoiceMaster recurringParent;

    @Column(name = "next_invoice_date")
    private LocalDate nextInvoiceDate;

    // Future enhancements: Document management
    @Column(name = "pdf_url", length = 500)
    private String pdfUrl; // Generated PDF URL

    @Column(name = "attachments")
    private String attachments; // Array of attachment URLs

    // Future enhancements: Sales and analytics
    @Column(name = "sales_person_id", columnDefinition = "UUID")
    private UUID salesPersonId; // Track which salesperson generated the invoice

    @Column(name = "sales_channel", length = 50)
    private String salesChannel; // DIRECT, ONLINE, MARKETPLACE, RETAIL

    @Column(name = "order_reference", length = 100)
    private String orderReference; // Reference to original order/quotation

    // Future enhancements: Shipping and logistics
    @Column(name = "shipping_address")
    private String shippingAddress; // Complete shipping address if different from customer

    @Column(name = "shipping_cost", precision = 15, scale = 2)
    private BigDecimal shippingCost = BigDecimal.ZERO;

    @Column(name = "shipping_tracking_number", length = 100)
    private String shippingTrackingNumber;

    @Column(name = "expected_delivery_date")
    private LocalDate expectedDeliveryDate;

    // Future enhancements: Custom fields
    @Column(name = "custom_fields")
    private String customFields;

    // Status
    @Column(name = "is_cancelled")
    private Boolean isCancelled = false;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancelled_by", columnDefinition = "UUID")
    private UUID cancelledBy;

    @Lob
    @Column(name = "cancellation_reason")
    private String cancellationReason;

    // Audit fields (additional to those in Auditable)
    @Column(name = "created_by", columnDefinition = "UUID")
    private UUID createdBy;

    @Column(name = "updated_by", columnDefinition = "UUID")
    private UUID updatedBy;

    // Invoice Items relationship
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<InvoiceItems> invoiceItems;

    // Enum for invoice type
    public enum InvoiceType {
        TAX_INVOICE,
        DEBIT_NOTE,
        CREDIT_NOTE,
        RECEIPT_VOUCHER,
        PAYMENT_VOUCHER
    }

    // Enum for payment status
    public enum PaymentType {
        CASH,
        CREDIT,
        DEBIT_CARD,
        CREDIT_CARD,
        NET_BANKING,
        UPI,
        CHEQUE,
        DEMAND_DRAFT,
        BANK_TRANSFER,
        OTHER
    }
}
