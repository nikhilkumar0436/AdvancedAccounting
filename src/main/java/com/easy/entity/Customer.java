package com.easy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "customers")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Customer extends Auditable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = true)
    @JsonIgnore
    private CompanyMaster company;

    @Column(name = "customer_name", nullable = false, length = 255)
    private String customerName;

    @Column(name = "gstin", length = 15)
    private String gstin;

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_type", nullable = false)
    private CustomerType customerType = CustomerType.UNREGISTERED;

    @Column(name = "pan", length = 10)
    private String pan;

    @Column(name = "address_line1", length = 500)
    private String addressLine1;

    @Column(name = "address_line2", length = 500)
    private String addressLine2;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 50)
    private String state;

    @Column(name = "state_code", length = 2)
    private String stateCode;

    @Column(name = "pincode", length = 6)
    private String pincode;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "phone", length = 15)
    private String phone;

    @Column(name = "mobile", length = 15)
    private String mobile;

    @Column(name = "whatsapp_number", length = 15)
    private String whatsappNumber;

    @Column(name = "credit_limit", precision = 15, scale = 2)
    private BigDecimal creditLimit = BigDecimal.valueOf(0.00);

    @Column(name = "opening_balance", precision = 15, scale = 2)
    private BigDecimal openingBalance = BigDecimal.valueOf(0.00);

    @Column(name = "outstanding_balance", precision = 15, scale = 2)
    private BigDecimal outstandingBalance = BigDecimal.valueOf(0.00);

    @Column(name = "contact_person_name", length = 255)
    private String contactPersonName;

    @Column(name = "contact_person_phone", length = 15)
    private String contactPersonPhone;

    @Lob
    @Column(name = "notes")
    private String notes;

    // Future enhancements: Multi-currency support
    @Column(name = "preferred_currency", length = 3)
    private String preferredCurrency = "INR";

    // Future enhancements: Customer categorization and segmentation
    @Column(name = "customer_category", length = 50)
    private String customerCategory; // VIP, REGULAR, WHOLESALE, RETAIL

    @Column(name = "customer_segment", length = 50)
    private String customerSegment; // Can be used for targeted marketing

    @ElementCollection
    @Column(name = "tags")
    private List<String> tags; // Array of tags for flexible categorization

    // Future enhancements: Payment and credit management
    @Column(name = "payment_terms", length = 100)
    private String paymentTerms; // NET_30, NET_60, ADVANCE, etc.

    @Column(name = "credit_days")
    private Integer creditDays = 0;

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage = BigDecimal.valueOf(0.00); // Special discount for customer

    // Future enhancements: Integration with CRM and external systems
    @Column(name = "external_customer_id", length = 100)
    private String externalCustomerId; // ID from external CRM/ERP system

    @Column(name = "source", length = 50)
    private String source; // DIRECT, WEBSITE, REFERRAL, MARKETPLACE

    // Future enhancements: Location and delivery preferences
    @Column(name = "billing_address_same_as_shipping")
    private Boolean billingAddressSameAsShipping = true;

    @Column(name = "shipping_address_line1", length = 500)
    private String shippingAddressLine1;

    @Column(name = "shipping_address_line2", length = 500)
    private String shippingAddressLine2;

    @Column(name = "shipping_city", length = 100)
    private String shippingCity;

    @Column(name = "shipping_state", length = 50)
    private String shippingState;

    @Column(name = "shipping_pincode", length = 6)
    private String shippingPincode;

    // Future enhancements: Custom fields for flexibility
    // TODO: Re-enable custom fields with proper JSON handling
    // @Column(name = "custom_fields", columnDefinition = "jsonb")
    // @Convert(converter = com.easy.entity.converter.JsonConverter.class)
    // private Object customFields; // Store additional custom data

    @Transient
    private Object customFields; // Temporary transient field to avoid mapping issues

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_cash_customer")
    private Boolean isCashCustomer = false;

    @Column(name = "created_by", columnDefinition = "UUID")
    private UUID createdBy;

    @Column(name = "updated_by", columnDefinition = "UUID")
    private UUID updatedBy;

    // Invoices relationship
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<InvoiceMaster> invoices;

    // Enum for customer type
    public enum CustomerType {
        REGISTERED,
        UNREGISTERED,
        CASH_CUSTOMER,
        WALK_IN
    }

}
