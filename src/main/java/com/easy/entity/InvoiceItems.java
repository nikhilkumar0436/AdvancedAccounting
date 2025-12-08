package com.easy.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "invoice_items")
@EntityListeners(AuditingEntityListener.class)
@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class InvoiceItems extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private InvoiceMaster invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductMaster product;

    @Column(name = "item_sequence", nullable = false)
    private Integer itemSequence;

    @Column(name = "item_description", length = 500, nullable = false)
    private String itemDescription;

    @Column(name = "hsn_sac_code", length = 10, nullable = false)
    private String hsnSacCode;

    @Column(name = "quantity", precision = 15, scale = 3, nullable = false)
    private BigDecimal quantity = new BigDecimal("1.000");

    @Column(name = "unit_of_measurement", length = 20)
    private String unitOfMeasurement = "NOS";

    @Column(name = "unit_price", precision = 15, scale = 2, nullable = false)
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    @Column(name = "discount_amount", precision = 15, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "taxable_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal taxableAmount = BigDecimal.ZERO;

    @Column(name = "gst_rate", precision = 5, scale = 2, nullable = false)
    private BigDecimal gstRate = BigDecimal.ZERO;

    @Column(name = "cgst_amount", precision = 15, scale = 2)
    private BigDecimal cgstAmount = BigDecimal.ZERO;

    @Column(name = "sgst_amount", precision = 15, scale = 2)
    private BigDecimal sgstAmount = BigDecimal.ZERO;

    @Column(name = "igst_amount", precision = 15, scale = 2)
    private BigDecimal igstAmount = BigDecimal.ZERO;

    @Column(name = "cess_rate", precision = 5, scale = 2)
    private BigDecimal cessRate = BigDecimal.ZERO;

    @Column(name = "cess_amount", precision = 15, scale = 2)
    private BigDecimal cessAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    // Future enhancements: Batch and serial number tracking
    @Column(name = "batch_number", length = 100)
    private String batchNumber;

    @Column(name = "serial_numbers", columnDefinition = "text[]")
    private String[] serialNumbers;

    @Column(name = "manufacturing_date")
    private LocalDate manufacturingDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    // Future enhancements: Warehouse and location
    @Column(name = "warehouse_id", columnDefinition = "UUID")
    private UUID warehouseId;

    @Column(name = "storage_location", length = 100)
    private String storageLocation;

    // Future enhancements: Item-level customization
    @Lob
    @Column(name = "item_notes")
    private String itemNotes;

    @Column(name = "custom_fields")
    private String customFields;

    // Unique constraint on invoice_id and item_sequence is handled at database level
}
