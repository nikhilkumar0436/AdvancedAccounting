package com.easy.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "product_master")
@EntityListeners(AuditingEntityListener.class)
@Data
public class ProductMaster extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private CompanyMaster company;

    @Column(name = "product_code", length = 50, unique = true)
    private String productCode;

    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @Column(name = "hsn_sac_code", nullable = false, length = 10)
    private String hsnSacCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = false)
    private ProductType productType = ProductType.GOODS;

    @Column(name = "unit_of_measurement", length = 20)
    private String unitOfMeasurement = "NOS";

    @Column(name = "gst_rate", precision = 5, scale = 2, nullable = false)
    private BigDecimal gstRate = BigDecimal.ZERO;

    @Column(name = "cess_rate", precision = 5, scale = 2)
    private BigDecimal cessRate = BigDecimal.ZERO;

    @Column(name = "purchase_price", precision = 15, scale = 2)
    private BigDecimal purchasePrice = BigDecimal.ZERO;

    @Column(name = "selling_price", precision = 15, scale = 2, nullable = false)
    private BigDecimal sellingPrice = BigDecimal.ZERO;

    @Column(name = "mrp", precision = 15, scale = 2)
    private BigDecimal mrp;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "opening_stock", precision = 15, scale = 3)
    private BigDecimal openingStock = BigDecimal.ZERO;

    @Column(name = "current_stock", precision = 15, scale = 3)
    private BigDecimal currentStock = BigDecimal.ZERO;

    @Column(name = "reorder_level", precision = 15, scale = 3)
    private BigDecimal reorderLevel = BigDecimal.ZERO;

    // Future enhancements: Multi-currency and pricing
    @Column(name = "base_currency", length = 3)
    private String baseCurrency = "INR";

    // Future enhancements: Advanced inventory management
    @Column(name = "barcode", length = 100)
    private String barcode;

    @Column(name = "sku", length = 100)
    private String sku;

    @Column(name = "batch_tracking_enabled")
    private Boolean batchTrackingEnabled = false;

    @Column(name = "serial_tracking_enabled")
    private Boolean serialTrackingEnabled = false;

    @Column(name = "expiry_tracking_enabled")
    private Boolean expiryTrackingEnabled = false;

    @Column(name = "warranty_period_days")
    private Integer warrantyPeriodDays = 0;

    // Future enhancements: Product variants and attributes
    @Column(name = "has_variants")
    private Boolean hasVariants = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_product_id")
    private ProductMaster parentProduct;

    @Column(name = "variant_attributes", columnDefinition = "jsonb")
    private String variantAttributes;

    // Future enhancements: Supplier and procurement
    @Column(name = "preferred_supplier_id", columnDefinition = "UUID")
    private UUID preferredSupplierId;

    @Column(name = "minimum_order_quantity", precision = 15, scale = 3)
    private BigDecimal minimumOrderQuantity = BigDecimal.ONE;

    @Column(name = "lead_time_days")
    private Integer leadTimeDays = 0;

    // Future enhancements: Pricing tiers and discounts
    @Column(name = "wholesale_price", precision = 15, scale = 2)
    private BigDecimal wholesalePrice;

    @Column(name = "retail_price", precision = 15, scale = 2)
    private BigDecimal retailPrice;

    @Column(name = "discount_applicable")
    private Boolean discountApplicable = true;

    @Column(name = "max_discount_percentage", precision = 5, scale = 2)
    private BigDecimal maxDiscountPercentage = new BigDecimal("100.00");

    // Future enhancements: E-commerce and digital features
    @Column(name = "product_image_url", length = 500)
    private String productImageUrl;

    @Column(name = "product_images", columnDefinition = "jsonb")
    private String productImages;

    @Column(name = "weight", precision = 10, scale = 3)
    private BigDecimal weight;

    @Column(name = "dimensions", columnDefinition = "jsonb")
    private String dimensions;

    @Column(name = "is_published_online")
    private Boolean isPublishedOnline = false;

    @Column(name = "seo_title", length = 255)
    private String seoTitle;

    @Lob
    @Column(name = "seo_description")
    private String seoDescription;

    @Column(name = "tags")
    private String tags;

    // Future enhancements: Custom fields
    @Column(name = "custom_fields", columnDefinition = "jsonb")
    private String customFields;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_by", columnDefinition = "UUID")
    private UUID createdBy;

    @Column(name = "updated_by", columnDefinition = "UUID")
    private UUID updatedBy;

    // Enum for product type
    public enum ProductType {
        GOODS,
        SERVICES
    }
}
