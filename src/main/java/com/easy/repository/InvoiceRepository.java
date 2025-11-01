package com.easy.repository;

import com.easy.entity.InvoiceMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<InvoiceMaster, UUID>, JpaSpecificationExecutor<InvoiceMaster> {

    // Find all invoices with specification support for complex filtering
    Page<InvoiceMaster> findAll(Specification<InvoiceMaster> specification, Pageable pageable);

    // Find by invoice number
    Optional<InvoiceMaster> findByInvoiceNumber(String invoiceNumber);

    // Find by company
    @Query("SELECT i FROM InvoiceMaster i WHERE i.company.id = :companyId")
    List<InvoiceMaster> findByCompanyId(@Param("companyId") UUID companyId);

    @Query("SELECT i FROM InvoiceMaster i WHERE i.company.id = :companyId ORDER BY i.invoiceDate DESC")
    Page<InvoiceMaster> findByCompanyIdOrderByInvoiceDateDesc(@Param("companyId") UUID companyId, Pageable pageable);

    // Find by customer
    @Query("SELECT i FROM InvoiceMaster i WHERE i.customer.id = :customerId")
    List<InvoiceMaster> findByCustomerId(@Param("customerId") UUID customerId);

    @Query("SELECT i FROM InvoiceMaster i WHERE i.customer.id = :customerId ORDER BY i.invoiceDate DESC")
    Page<InvoiceMaster> findByCustomerIdOrderByInvoiceDateDesc(@Param("customerId") UUID customerId, Pageable pageable);

    // Find by date range
    List<InvoiceMaster> findByInvoiceDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT i FROM InvoiceMaster i WHERE i.company.id = :companyId AND i.invoiceDate BETWEEN :startDate AND :endDate")
    List<InvoiceMaster> findByCompanyIdAndInvoiceDateBetween(@Param("companyId") UUID companyId,
                                                             @Param("startDate") LocalDate startDate,
                                                             @Param("endDate") LocalDate endDate);

    // Find by invoice type
    List<InvoiceMaster> findByInvoiceType(InvoiceMaster.InvoiceType invoiceType);

    @Query("SELECT i FROM InvoiceMaster i WHERE i.company.id = :companyId AND i.invoiceType = :invoiceType")
    List<InvoiceMaster> findByCompanyIdAndInvoiceType(@Param("companyId") UUID companyId,
                                                      @Param("invoiceType") InvoiceMaster.InvoiceType invoiceType);

    // Find by financial year
    List<InvoiceMaster> findByFinancialYear(String financialYear);

    @Query("SELECT i FROM InvoiceMaster i WHERE i.company.id = :companyId AND i.financialYear = :financialYear")
    List<InvoiceMaster> findByCompanyIdAndFinancialYear(@Param("companyId") UUID companyId,
                                                        @Param("financialYear") String financialYear);

    // Find by payment type
    List<InvoiceMaster> findByPaymentType(InvoiceMaster.PaymentType paymentType);

    // Find by amount range
    List<InvoiceMaster> findByTotalInvoiceAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);

    // Find pending/overdue invoices
    @Query("SELECT i FROM InvoiceMaster i WHERE i.paymentType = 'CREDIT' AND i.balanceAmount > 0 AND i.dueDate < :currentDate")
    List<InvoiceMaster> findOverdueInvoices(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT i FROM InvoiceMaster i WHERE i.company.id = :companyId AND i.paymentType = 'CREDIT' AND i.balanceAmount > 0 AND i.dueDate < :currentDate")
    List<InvoiceMaster> findOverdueInvoicesByCompany(@Param("companyId") UUID companyId,
                                                     @Param("currentDate") LocalDate currentDate);

    // Find unpaid invoices
    @Query("SELECT i FROM InvoiceMaster i WHERE i.paymentType = 'CREDIT' AND i.balanceAmount > 0")
    List<InvoiceMaster> findUnpaidInvoices();

    @Query("SELECT i FROM InvoiceMaster i WHERE i.company.id = :companyId AND i.paymentType = 'CREDIT' AND i.balanceAmount > 0")
    List<InvoiceMaster> findUnpaidInvoicesByCompany(@Param("companyId") UUID companyId);

    // Find by approval status
    List<InvoiceMaster> findByApprovalStatus(String approvalStatus);

    // Find cancelled invoices
    List<InvoiceMaster> findByIsCancelled(Boolean isCancelled);

    @Query("SELECT i FROM InvoiceMaster i WHERE i.company.id = :companyId AND i.isCancelled = :isCancelled")
    List<InvoiceMaster> findByCompanyIdAndIsCancelled(@Param("companyId") UUID companyId,
                                                      @Param("isCancelled") Boolean isCancelled);

    // Statistics queries
    @Query("SELECT COUNT(i) FROM InvoiceMaster i WHERE i.company.id = :companyId AND i.invoiceDate BETWEEN :startDate AND :endDate")
    Long countInvoicesByCompanyAndDateRange(@Param("companyId") UUID companyId,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(i.totalInvoiceAmount) FROM InvoiceMaster i WHERE i.company.id = :companyId AND i.invoiceDate BETWEEN :startDate AND :endDate")
    BigDecimal sumInvoiceAmountByCompanyAndDateRange(@Param("companyId") UUID companyId,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(i.balanceAmount) FROM InvoiceMaster i WHERE i.company.id = :companyId AND i.paymentType = 'CREDIT' AND i.balanceAmount > 0")
    BigDecimal sumOutstandingAmountByCompany(@Param("companyId") UUID companyId);

    // Find by e-invoice IRN
    Optional<InvoiceMaster> findByEinvoiceIrn(String einvoiceIrn);

    // Find recurring invoices
    List<InvoiceMaster> findByIsRecurring(Boolean isRecurring);

    @Query("SELECT i FROM InvoiceMaster i WHERE i.isRecurring = true AND i.nextInvoiceDate <= :currentDate")
    List<InvoiceMaster> findDueRecurringInvoices(@Param("currentDate") LocalDate currentDate);
}
