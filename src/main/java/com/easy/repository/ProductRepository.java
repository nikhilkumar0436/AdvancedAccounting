package com.easy.repository;

import com.easy.entity.ProductMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<ProductMaster, UUID>, JpaSpecificationExecutor<ProductMaster> {

    /**
     * Find products by company ID
     */
    @Query("SELECT p FROM ProductMaster p WHERE p.company.id = :companyId")
    List<ProductMaster> findByCompanyId(@Param("companyId") UUID companyId);

    /**
     * Find product by product code
     */
    Optional<ProductMaster> findByProductCode(String productCode);

    /**
     * Find product by HSN/SAC code
     */
    List<ProductMaster> findByHsnSacCode(String hsnSacCode);

    /**
     * Find products by category
     */
    List<ProductMaster> findByCategory(String category);

    /**
     * Find products by product type
     */
    List<ProductMaster> findByProductType(ProductMaster.ProductType productType);

    /**
     * Find active products
     */
    List<ProductMaster> findByIsActive(Boolean isActive);

    /**
     * Find products with low stock (current stock <= reorder level)
     */
    @Query("SELECT p FROM ProductMaster p WHERE p.currentStock <= p.reorderLevel AND p.isActive = true")
    List<ProductMaster> findLowStockProducts();

    /**
     * Search products by name containing keyword
     */
    @Query("SELECT p FROM ProductMaster p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<ProductMaster> searchByProductName(@Param("keyword") String keyword);

    /**
     * Find products by company and active status with pagination
     */
    Page<ProductMaster> findByCompanyIdAndIsActive(UUID companyId, Boolean isActive, Pageable pageable);

    /**
     * Find products by company and product type with pagination
     */
    Page<ProductMaster> findByCompanyIdAndProductType(UUID companyId, ProductMaster.ProductType productType, Pageable pageable);

    /**
     * Check if product code exists for company (excluding specific product ID)
     */
    @Query("SELECT COUNT(p) > 0 FROM ProductMaster p WHERE p.productCode = :productCode AND p.company.id = :companyId AND p.id != :excludeId")
    boolean existsByProductCodeAndCompanyIdAndIdNot(@Param("productCode") String productCode,
                                                   @Param("companyId") UUID companyId,
                                                   @Param("excludeId") UUID excludeId);

    /**
     * Check if product code exists for company
     */
    @Query("SELECT COUNT(p) > 0 FROM ProductMaster p WHERE p.productCode = :productCode AND p.company.id = :companyId")
    boolean existsByProductCodeAndCompanyId(@Param("productCode") String productCode, @Param("companyId") UUID companyId);
}
