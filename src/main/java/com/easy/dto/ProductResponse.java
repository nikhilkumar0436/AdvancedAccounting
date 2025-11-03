package com.easy.dto;

import com.easy.entity.ProductMaster;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class ProductResponse {

    private UUID id;
    private String productName;
    private String productCode;
    private String hsnSacCode;
    private ProductMaster.ProductType productType;
    private String unitOfMeasurement;
    private BigDecimal gstRate;
    private BigDecimal cessRate;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;
    private BigDecimal mrp;
    private String description;
    private String category;
    private BigDecimal openingStock;
    private BigDecimal currentStock;
    private BigDecimal reorderLevel;
    private String baseCurrency;
    private String barcode;
    private String sku;
    private Boolean batchTrackingEnabled;
    private Boolean serialTrackingEnabled;
    private Boolean expiryTrackingEnabled;
    private Integer warrantyPeriodDays;
    private Boolean hasVariants;
    private UUID parentProductId;
    private String variantAttributes;
    private UUID preferredSupplierId;
    private BigDecimal minimumOrderQuantity;
    private Integer leadTimeDays;
    private BigDecimal wholesalePrice;
    private BigDecimal retailPrice;
    private Boolean discountApplicable;
    private BigDecimal maxDiscountPercentage;
    private String productImageUrl;
    private String productImages;
    private BigDecimal weight;
    private String dimensions;
    private Boolean isPublishedOnline;
    private String seoTitle;
    private String seoDescription;
    private String tags;
    private String customFields;
    private Boolean isActive;
    private UUID createdBy;
    private UUID updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Company information (basic details only to avoid circular references)
    private UUID companyId;
    private String companyName;

    // Parent product information (if applicable)
    private String parentProductName;

    // Calculated fields
    private BigDecimal profitMargin;
    private BigDecimal profitPercentage;
    private Boolean isLowStock;
    private BigDecimal stockValue;
}
