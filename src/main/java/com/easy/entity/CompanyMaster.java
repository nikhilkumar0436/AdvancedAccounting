package com.easy.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "company_master")
@EntityListeners(AuditingEntityListener.class)
@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CompanyMaster extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "company_name", nullable = false, length = 255)
    private String companyName;

    @Column(name = "gstin", unique = true, nullable = false, length = 15)
    private String gstin;

    @Column(name = "pan", nullable = false, length = 10)
    private String pan;

    @Column(name = "address_line1", length = 500)
    private String addressLine1;

    @Column(name = "address_line2", length = 500)
    private String addressLine2;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", nullable = false, length = 50)
    private String state;

    @Column(name = "state_code", nullable = false, length = 2)
    private String stateCode;

    @Column(name = "pincode", length = 6)
    private String pincode;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "phone", length = 15)
    private String phone;

    @Column(name = "mobile", length = 15)
    private String mobile;

    @Column(name = "website", length = 255)
    private String website;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "financial_year_start")
    private LocalDate financialYearStart = LocalDate.of(2024, 4, 1);
    @Column(name = "financial_year_end")
    private LocalDate financialYearEnd = LocalDate.of(2025, 3, 31);

    @Column(name = "invoice_prefix", length = 10)
    private String invoicePrefix = "INV";

    @Column(name = "terms_and_conditions", columnDefinition = "TEXT")
    private String termsAndConditions;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "bank_account_number", length = 30)
    private String bankAccountNumber;

    @Column(name = "bank_ifsc_code", length = 11)
    private String bankIfscCode;

    @Column(name = "bank_branch", length = 100)
    private String bankBranch;

    // Future enhancements: Multi-currency support
    @Column(name = "base_currency", length = 3)
    private String baseCurrency = "INR";

    @Column(name = "currency_symbol", length = 10)
    private String currencySymbol = "₹";

    // Future enhancements: Multi-branch/location support
    @Column(name = "parent_company_id", columnDefinition = "UUID")
    private UUID parentCompanyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "company_type", length = 50)
    private CompanyType companyType = CompanyType.HEAD_OFFICE;

    // Future enhancements: Integration and customization
    @Column(name = "settings", columnDefinition = "jsonb")
    private String settings; // Store flexible settings like themes, preferences, custom fields

    @Column(name = "integration_config", columnDefinition = "jsonb")
    private String integrationConfig; // Store third-party integration credentials/settings

    // Future enhancements: Subscription/license management
    @Column(name = "subscription_plan", length = 50)
    private String subscriptionPlan; // BASIC, PROFESSIONAL, ENTERPRISE

    @Column(name = "subscription_valid_until")
    private LocalDate subscriptionValidUntil;

    @Column(name = "max_users")
    private Integer maxUsers = 5;

    @Column(name = "max_branches")
    private Integer maxBranches = 1;

    @Column(name = "is_active")
    private Boolean isActive = true;


    @Column(name = "created_by", columnDefinition = "UUID")
    private UUID createdBy;

    @Column(name = "updated_by", columnDefinition = "UUID")
    private UUID updatedBy;

    // Relationships
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Customer> customers;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InvoiceMaster> invoices;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductMaster> products;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GstReturns> gstReturns;

    // Enum for company type
    public enum CompanyType {
        HEAD_OFFICE,
        BRANCH,
        SUBSIDIARY
    }

}
