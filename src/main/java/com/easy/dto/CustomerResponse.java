package com.easy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Schema(description = "Customer response")
public class CustomerResponse {

    @Schema(description = "Customer ID")
    private UUID id;

    @Schema(description = "Customer name")
    private String customerName;

    @Schema(description = "Company ID")
    private UUID companyId;

    @Schema(description = "GST Identification Number")
    private String gstin;

    @Schema(description = "Customer type")
    private String customerType;

    @Schema(description = "Permanent Account Number")
    private String pan;

    @Schema(description = "Address line 1")
    private String addressLine1;

    @Schema(description = "Address line 2")
    private String addressLine2;

    @Schema(description = "City")
    private String city;

    @Schema(description = "State")
    private String state;

    @Schema(description = "State code")
    private String stateCode;

    @Schema(description = "Pincode")
    private String pincode;

    @Schema(description = "Email address")
    private String email;

    @Schema(description = "Phone number")
    private String phone;

    @Schema(description = "Mobile number")
    private String mobile;

    @Schema(description = "WhatsApp number")
    private String whatsappNumber;

    @Schema(description = "Credit limit")
    private BigDecimal creditLimit;

    @Schema(description = "Opening balance")
    private BigDecimal openingBalance;

    @Schema(description = "Outstanding balance")
    private BigDecimal outstandingBalance;

    @Schema(description = "Contact person name")
    private String contactPersonName;

    @Schema(description = "Contact person phone")
    private String contactPersonPhone;

    @Schema(description = "Is cash customer")
    private Boolean isCashCustomer;

    @Schema(description = "Is active")
    private Boolean isActive;

    @Schema(description = "Created date")
    private LocalDateTime createdAt;

    @Schema(description = "Updated date")
    private LocalDateTime updatedAt;

    @Schema(description = "Created by")
    private String createdBy;

    @Schema(description = "Updated by")
    private String updatedBy;
}
