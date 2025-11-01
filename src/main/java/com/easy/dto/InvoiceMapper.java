package com.easy.dto;

import com.easy.entity.CompanyMaster;
import com.easy.entity.Customer;
import com.easy.entity.InvoiceItems;
import com.easy.entity.InvoiceMaster;
import com.easy.entity.ProductMaster;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InvoiceMapper {

    public InvoiceMaster toEntity(InvoiceRequest request) {
        if (request == null) {
            return null;
        }

        InvoiceMaster invoice = new InvoiceMaster();

        // Basic fields
        invoice.setInvoiceNumber(request.getInvoiceNumber());
        invoice.setInvoiceDate(request.getInvoiceDate());
        invoice.setInvoiceType(request.getInvoiceType());
        invoice.setFinancialYear(request.getFinancialYear());
        invoice.setPlaceOfSupply(request.getPlaceOfSupply());
        invoice.setPlaceOfSupplyStateCode(request.getPlaceOfSupplyStateCode());
        invoice.setIsInterState(request.getIsInterState());
        invoice.setReverseChargeApplicable(request.getReverseChargeApplicable());

        // Amounts
        invoice.setTotalTaxableAmount(request.getTotalTaxableAmount());
        invoice.setTotalDiscountAmount(request.getTotalDiscountAmount());
        invoice.setCgstAmount(request.getCgstAmount());
        invoice.setSgstAmount(request.getSgstAmount());
        invoice.setIgstAmount(request.getIgstAmount());
        invoice.setCessAmount(request.getCessAmount());
        invoice.setRoundOff(request.getRoundOff());
        invoice.setTotalInvoiceAmount(request.getTotalInvoiceAmount());

        // Payment info
        invoice.setPaymentType(request.getPaymentType());
        invoice.setDueDate(request.getDueDate());
        invoice.setPaidAmount(request.getPaidAmount());
        invoice.setBalanceAmount(request.getBalanceAmount());

        // Additional info
        invoice.setNotes(request.getNotes());
        invoice.setTermsConditions(request.getTermsConditions());
        invoice.setTransportDetails(request.getTransportDetails());
        invoice.setVehicleNumber(request.getVehicleNumber());
        invoice.setEwayBillNumber(request.getEwayBillNumber());

        // Currency support
        invoice.setCurrency(request.getCurrency());
        invoice.setExchangeRate(request.getExchangeRate());
        invoice.setBaseCurrencyAmount(request.getBaseCurrencyAmount());

        // E-invoice fields
        invoice.setEinvoiceIrn(request.getEinvoiceIrn());
        invoice.setEinvoiceAckNo(request.getEinvoiceAckNo());

        // Approval workflow
        invoice.setApprovalStatus(request.getApprovalStatus());

        // Recurring invoices
        invoice.setIsRecurring(request.getIsRecurring());
        invoice.setRecurringFrequency(request.getRecurringFrequency());
        invoice.setNextInvoiceDate(request.getNextInvoiceDate());

        // Sales and analytics
        invoice.setSalesPersonId(request.getSalesPersonId());
        invoice.setSalesChannel(request.getSalesChannel());
        invoice.setOrderReference(request.getOrderReference());

        // Shipping and logistics
        invoice.setShippingAddress(request.getShippingAddress());
        invoice.setShippingCost(request.getShippingCost());
        invoice.setShippingTrackingNumber(request.getShippingTrackingNumber());
        invoice.setExpectedDeliveryDate(request.getExpectedDeliveryDate());

        // Custom fields
        invoice.setCustomFields(request.getCustomFields());

        // Map invoice items
        if (request.getInvoiceItems() != null && !request.getInvoiceItems().isEmpty()) {
            List<InvoiceItems> invoiceItems = mapRequestItemsToEntities(request.getInvoiceItems(), invoice);
            invoice.setInvoiceItems(invoiceItems);
        }

        return invoice;
    }

    public InvoiceResponse toResponse(InvoiceMaster invoice) {
        if (invoice == null) {
            return null;
        }

        InvoiceResponse response = new InvoiceResponse();

        // Basic fields
        response.setId(invoice.getId());
        response.setInvoiceNumber(invoice.getInvoiceNumber());
        response.setInvoiceDate(invoice.getInvoiceDate());
        response.setInvoiceType(invoice.getInvoiceType());
        response.setFinancialYear(invoice.getFinancialYear());
        response.setPlaceOfSupply(invoice.getPlaceOfSupply());
        response.setPlaceOfSupplyStateCode(invoice.getPlaceOfSupplyStateCode());
        response.setIsInterState(invoice.getIsInterState());
        response.setReverseChargeApplicable(invoice.getReverseChargeApplicable());

        // Company and Customer info
        response.setCompany(mapCompanyInfo(invoice.getCompany()));
        response.setCustomer(mapCustomerInfo(invoice.getCustomer()));

        // Amounts
        response.setTotalTaxableAmount(invoice.getTotalTaxableAmount());
        response.setTotalDiscountAmount(invoice.getTotalDiscountAmount());
        response.setCgstAmount(invoice.getCgstAmount());
        response.setSgstAmount(invoice.getSgstAmount());
        response.setIgstAmount(invoice.getIgstAmount());
        response.setCessAmount(invoice.getCessAmount());
        response.setRoundOff(invoice.getRoundOff());
        response.setTotalInvoiceAmount(invoice.getTotalInvoiceAmount());

        // Payment info
        response.setPaymentType(invoice.getPaymentType());
        response.setDueDate(invoice.getDueDate());
        response.setPaidAmount(invoice.getPaidAmount());
        response.setBalanceAmount(invoice.getBalanceAmount());

        // Additional info
        response.setNotes(invoice.getNotes());
        response.setTermsConditions(invoice.getTermsConditions());
        response.setTransportDetails(invoice.getTransportDetails());
        response.setVehicleNumber(invoice.getVehicleNumber());
        response.setEwayBillNumber(invoice.getEwayBillNumber());

        // Currency support
        response.setCurrency(invoice.getCurrency());
        response.setExchangeRate(invoice.getExchangeRate());
        response.setBaseCurrencyAmount(invoice.getBaseCurrencyAmount());

        // E-invoice fields
        response.setEinvoiceIrn(invoice.getEinvoiceIrn());
        response.setEinvoiceAckNo(invoice.getEinvoiceAckNo());
        response.setEinvoiceAckDate(invoice.getEinvoiceAckDate());
        response.setEinvoiceQrCode(invoice.getEinvoiceQrCode());

        // Approval workflow
        response.setApprovalStatus(invoice.getApprovalStatus());
        response.setApprovedBy(invoice.getApprovedBy());
        response.setApprovedAt(invoice.getApprovedAt());
        response.setRejectionReason(invoice.getRejectionReason());

        // Recurring invoices
        response.setIsRecurring(invoice.getIsRecurring());
        response.setRecurringFrequency(invoice.getRecurringFrequency());
        response.setRecurringParentId(invoice.getRecurringParent() != null ?
                                      invoice.getRecurringParent().getId() : null);
        response.setNextInvoiceDate(invoice.getNextInvoiceDate());

        // Document management
        response.setPdfUrl(invoice.getPdfUrl());
        response.setAttachments(invoice.getAttachments());

        // Sales and analytics
        response.setSalesPersonId(invoice.getSalesPersonId());
        response.setSalesChannel(invoice.getSalesChannel());
        response.setOrderReference(invoice.getOrderReference());

        // Shipping and logistics
        response.setShippingAddress(invoice.getShippingAddress());
        response.setShippingCost(invoice.getShippingCost());
        response.setShippingTrackingNumber(invoice.getShippingTrackingNumber());
        response.setExpectedDeliveryDate(invoice.getExpectedDeliveryDate());

        // Custom fields
        response.setCustomFields(invoice.getCustomFields());

        // Status
        response.setIsCancelled(invoice.getIsCancelled());
        response.setCancelledAt(invoice.getCancelledAt());
        response.setCancelledBy(invoice.getCancelledBy());
        response.setCancellationReason(invoice.getCancellationReason());

        // Audit fields
        response.setCreatedBy(invoice.getCreatedBy());
        response.setUpdatedBy(invoice.getUpdatedBy());


        // Invoice Items
        response.setInvoiceItems(mapInvoiceItems(invoice.getInvoiceItems()));

        return response;
    }

    public List<InvoiceResponse> toResponseList(List<InvoiceMaster> invoices) {
        if (invoices == null || invoices.isEmpty()) {
            return Collections.emptyList();
        }
        return invoices.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private InvoiceResponse.CompanyInfo mapCompanyInfo(CompanyMaster company) {
        if (company == null) {
            return null;
        }

        InvoiceResponse.CompanyInfo companyInfo = new InvoiceResponse.CompanyInfo();
        companyInfo.setId(company.getId());
        companyInfo.setCompanyName(company.getCompanyName());
        companyInfo.setGstin(company.getGstin());
     //   companyInfo.setAddress(company.getAddress());
        companyInfo.setCity(company.getCity());
        companyInfo.setState(company.getState());
        companyInfo.setPincode(company.getPincode());
        companyInfo.setPhone(company.getPhone());
        companyInfo.setEmail(company.getEmail());

        return companyInfo;
    }

    private InvoiceResponse.CustomerInfo mapCustomerInfo(Customer customer) {
        if (customer == null) {
            return null;
        }

        InvoiceResponse.CustomerInfo customerInfo = new InvoiceResponse.CustomerInfo();
        customerInfo.setId(customer.getId());
        customerInfo.setCustomerName(customer.getCustomerName());
        customerInfo.setGstin(customer.getGstin());
     //   customerInfo.setAddress(customer.getAddress());
        customerInfo.setCity(customer.getCity());
        customerInfo.setState(customer.getState());
        customerInfo.setPincode(customer.getPincode());
        customerInfo.setPhone(customer.getPhone());
        customerInfo.setMobile(customer.getMobile());
        customerInfo.setEmail(customer.getEmail());
        customerInfo.setCustomerType(customer.getCustomerType() != null ? customer.getCustomerType().toString() : null);

        return customerInfo;
    }

    private List<InvoiceResponse.InvoiceItemResponse> mapInvoiceItems(List<InvoiceItems> invoiceItems) {
        if (invoiceItems == null || invoiceItems.isEmpty()) {
            return Collections.emptyList();
        }

        return invoiceItems.stream()
                .map(this::mapInvoiceItem)
                .collect(Collectors.toList());
    }

    private InvoiceResponse.InvoiceItemResponse mapInvoiceItem(InvoiceItems item) {
        if (item == null) {
            return null;
        }

        InvoiceResponse.InvoiceItemResponse itemResponse = new InvoiceResponse.InvoiceItemResponse();
        itemResponse.setId(item.getId());
        itemResponse.setProductId(item.getProduct() != null ? item.getProduct().getId() : null);
        itemResponse.setItemDescription(item.getItemDescription());
        itemResponse.setQuantity(item.getQuantity());
        itemResponse.setUnit(item.getUnitOfMeasurement());
        itemResponse.setRate(item.getUnitPrice());
        itemResponse.setDiscountAmount(item.getDiscountAmount());
        itemResponse.setTaxableAmount(item.getTaxableAmount());
        itemResponse.setGstRate(item.getGstRate());
        itemResponse.setCgstAmount(item.getCgstAmount());
        itemResponse.setSgstAmount(item.getSgstAmount());
        itemResponse.setIgstAmount(item.getIgstAmount());
        itemResponse.setCessRate(item.getCessRate());
        itemResponse.setCessAmount(item.getCessAmount());
        itemResponse.setTotalAmount(item.getTotalAmount());
        itemResponse.setHsnCode(item.getHsnSacCode());
        itemResponse.setSacCode(item.getHsnSacCode()); // HSN/SAC code is stored in same field

        // Product info
        itemResponse.setProduct(mapProductInfo(item.getProduct()));

        return itemResponse;
    }

    private InvoiceResponse.ProductInfo mapProductInfo(ProductMaster product) {
        if (product == null) {
            return null;
        }

        InvoiceResponse.ProductInfo productInfo = new InvoiceResponse.ProductInfo();
        productInfo.setId(product.getId());
        productInfo.setProductName(product.getProductName());
        productInfo.setProductCode(product.getProductCode());
        productInfo.setHsnCode(product.getHsnSacCode());  // HSN/SAC code is stored in same field
        productInfo.setSacCode(product.getHsnSacCode());  // HSN/SAC code is stored in same field
        productInfo.setUnit(product.getUnitOfMeasurement());
        productInfo.setGstRate(product.getGstRate());

        return productInfo;
    }

    public void updateEntityFromRequest(InvoiceMaster invoice, InvoiceRequest request) {
        if (invoice == null || request == null) {
            return;
        }

        // Update basic fields
        invoice.setInvoiceNumber(request.getInvoiceNumber());
        invoice.setInvoiceDate(request.getInvoiceDate());
        invoice.setInvoiceType(request.getInvoiceType());
        invoice.setFinancialYear(request.getFinancialYear());
        invoice.setPlaceOfSupply(request.getPlaceOfSupply());
        invoice.setPlaceOfSupplyStateCode(request.getPlaceOfSupplyStateCode());
        invoice.setIsInterState(request.getIsInterState());
        invoice.setReverseChargeApplicable(request.getReverseChargeApplicable());

        // Update amounts
        invoice.setTotalTaxableAmount(request.getTotalTaxableAmount());
        invoice.setTotalDiscountAmount(request.getTotalDiscountAmount());
        invoice.setCgstAmount(request.getCgstAmount());
        invoice.setSgstAmount(request.getSgstAmount());
        invoice.setIgstAmount(request.getIgstAmount());
        invoice.setCessAmount(request.getCessAmount());
        invoice.setRoundOff(request.getRoundOff());
        invoice.setTotalInvoiceAmount(request.getTotalInvoiceAmount());

        // Update payment info
        invoice.setPaymentType(request.getPaymentType());
        invoice.setDueDate(request.getDueDate());
        invoice.setPaidAmount(request.getPaidAmount());
        invoice.setBalanceAmount(request.getBalanceAmount());

        // Update additional info
        invoice.setNotes(request.getNotes());
        invoice.setTermsConditions(request.getTermsConditions());
        invoice.setTransportDetails(request.getTransportDetails());
        invoice.setVehicleNumber(request.getVehicleNumber());
        invoice.setEwayBillNumber(request.getEwayBillNumber());

        // Update currency support
        invoice.setCurrency(request.getCurrency());
        invoice.setExchangeRate(request.getExchangeRate());
        invoice.setBaseCurrencyAmount(request.getBaseCurrencyAmount());

        // Update other fields
        invoice.setApprovalStatus(request.getApprovalStatus());
        invoice.setIsRecurring(request.getIsRecurring());
        invoice.setRecurringFrequency(request.getRecurringFrequency());
        invoice.setNextInvoiceDate(request.getNextInvoiceDate());
        invoice.setSalesPersonId(request.getSalesPersonId());
        invoice.setSalesChannel(request.getSalesChannel());
        invoice.setOrderReference(request.getOrderReference());
        invoice.setShippingAddress(request.getShippingAddress());
        invoice.setShippingCost(request.getShippingCost());
        invoice.setShippingTrackingNumber(request.getShippingTrackingNumber());
        invoice.setExpectedDeliveryDate(request.getExpectedDeliveryDate());
        invoice.setCustomFields(request.getCustomFields());

        // Update invoice items
        if (request.getInvoiceItems() != null) {
            // Clear existing items and add new ones
            if (invoice.getInvoiceItems() != null) {
                invoice.getInvoiceItems().clear();
            } else {
                invoice.setInvoiceItems(new ArrayList<>());
            }

            if (!request.getInvoiceItems().isEmpty()) {
                List<InvoiceItems> newItems = mapRequestItemsToEntities(request.getInvoiceItems(), invoice);
                invoice.getInvoiceItems().addAll(newItems);
            }
        }
    }

    /**
     * Maps InvoiceItemRequest list to InvoiceItems entities
     */
    private List<InvoiceItems> mapRequestItemsToEntities(List<InvoiceRequest.InvoiceItemRequest> itemRequests, InvoiceMaster invoice) {
        if (itemRequests == null || itemRequests.isEmpty()) {
            return new ArrayList<>();
        }

        List<InvoiceItems> invoiceItems = new ArrayList<>();

        for (int i = 0; i < itemRequests.size(); i++) {
            InvoiceRequest.InvoiceItemRequest itemRequest = itemRequests.get(i);
            InvoiceItems item = mapRequestItemToEntity(itemRequest, invoice, i + 1);
            invoiceItems.add(item);
        }

        return invoiceItems;
    }

    /**
     * Maps single InvoiceItemRequest to InvoiceItems entity
     */
    private InvoiceItems mapRequestItemToEntity(InvoiceRequest.InvoiceItemRequest itemRequest, InvoiceMaster invoice, int sequence) {
        InvoiceItems item = new InvoiceItems();

        // Set the invoice reference
        item.setInvoice(invoice);
        item.setItemSequence(sequence);

        // Map basic fields
        item.setItemDescription(itemRequest.getItemDescription());
        item.setQuantity(itemRequest.getQuantity());
        item.setUnitOfMeasurement(itemRequest.getUnit());
        item.setUnitPrice(itemRequest.getRate());
        item.setDiscountAmount(itemRequest.getDiscountAmount());
        item.setTaxableAmount(itemRequest.getTaxableAmount());
        item.setGstRate(itemRequest.getGstRate());
        item.setCgstAmount(itemRequest.getCgstAmount());
        item.setSgstAmount(itemRequest.getSgstAmount());
        item.setIgstAmount(itemRequest.getIgstAmount());
        item.setCessRate(itemRequest.getCessRate());
        item.setCessAmount(itemRequest.getCessAmount());
        item.setTotalAmount(itemRequest.getTotalAmount());

        // Handle HSN/SAC code
        if (itemRequest.getHsnCode() != null && !itemRequest.getHsnCode().trim().isEmpty()) {
            item.setHsnSacCode(itemRequest.getHsnCode());
        } else if (itemRequest.getSacCode() != null && !itemRequest.getSacCode().trim().isEmpty()) {
            item.setHsnSacCode(itemRequest.getSacCode());
        }

        return item;
    }
}
