package com.easy.dto;

import com.easy.entity.ProductMaster;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class ProductMapper {

    public ProductResponse toResponse(ProductMaster product) {
        if (product == null) {
            return null;
        }

        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setProductName(product.getProductName());
        response.setProductCode(product.getProductCode());
        response.setHsnSacCode(product.getHsnSacCode());
        response.setProductType(product.getProductType());
        response.setUnitOfMeasurement(product.getUnitOfMeasurement());
        response.setGstRate(product.getGstRate());
        response.setCessRate(product.getCessRate());
        response.setPurchasePrice(product.getPurchasePrice());
        response.setSellingPrice(product.getSellingPrice());
        response.setMrp(product.getMrp());
        response.setDescription(product.getDescription());
        response.setCategory(product.getCategory());
        response.setOpeningStock(product.getOpeningStock());
        response.setCurrentStock(product.getCurrentStock());
        response.setReorderLevel(product.getReorderLevel());
        response.setBaseCurrency(product.getBaseCurrency());
        response.setBarcode(product.getBarcode());
        response.setSku(product.getSku());
        response.setBatchTrackingEnabled(product.getBatchTrackingEnabled());
        response.setSerialTrackingEnabled(product.getSerialTrackingEnabled());
        response.setExpiryTrackingEnabled(product.getExpiryTrackingEnabled());
        response.setWarrantyPeriodDays(product.getWarrantyPeriodDays());
        response.setHasVariants(product.getHasVariants());
        response.setVariantAttributes(product.getVariantAttributes());
        response.setPreferredSupplierId(product.getPreferredSupplierId());
        response.setMinimumOrderQuantity(product.getMinimumOrderQuantity());
        response.setLeadTimeDays(product.getLeadTimeDays());
        response.setWholesalePrice(product.getWholesalePrice());
        response.setRetailPrice(product.getRetailPrice());
        response.setDiscountApplicable(product.getDiscountApplicable());
        response.setMaxDiscountPercentage(product.getMaxDiscountPercentage());
        response.setProductImageUrl(product.getProductImageUrl());
        response.setProductImages(product.getProductImages());
        response.setWeight(product.getWeight());
        response.setDimensions(product.getDimensions());
        response.setIsPublishedOnline(product.getIsPublishedOnline());
        response.setSeoTitle(product.getSeoTitle());
        response.setSeoDescription(product.getSeoDescription());
        response.setTags(product.getTags());
        response.setCustomFields(product.getCustomFields());
        response.setIsActive(product.getIsActive());
        response.setCreatedBy(product.getCreatedBy());
        response.setUpdatedBy(product.getUpdatedBy());
        // Note: createdAt and updatedAt are private in Auditable class
        // response.setCreatedAt(product.getCreatedAt());
        // response.setUpdatedAt(product.getUpdatedAt());

        // Company information
        if (product.getCompany() != null) {
            response.setCompanyId(product.getCompany().getId());
            response.setCompanyName(product.getCompany().getCompanyName());
        }

        // Parent product information
        if (product.getParentProduct() != null) {
            response.setParentProductId(product.getParentProduct().getId());
            response.setParentProductName(product.getParentProduct().getProductName());
        }

        // Calculate derived fields
        calculateDerivedFields(response);

        return response;
    }

    public ProductMaster toEntity(ProductRequest request) {
        if (request == null) {
            return null;
        }

        ProductMaster product = new ProductMaster();
        return updateEntity(product, request);
    }

    public ProductMaster updateEntity(ProductMaster product, ProductRequest request) {
        if (product == null || request == null) {
            return product;
        }

        product.setProductName(request.getProductName());
        product.setProductCode(request.getProductCode());
        product.setHsnSacCode(request.getHsnSacCode());
        product.setProductType(request.getProductType());
        product.setUnitOfMeasurement(request.getUnitOfMeasurement());
        product.setGstRate(request.getGstRate());
        product.setCessRate(request.getCessRate());
        product.setPurchasePrice(request.getPurchasePrice());
        product.setSellingPrice(request.getSellingPrice());
        product.setMrp(request.getMrp());
        product.setDescription(request.getDescription());
        product.setCategory(request.getCategory());
        product.setOpeningStock(request.getOpeningStock());
        product.setCurrentStock(request.getCurrentStock());
        product.setReorderLevel(request.getReorderLevel());
        product.setBaseCurrency(request.getBaseCurrency());
        product.setBarcode(request.getBarcode());
        product.setSku(request.getSku());
        product.setBatchTrackingEnabled(request.getBatchTrackingEnabled());
        product.setSerialTrackingEnabled(request.getSerialTrackingEnabled());
        product.setExpiryTrackingEnabled(request.getExpiryTrackingEnabled());
        product.setWarrantyPeriodDays(request.getWarrantyPeriodDays());
        product.setHasVariants(request.getHasVariants());
        product.setVariantAttributes(isEmptyOrNull(request.getVariantAttributes()) ? null : request.getVariantAttributes());
        product.setPreferredSupplierId(request.getPreferredSupplierId());
        product.setMinimumOrderQuantity(request.getMinimumOrderQuantity());
        product.setLeadTimeDays(request.getLeadTimeDays());
        product.setWholesalePrice(request.getWholesalePrice());
        product.setRetailPrice(request.getRetailPrice());
        product.setDiscountApplicable(request.getDiscountApplicable());
        product.setMaxDiscountPercentage(request.getMaxDiscountPercentage());
        product.setProductImageUrl(request.getProductImageUrl());
        product.setProductImages(isEmptyOrNull(request.getProductImages()) ? null : request.getProductImages());
        product.setWeight(request.getWeight());
        product.setDimensions(isEmptyOrNull(request.getDimensions()) ? null : request.getDimensions());
        product.setIsPublishedOnline(request.getIsPublishedOnline());
        product.setSeoTitle(request.getSeoTitle());
        product.setSeoDescription(request.getSeoDescription());
        product.setTags(request.getTags());

        // Handle JSONB fields - convert empty strings to null to avoid PostgreSQL type errors
        product.setCustomFields(request.getCustomFields());
        product.setDimensions(isEmptyOrNull(request.getDimensions()) ? null : request.getDimensions());
        product.setProductImages(isEmptyOrNull(request.getProductImages()) ? null : request.getProductImages());
        product.setVariantAttributes(isEmptyOrNull(request.getVariantAttributes()) ? null : request.getVariantAttributes());

        product.setIsActive(request.getIsActive());
        product.setCreatedBy(request.getCreatedBy());
        product.setUpdatedBy(request.getUpdatedBy());

        return product;
    }

    private boolean isEmptyOrNull(String value) {
        return value == null || value.trim().isEmpty();
    }

    private void calculateDerivedFields(ProductResponse response) {
        // Calculate profit margin and percentage
        if (response.getPurchasePrice() != null && response.getSellingPrice() != null) {
            BigDecimal profitMargin = response.getSellingPrice().subtract(response.getPurchasePrice());
            response.setProfitMargin(profitMargin);

            if (response.getPurchasePrice().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal profitPercentage = profitMargin
                        .divide(response.getPurchasePrice(), 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"));
                response.setProfitPercentage(profitPercentage);
            }
        }

        // Check if stock is low
        if (response.getCurrentStock() != null && response.getReorderLevel() != null) {
            response.setIsLowStock(response.getCurrentStock().compareTo(response.getReorderLevel()) <= 0);
        }

        // Calculate stock value
        if (response.getCurrentStock() != null && response.getPurchasePrice() != null) {
            BigDecimal stockValue = response.getCurrentStock().multiply(response.getPurchasePrice());
            response.setStockValue(stockValue);
        }
    }
}
