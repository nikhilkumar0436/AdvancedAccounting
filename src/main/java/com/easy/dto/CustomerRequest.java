package com.easy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import com.easy.entity.Customer.CustomerType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Schema(description = "Customer creation/update request")
public class CustomerRequest {

    @NotBlank(message = "Customer name is required")
    @Size(max = 255, message = "Customer name must not exceed 255 characters")
    @Schema(description = "Customer name", example = "Acme Corporation", required = true)
    private String customerName;

    @NotNull(message = "Company ID is required")
    @Schema(description = "Company ID", required = true)
    private UUID companyId;

    @Pattern(regexp = "^$|^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$",
             message = "Invalid GSTIN format")
    @Schema(description = "GST Identification Number (Optional)", example = "27AAECA3548E1ZA")
    private String gstin;

    @Schema(description = "Customer type", example = "REGISTERED")
    private CustomerType customerType = CustomerType.UNREGISTERED;

    @Pattern(regexp = "^$|^[A-Z]{5}[0-9]{4}[A-Z]{1}$",
             message = "Invalid PAN format")
    @Schema(description = "Permanent Account Number (Optional)", example = "AAECA3548E")
    private String pan;

    @Size(max = 500, message = "Address line 1 must not exceed 500 characters")
    @Schema(description = "Address line 1")
    private String addressLine1;

    @Size(max = 500, message = "Address line 2 must not exceed 500 characters")
    @Schema(description = "Address line 2")
    private String addressLine2;

    @Size(max = 100, message = "City must not exceed 100 characters")
    @Schema(description = "City")
    private String city;

    @Size(max = 50, message = "State must not exceed 50 characters")
    @Schema(description = "State")
    private String state;

    @Pattern(regexp = "^[0-9]{2}$", message = "State code must be 2 digits")
    @Schema(description = "State code", example = "27")
    private String stateCode;

    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Invalid pincode format")
    @Schema(description = "Pincode", example = "400001")
    private String pincode;

    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Schema(description = "Email address")
    private String email;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number format")
    @Schema(description = "Phone number")
    private String phone;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid mobile number format")
    @Schema(description = "Mobile number")
    private String mobile;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid WhatsApp number format")
    @Schema(description = "WhatsApp number")
    private String whatsappNumber;

    @DecimalMin(value = "0.0", message = "Credit limit must be non-negative")
    @Digits(integer = 13, fraction = 2, message = "Credit limit format is invalid")
    @Schema(description = "Credit limit", example = "100000.00")
    private BigDecimal creditLimit;

    @DecimalMin(value = "0.0", message = "Opening balance must be non-negative")
    @Digits(integer = 13, fraction = 2, message = "Opening balance format is invalid")
    @Schema(description = "Opening balance", example = "5000.00")
    private BigDecimal openingBalance;

    @Size(max = 255, message = "Contact person name must not exceed 255 characters")
    @Schema(description = "Contact person name")
    private String contactPersonName;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid contact person phone format")
    @Schema(description = "Contact person phone")
    private String contactPersonPhone;

    @Schema(description = "Notes")
    private String notes;

    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be 3 letter code")
    @Schema(description = "Preferred currency", example = "INR")
    private String preferredCurrency = "INR";

    @Size(max = 50, message = "Customer category must not exceed 50 characters")
    @Schema(description = "Customer category", example = "VIP")
    private String customerCategory;

    @Size(max = 50, message = "Customer segment must not exceed 50 characters")
    @Schema(description = "Customer segment", example = "WHOLESALE")
    private String customerSegment;

    @Schema(description = "Tags for categorization")
    private List<String> tags;

    @Size(max = 100, message = "Payment terms must not exceed 100 characters")
    @Schema(description = "Payment terms", example = "NET_30")
    private String paymentTerms;

    @Min(value = 0, message = "Credit days must be non-negative")
    @Schema(description = "Credit days", example = "30")
    private Integer creditDays = 0;

    @DecimalMin(value = "0.0", message = "Discount percentage must be non-negative")
    @DecimalMax(value = "100.0", message = "Discount percentage cannot exceed 100")
    @Digits(integer = 3, fraction = 2, message = "Discount percentage format is invalid")
    @Schema(description = "Discount percentage", example = "5.00")
    private BigDecimal discountPercentage;

    @Size(max = 100, message = "External customer ID must not exceed 100 characters")
    @Schema(description = "External customer ID")
    private String externalCustomerId;

    @Size(max = 50, message = "Source must not exceed 50 characters")
    @Schema(description = "Customer source", example = "WEBSITE")
    private String source;

    @Schema(description = "Is billing address same as shipping", example = "true")
    private Boolean billingAddressSameAsShipping = true;

    @Size(max = 500, message = "Shipping address line 1 must not exceed 500 characters")
    @Schema(description = "Shipping address line 1")
    private String shippingAddressLine1;

    @Size(max = 500, message = "Shipping address line 2 must not exceed 500 characters")
    @Schema(description = "Shipping address line 2")
    private String shippingAddressLine2;

    @Size(max = 100, message = "Shipping city must not exceed 100 characters")
    @Schema(description = "Shipping city")
    private String shippingCity;

    @Size(max = 50, message = "Shipping state must not exceed 50 characters")
    @Schema(description = "Shipping state")
    private String shippingState;

    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Invalid shipping pincode format")
    @Schema(description = "Shipping pincode")
    private String shippingPincode;

    @Schema(description = "Custom fields in JSON format")
    private String customFields;

    @Schema(description = "Is cash customer", example = "false")
    private Boolean isCashCustomer = false;

    @Schema(description = "Is active", example = "true")
    private Boolean isActive = true;
}
