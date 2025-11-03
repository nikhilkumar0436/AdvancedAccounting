package com.easy.request.product;

import com.easy.entity.ProductMaster;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductSearchRequest implements Serializable {

    private int page = 0;
    private int size = 100;
    private String sortBy = "productName";
    private String sortDirection = "ASC";
    private FilterBy filterBy;

    @Data
    public static class FilterBy {
        // Basic product information
        private String productName;
        private String productCode;
        private String hsnSacCode;
        private ProductMaster.ProductType productType;
        private String category;

        // Company filter
        private UUID companyId;

        // Price range filters
        private BigDecimal sellingPriceFrom;
        private BigDecimal sellingPriceTo;
        private BigDecimal purchasePriceFrom;
        private BigDecimal purchasePriceTo;

        // Stock filters
        private BigDecimal currentStockFrom;
        private BigDecimal currentStockTo;
        private Boolean isLowStock; // currentStock <= reorderLevel

        // Status filters
        private Boolean isActive;
        private Boolean isPublishedOnline;

        // GST rate filters
        private BigDecimal gstRateFrom;
        private BigDecimal gstRateTo;

        // Tracking filters
        private Boolean batchTrackingEnabled;
        private Boolean serialTrackingEnabled;
        private Boolean expiryTrackingEnabled;

        // Variant filters
        private Boolean hasVariants;
        private UUID parentProductId;

        // Supplier filter
        private UUID preferredSupplierId;

        // Date range filters
        private LocalDate createdDateFrom;
        private LocalDate createdDateTo;
        private LocalDate updatedDateFrom;
        private LocalDate updatedDateTo;

        // Search filters
        private String searchKeyword; // Search in product name, code, description
        private String barcode;
        private String sku;
        private String tags;
    }
}
