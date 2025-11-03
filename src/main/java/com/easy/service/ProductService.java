package com.easy.service;

import com.easy.dto.ProductMapper;
import com.easy.dto.ProductRequest;
import com.easy.dto.ProductResponse;
import com.easy.entity.CompanyMaster;
import com.easy.entity.ProductMaster;
import com.easy.exception.ResourceNotFoundException;
import com.easy.exception.DuplicateResourceException;
import com.easy.repository.CompanyRepository;
import com.easy.repository.ProductRepository;
import com.easy.request.product.ProductSearchRequest;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CompanyRepository companyRepository;

    public Page<ProductResponse> getProductsWithFilters(ProductSearchRequest request) {
        Specification<ProductMaster> specification = createSpecification(request.getFilterBy());
        Pageable pageable = createPageable(request);
        Page<ProductMaster> products = productRepository.findAll(specification, pageable);
        return products.map(productMapper::toResponse);
    }

    public Optional<ProductMaster> getProductById(UUID id) {
        return productRepository.findById(id);
    }

    public ProductResponse getProductResponseById(UUID id) {
        ProductMaster product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
        return productMapper.toResponse(product);
    }

    public List<ProductResponse> getProductResponsesByCompany(UUID companyId) {
        List<ProductMaster> products = productRepository.findByCompanyId(companyId);
        return products.stream()
                .map(productMapper::toResponse)
                .toList();
    }

    public Optional<ProductMaster> getProductByProductCode(String productCode) {
        return productRepository.findByProductCode(productCode);
    }

    public List<ProductMaster> getProductsByHsnSacCode(String hsnSacCode) {
        return productRepository.findByHsnSacCode(hsnSacCode);
    }

    public List<ProductMaster> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public List<ProductMaster> getProductsByType(ProductMaster.ProductType productType) {
        return productRepository.findByProductType(productType);
    }

    public List<ProductMaster> getLowStockProducts() {
        return productRepository.findLowStockProducts();
    }

    public List<ProductMaster> searchProductsByName(String keyword) {
        return productRepository.searchByProductName(keyword);
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Creating new product with name: {}", request.getProductName());

        // Validate business rules
        validateProductRequest(request, null);

        // Get company
        CompanyMaster company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + request.getCompanyId()));

        // Convert request to entity
        ProductMaster product = productMapper.toEntity(request);
        product.setCompany(company);

        // Set parent product if specified
        if (request.getParentProductId() != null) {
            ProductMaster parentProduct = productRepository.findById(request.getParentProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent product not found with ID: " + request.getParentProductId()));
            product.setParentProduct(parentProduct);
        }

        // Save product
        ProductMaster savedProduct = productRepository.save(product);
        log.info("Product created successfully with ID: {}", savedProduct.getId());

        return productMapper.toResponse(savedProduct);
    }

    @Transactional
    public ProductResponse updateProduct(UUID id, ProductRequest request) {
        log.info("Updating product with ID: {}", id);

        // Validate business rules
        validateProductRequest(request, id);

        // Get existing product
        ProductMaster existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        // Get company
        CompanyMaster company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + request.getCompanyId()));

        // Update entity with request data
        productMapper.updateEntity(existingProduct, request);
        existingProduct.setCompany(company);

        // Set parent product if specified
        if (request.getParentProductId() != null) {
            ProductMaster parentProduct = productRepository.findById(request.getParentProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent product not found with ID: " + request.getParentProductId()));
            existingProduct.setParentProduct(parentProduct);
        } else {
            existingProduct.setParentProduct(null);
        }

        // Save updated product
        ProductMaster updatedProduct = productRepository.save(existingProduct);
        log.info("Product updated successfully: {}", updatedProduct.getProductName());

        return productMapper.toResponse(updatedProduct);
    }

    @Transactional
    public ProductResponse updateProductStatus(UUID id, Boolean isActive) {
        log.info("Updating status for product ID: {} to {}", id, isActive);

        ProductMaster product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        product.setIsActive(isActive);
        ProductMaster updatedProduct = productRepository.save(product);

        log.info("Product status updated successfully for ID: {}", id);
        return productMapper.toResponse(updatedProduct);
    }

    @Transactional
    public void deleteProduct(UUID id) {
        log.info("Deleting product with ID: {}", id);

        ProductMaster product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        // Check for dependencies before deletion
        // Note: Add business logic here to check if product is used in invoices, etc.

        productRepository.delete(product);
        log.info("Product deleted successfully with ID: {}", id);
    }

    private void validateProductRequest(ProductRequest request, UUID excludeId) {
        // Validate product code uniqueness within company
        if (request.getProductCode() != null && !request.getProductCode().trim().isEmpty()) {
            boolean codeExists;
            if (excludeId != null) {
                codeExists = productRepository.existsByProductCodeAndCompanyIdAndIdNot(
                        request.getProductCode(), request.getCompanyId(), excludeId);
            } else {
                codeExists = productRepository.existsByProductCodeAndCompanyId(
                        request.getProductCode(), request.getCompanyId());
            }

            if (codeExists) {
                throw new DuplicateResourceException("Product code already exists: " + request.getProductCode());
            }
        }

        // Validate price logic
        if (request.getPurchasePrice() != null && request.getSellingPrice() != null) {
            if (request.getSellingPrice().compareTo(request.getPurchasePrice()) < 0) {
                log.warn("Selling price is less than purchase price for product: {}", request.getProductName());
            }
        }

        // Validate MRP logic
        if (request.getMrp() != null && request.getSellingPrice() != null) {
            if (request.getSellingPrice().compareTo(request.getMrp()) > 0) {
                log.warn("Selling price is greater than MRP for product: {}", request.getProductName());
            }
        }
    }

    private Specification<ProductMaster> createSpecification(ProductSearchRequest.FilterBy filterBy) {
        return (Root<ProductMaster> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filterBy != null) {
                // Product name filter
                if (filterBy.getProductName() != null && !filterBy.getProductName().trim().isEmpty()) {
                    predicates.add(cb.like(cb.lower(root.get("productName")),
                            "%" + filterBy.getProductName().toLowerCase() + "%"));
                }

                // Product code filter
                if (filterBy.getProductCode() != null && !filterBy.getProductCode().trim().isEmpty()) {
                    predicates.add(cb.like(cb.lower(root.get("productCode")),
                            "%" + filterBy.getProductCode().toLowerCase() + "%"));
                }

                // HSN/SAC code filter
                if (filterBy.getHsnSacCode() != null && !filterBy.getHsnSacCode().trim().isEmpty()) {
                    predicates.add(cb.equal(root.get("hsnSacCode"), filterBy.getHsnSacCode()));
                }

                // Product type filter
                if (filterBy.getProductType() != null) {
                    predicates.add(cb.equal(root.get("productType"), filterBy.getProductType()));
                }

                // Category filter
                if (filterBy.getCategory() != null && !filterBy.getCategory().trim().isEmpty()) {
                    predicates.add(cb.like(cb.lower(root.get("category")),
                            "%" + filterBy.getCategory().toLowerCase() + "%"));
                }

                // Company filter
                if (filterBy.getCompanyId() != null) {
                    predicates.add(cb.equal(root.get("company").get("id"), filterBy.getCompanyId()));
                }

                // Price range filters
                if (filterBy.getSellingPriceFrom() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("sellingPrice"), filterBy.getSellingPriceFrom()));
                }
                if (filterBy.getSellingPriceTo() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("sellingPrice"), filterBy.getSellingPriceTo()));
                }

                // Stock filters
                if (filterBy.getCurrentStockFrom() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("currentStock"), filterBy.getCurrentStockFrom()));
                }
                if (filterBy.getCurrentStockTo() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("currentStock"), filterBy.getCurrentStockTo()));
                }

                // Low stock filter
                if (filterBy.getIsLowStock() != null && filterBy.getIsLowStock()) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("currentStock"), root.get("reorderLevel")));
                }

                // Status filters
                if (filterBy.getIsActive() != null) {
                    predicates.add(cb.equal(root.get("isActive"), filterBy.getIsActive()));
                }

                // Search keyword filter (searches in product name, code, and description)
                if (filterBy.getSearchKeyword() != null && !filterBy.getSearchKeyword().trim().isEmpty()) {
                    String keyword = "%" + filterBy.getSearchKeyword().toLowerCase() + "%";
                    Predicate searchPredicate = cb.or(
                            cb.like(cb.lower(root.get("productName")), keyword),
                            cb.like(cb.lower(root.get("productCode")), keyword),
                            cb.like(cb.lower(root.get("description")), keyword)
                    );
                    predicates.add(searchPredicate);
                }

                // Date range filters
                if (filterBy.getCreatedDateFrom() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), filterBy.getCreatedDateFrom().atStartOfDay()));
                }
                if (filterBy.getCreatedDateTo() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), filterBy.getCreatedDateTo().atTime(23, 59, 59)));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Pageable createPageable(ProductSearchRequest request) {
        Sort.Direction direction = Sort.Direction.fromString(request.getSortDirection());
        Sort sort = Sort.by(direction, request.getSortBy());
        return PageRequest.of(request.getPage(), request.getSize(), sort);
    }
}
