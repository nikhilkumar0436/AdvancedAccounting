package com.easy.dto;

import com.easy.entity.ProductMaster;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(max = 255, message = "Product name must not exceed 255 characters")
    private String productName;

    @NotNull(message = "Company ID is required")
    private UUID companyId;

    @Size(max = 50, message = "Product code must not exceed 50 characters")
    private String productCode;

    @NotBlank(message = "HSN/SAC code is required")
    @Size(max = 10, message = "HSN/SAC code must not exceed 10 characters")
    private String hsnSacCode;

    @NotNull(message = "Product type is required")
    private ProductMaster.ProductType productType = ProductMaster.ProductType.GOODS;

    @Size(max = 20, message = "Unit of measurement must not exceed 20 characters")
    private String unitOfMeasurement = "NOS";

    @NotNull(message = "GST rate is required")
    @DecimalMin(value = "0.0", message = "GST rate must be non-negative")
    @DecimalMax(value = "100.0", message = "GST rate must not exceed 100%")
    private BigDecimal gstRate = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Cess rate must be non-negative")
    @DecimalMax(value = "100.0", message = "Cess rate must not exceed 100%")
    private BigDecimal cessRate = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Purchase price must be non-negative")
    private BigDecimal purchasePrice = BigDecimal.ZERO;

    @NotNull(message = "Selling price is required")
    @DecimalMin(value = "0.0", message = "Selling price must be non-negative")
    private BigDecimal sellingPrice = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "MRP must be non-negative")
    private BigDecimal mrp;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;

    @DecimalMin(value = "0.0", message = "Opening stock must be non-negative")
    private BigDecimal openingStock = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Current stock must be non-negative")
    private BigDecimal currentStock = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Reorder level must be non-negative")
    private BigDecimal reorderLevel = BigDecimal.ZERO;

    @Size(max = 3, message = "Base currency must not exceed 3 characters")
    private String baseCurrency = "INR";

    @Size(max = 100, message = "Barcode must not exceed 100 characters")
    private String barcode;

    @Size(max = 100, message = "SKU must not exceed 100 characters")
    private String sku;

    private Boolean batchTrackingEnabled = false;
    private Boolean serialTrackingEnabled = false;
    private Boolean expiryTrackingEnabled = false;

    @Min(value = 0, message = "Warranty period must be non-negative")
    private Integer warrantyPeriodDays = 0;

    private Boolean hasVariants = false;
    private UUID parentProductId;
    private String variantAttributes;
    private UUID preferredSupplierId;

    @DecimalMin(value = "0.001", message = "Minimum order quantity must be positive")
    private BigDecimal minimumOrderQuantity = BigDecimal.ONE;

    @Min(value = 0, message = "Lead time must be non-negative")
    private Integer leadTimeDays = 0;

    @DecimalMin(value = "0.0", message = "Wholesale price must be non-negative")
    private BigDecimal wholesalePrice;

    @DecimalMin(value = "0.0", message = "Retail price must be non-negative")
    private BigDecimal retailPrice;

    private Boolean discountApplicable = true;

    @DecimalMin(value = "0.0", message = "Maximum discount percentage must be non-negative")
    @DecimalMax(value = "100.0", message = "Maximum discount percentage must not exceed 100%")
    private BigDecimal maxDiscountPercentage = new BigDecimal("100.00");

    @Size(max = 500, message = "Product image URL must not exceed 500 characters")
    private String productImageUrl;

    private String productImages;

    @DecimalMin(value = "0.0", message = "Weight must be non-negative")
    private BigDecimal weight;

    private String dimensions;
    private Boolean isPublishedOnline = false;

    @Size(max = 255, message = "SEO title must not exceed 255 characters")
    private String seoTitle;

    @Size(max = 1000, message = "SEO description must not exceed 1000 characters")
    private String seoDescription;

    private String tags;
    private String customFields;
    private Boolean isActive = true;
    private UUID createdBy;
    private UUID updatedBy;
}
