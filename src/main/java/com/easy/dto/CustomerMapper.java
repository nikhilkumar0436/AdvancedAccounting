package com.easy.dto;

import org.springframework.beans.BeanUtils;
import com.easy.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public CustomerResponse toResponse(Customer customer) {
        if (customer == null) {
            return null;
        }

        CustomerResponse response = new CustomerResponse();
        BeanUtils.copyProperties(customer, response);

        // Handle nested objects
        if (customer.getCompany() != null) {
            response.setCompanyId(customer.getCompany().getId());
        }

        // Handle enums
        if (customer.getCustomerType() != null) {
            response.setCustomerType(customer.getCustomerType().toString());
        }

        return response;
    }

    public Customer toEntity(CustomerRequest request) {
        if (request == null) {
            return null;
        }

        Customer customer = new Customer();
        BeanUtils.copyProperties(request, customer);

        // Note: Company entity should be set separately in the service layer
        // based on the companyId from the request

        return customer;
    }

    public void updateEntity(CustomerRequest request, Customer customer) {
        if (request == null || customer == null) {
            return;
        }

        // Copy properties excluding ID and audit fields
        String[] ignoreProperties = {"id", "createdAt", "updatedAt", "createdBy", "updatedBy", "company"};
        BeanUtils.copyProperties(request, customer, ignoreProperties);

        // Note: Company entity should be updated separately in the service layer
        // based on the companyId from the request
    }
}
