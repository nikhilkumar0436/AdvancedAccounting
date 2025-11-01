package com.easy.repository;

import com.easy.entity.InvoiceItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InvoiceItemsRepository extends JpaRepository<InvoiceItems, UUID> {

    // Find all items for a specific invoice
    @Query("SELECT ii FROM InvoiceItems ii WHERE ii.invoice.id = :invoiceId ORDER BY ii.itemSequence")
    List<InvoiceItems> findByInvoiceId(@Param("invoiceId") UUID invoiceId);

    // Find all items for a specific product
    @Query("SELECT ii FROM InvoiceItems ii WHERE ii.product.id = :productId")
    List<InvoiceItems> findByProductId(@Param("productId") UUID productId);

    // Delete all items for a specific invoice
    void deleteByInvoiceId(UUID invoiceId);

    // Find items by HSN/SAC code
    List<InvoiceItems> findByHsnSacCode(String hsnSacCode);

    // Count items for an invoice
    @Query("SELECT COUNT(ii) FROM InvoiceItems ii WHERE ii.invoice.id = :invoiceId")
    Long countByInvoiceId(@Param("invoiceId") UUID invoiceId);
}
