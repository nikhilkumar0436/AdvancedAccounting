package com.easy.controller;

import com.easy.dto.ProductRequest;
import com.easy.dto.ProductResponse;
import com.easy.entity.ProductMaster;
import com.easy.request.product.ProductSearchRequest;
import com.easy.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Product Management", description = "APIs for managing products")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/search")
    @Operation(summary = "Get products with filters and pagination",
            description = "Retrieve a paginated list of products with optional filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<ProductResponse>> getProducts(
            @Parameter(description = "Filter and pagination request") @Valid @RequestBody ProductSearchRequest request) {

        log.info("Fetching products with filters: page={}, size={}", request.getPage(), request.getSize());
        Page<ProductResponse> products = productService.getProductsWithFilters(request);
        log.info("Retrieved {} products", products.getTotalElements());

        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieve a specific product by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ProductResponse> getProductById(
            @Parameter(description = "Product ID", required = true)
            @PathVariable @NotNull UUID id) {

        log.info("Fetching product with ID: {}", id);
        ProductResponse product = productService.getProductResponseById(id);
        log.info("Product found: {}", product.getProductName());

        return ResponseEntity.ok(product);
    }

    @GetMapping("/company/{companyId}")
    @Operation(summary = "Get products by company", description = "Retrieve all products for a specific company")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Company not found"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ProductResponse>> getProductsByCompany(
            @Parameter(description = "Company ID", required = true)
            @PathVariable @NotNull UUID companyId) {

        log.info("Fetching products for company ID: {}", companyId);
        List<ProductResponse> products = productService.getProductResponsesByCompany(companyId);
        log.info("Retrieved {} products for company", products.size());

        return ResponseEntity.ok(products);
    }

    @GetMapping("/code/{productCode}")
    @Operation(summary = "Get product by product code", description = "Retrieve a product by their product code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ProductMaster> getProductByCode(
            @Parameter(description = "Product Code", required = true)
            @PathVariable String productCode) {

        log.info("Fetching product with code: {}", productCode);
        return productService.getProductByProductCode(productCode)
                .map(product -> {
                    log.info("Product found with code: {}", productCode);
                    return ResponseEntity.ok(product);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/hsn/{hsnSacCode}")
    @Operation(summary = "Get products by HSN/SAC code", description = "Retrieve products by their HSN/SAC code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products found"),
            @ApiResponse(responseCode = "404", description = "No products found"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ProductMaster>> getProductsByHsnSacCode(
            @Parameter(description = "HSN/SAC Code", required = true)
            @PathVariable String hsnSacCode) {

        log.info("Fetching products with HSN/SAC code: {}", hsnSacCode);
        List<ProductMaster> products = productService.getProductsByHsnSacCode(hsnSacCode);
        log.info("Found {} products with HSN/SAC code: {}", products.size(), hsnSacCode);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get products by category", description = "Retrieve products by their category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products found"),
            @ApiResponse(responseCode = "404", description = "No products found"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ProductMaster>> getProductsByCategory(
            @Parameter(description = "Category", required = true)
            @PathVariable String category) {

        log.info("Fetching products with category: {}", category);
        List<ProductMaster> products = productService.getProductsByCategory(category);
        log.info("Found {} products with category: {}", products.size(), category);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Get low stock products", description = "Retrieve products with low stock (current stock <= reorder level)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Low stock products retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ProductMaster>> getLowStockProducts() {

        log.info("Fetching low stock products");
        List<ProductMaster> products = productService.getLowStockProducts();
        log.info("Found {} low stock products", products.size());
        return ResponseEntity.ok(products);
    }

    @PostMapping
    @Operation(summary = "Create new product", description = "Create a new product with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body or validation errors"),
            @ApiResponse(responseCode = "409", description = "Product already exists (duplicate product code)"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ProductResponse> createProduct(
            @Parameter(description = "Product creation request", required = true)
            @Valid @RequestBody ProductRequest request) {

        log.info("Creating new product: {}", request.getProductName());
        ProductResponse createdProduct = productService.createProduct(request);
        log.info("Product created successfully with ID: {}", createdProduct.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product", description = "Update an existing product with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body or validation errors"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "409", description = "Duplicate product code"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ProductResponse> updateProduct(
            @Parameter(description = "Product ID", required = true)
            @PathVariable @NotNull UUID id,
            @Parameter(description = "Product update request", required = true)
            @Valid @RequestBody ProductRequest request) {

        log.info("Updating product with ID: {}", id);
        ProductResponse updatedProduct = productService.updateProduct(id, request);
        log.info("Product updated successfully: {}", updatedProduct.getProductName());

        return ResponseEntity.ok(updatedProduct);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update product status", description = "Update the active status of a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product status updated successfully",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ProductResponse> updateProductStatus(
            @Parameter(description = "Product ID", required = true)
            @PathVariable @NotNull UUID id,
            @Parameter(description = "Active status", required = true)
            @RequestParam @NotNull Boolean isActive) {

        log.info("Updating status for product ID: {} to {}", id, isActive);
        ProductResponse updatedProduct = productService.updateProductStatus(id, isActive);
        log.info("Product status updated successfully for ID: {}", id);

        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product", description = "Delete a product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "409", description = "Cannot delete product with existing dependencies"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID", required = true)
            @PathVariable @NotNull UUID id) {

        log.info("Deleting product with ID: {}", id);
        productService.deleteProduct(id);
        log.info("Product deleted successfully with ID: {}", id);

        return ResponseEntity.noContent().build();
    }
}
