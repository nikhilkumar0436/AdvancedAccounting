package com.easy.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "gst_returns")
@EntityListeners(AuditingEntityListener.class)
@Data
public class GstReturns extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private CompanyMaster company;

    @Enumerated(EnumType.STRING)
    @Column(name = "return_type", nullable = false)
    private GstReturnType returnType;

    @Column(name = "return_period", length = 7, nullable = false)
    private String returnPeriod; // Format: 2025-01

    @Column(name = "financial_year", length = 9, nullable = false)
    private String financialYear; // Format: 2024-2025

    // Totals
    @Column(name = "total_invoices")
    private Integer totalInvoices = 0;

    @Column(name = "total_sales", precision = 15, scale = 2)
    private BigDecimal totalSales = BigDecimal.ZERO;

    @Column(name = "taxable_sales", precision = 15, scale = 2)
    private BigDecimal taxableSales = BigDecimal.ZERO;

    @Column(name = "igst_amount", precision = 15, scale = 2)
    private BigDecimal igstAmount = BigDecimal.ZERO;

    @Column(name = "cgst_amount", precision = 15, scale = 2)
    private BigDecimal cgstAmount = BigDecimal.ZERO;

    @Column(name = "sgst_amount", precision = 15, scale = 2)
    private BigDecimal sgstAmount = BigDecimal.ZERO;

    @Column(name = "cess_amount", precision = 15, scale = 2)
    private BigDecimal cessAmount = BigDecimal.ZERO;

    // Filing status
    @Enumerated(EnumType.STRING)
    @Column(name = "filing_status", nullable = false)
    private FilingStatus filingStatus = FilingStatus.DRAFT;

    @Column(name = "filed_date")
    private LocalDateTime filedDate;

    @Column(name = "filed_by", columnDefinition = "UUID")
    private UUID filedBy;

    @Column(name = "arn_number", length = 50)
    private String arnNumber; // Acknowledgement Reference Number

    // Export data
    @Column(name = "export_data")
    private String exportData; // Complete GSTR data in JSON format

    // Future enhancements: Amendments and revisions
    @Column(name = "is_amendment")
    private Boolean isAmendment = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_return_id")
    private GstReturns originalReturn;

    @Lob
    @Column(name = "amendment_reason")
    private String amendmentReason;

    // Future enhancements: ITC (Input Tax Credit) tracking
    @Column(name = "itc_claimed", precision = 15, scale = 2)
    private BigDecimal itcClaimed = BigDecimal.ZERO;

    @Column(name = "itc_reversed", precision = 15, scale = 2)
    private BigDecimal itcReversed = BigDecimal.ZERO;

    @Column(name = "itc_ineligible", precision = 15, scale = 2)
    private BigDecimal itcIneligible = BigDecimal.ZERO;

    // Future enhancements: Interest and penalties
    @Column(name = "interest_amount", precision = 15, scale = 2)
    private BigDecimal interestAmount = BigDecimal.ZERO;

    @Column(name = "late_fee", precision = 15, scale = 2)
    private BigDecimal lateFee = BigDecimal.ZERO;

    @Column(name = "penalty_amount", precision = 15, scale = 2)
    private BigDecimal penaltyAmount = BigDecimal.ZERO;

    // Future enhancements: Payment tracking
    @Column(name = "payment_reference", length = 100)
    private String paymentReference;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "payment_amount", precision = 15, scale = 2)
    private BigDecimal paymentAmount = BigDecimal.ZERO;

    // Future enhancements: Document attachments
    @Column(name = "attachments", columnDefinition = "jsonb")
    private String attachments; // Supporting documents

    // Enums
    public enum GstReturnType {
        GSTR1,
        GSTR2,
        GSTR3B,
        GSTR4,
        GSTR5,
        GSTR6,
        GSTR7,
        GSTR8,
        GSTR9,
        GSTR10,
        GSTR11,
        ANNUAL_RETURN,
        COMPOSITION_QUARTERLY
    }

    public enum FilingStatus {
        DRAFT,
        PREPARED,
        UNDER_REVIEW,
        APPROVED,
        FILED,
        ACKNOWLEDGED,
        PROCESSED,
        REJECTED,
        AMENDED
    }

    // Unique constraint on company_id, return_type, return_period, is_amendment is handled at database level
}
