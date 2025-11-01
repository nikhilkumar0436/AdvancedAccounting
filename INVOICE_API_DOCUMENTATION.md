# Invoice Management APIs with Comprehensive Invoice Items Support

This document describes the comprehensive Invoice Management APIs for the Advanced Accounting System. The APIs provide full CRUD operations and advanced features for managing invoices **with complete invoice items support**.

## Key Enhancement: Invoice Items Management

**The invoice system now properly handles invoice items when creating and updating invoices:**

- ✅ **Invoice items are automatically created** when an invoice is created
- ✅ **Bidirectional relationships** are properly maintained
- ✅ **Multiple items per invoice** are fully supported
- ✅ **Item-level GST calculations** are handled
- ✅ **HSN/SAC codes** for products and services
- ✅ **Cascade operations** ensure items are saved/updated with invoices
- ✅ **Validation** ensures invoices must contain at least one item

## Components Created

### 1. InvoiceRepository
**File:** `src/main/java/com/easy/repository/InvoiceRepository.java`

JPA Repository interface extending `JpaRepository` and `JpaSpecificationExecutor` for InvoiceMaster entity.

### 2. InvoiceItemsRepository ⭐ NEW
**File:** `src/main/java/com/easy/repository/InvoiceItemsRepository.java`

Dedicated repository for managing invoice items with methods for:
- Finding items by invoice
- Finding items by product
- Item sequence management
- HSN/SAC code queries

### 3. Enhanced DTOs with Invoice Items Support

#### InvoiceRequest
**File:** `src/main/java/com/easy/dto/InvoiceRequest.java`

Request DTO now includes:
- **Nested InvoiceItemRequest** for line items with full validation
- Item-level GST calculations (CGST, SGST, IGST, Cess)
- HSN/SAC code support
- Quantity, rate, discount handling
- Product linking

#### InvoiceResponse  
**File:** `src/main/java/com/easy/dto/InvoiceResponse.java`

Response DTO includes:
- **Complete invoice items** with product details
- Item-level tax breakdowns
- Sequence management
- Nested product information

### 4. Enhanced InvoiceMapper
**File:** `src/main/java/com/easy/dto/InvoiceMapper.java`

Mapper handles:
- **Bidirectional relationship management** between invoices and items
- Item sequence assignment
- Request-to-entity mapping for items
- Entity-to-response mapping with complete item details

### 5. Enhanced InvoiceService
**File:** `src/main/java/com/easy/service/InvoiceService.java`

Service layer now includes:
- **Invoice items validation** (ensures at least one item)
- **Proper cascade handling** for item creation/updates
- **Bidirectional relationship** management
- Enhanced logging for items tracking

### 6. Enhanced InvoiceController
**File:** `src/main/java/com/easy/controller/InvoiceController.java`

Added endpoint:
- `GET /api/invoices/{id}/items` - Get invoice items separately

### 7. InvoiceItemsExamples ⭐ NEW
**File:** `src/main/java/com/easy/example/InvoiceItemsExamples.java`

Comprehensive examples demonstrating:
- Multi-item invoice creation
- Inter-state invoices with IGST
- Credit notes with returned items
- Proper GST calculations

## Enhanced API Examples with Invoice Items

### Create Invoice with Multiple Items
```http
POST /api/invoices
Content-Type: application/json

{
  "companyId": "uuid",
  "customerId": "uuid",
  "invoiceNumber": "INV-2024-0001",
  "invoiceDate": "2024-11-02",
  "invoiceType": "TAX_INVOICE",
  "financialYear": "2024-2025",
  "placeOfSupply": "Maharashtra",
  "placeOfSupplyStateCode": "27",
  "isInterState": false,
  "totalTaxableAmount": 107000.00,
  "totalDiscountAmount": 5500.00,
  "cgstAmount": 9630.00,
  "sgstAmount": 9630.00,
  "totalInvoiceAmount": 126260.00,
  "paymentType": "CREDIT",
  "dueDate": "2024-12-02",
  "invoiceItems": [
    {
      "productId": "uuid",
      "itemDescription": "Laptop Computer - Dell Inspiron 15",
      "quantity": 2,
      "unit": "PCS",
      "rate": 50000.00,
      "discountAmount": 5000.00,
      "taxableAmount": 95000.00,
      "gstRate": 18.00,
      "cgstAmount": 8550.00,
      "sgstAmount": 8550.00,
      "totalAmount": 112100.00,
      "hsnCode": "84713000"
    },
    {
      "productId": "uuid",
      "itemDescription": "Wireless Computer Mouse",
      "quantity": 5,
      "unit": "PCS",
      "rate": 1500.00,
      "discountAmount": 500.00,
      "taxableAmount": 7000.00,
      "gstRate": 18.00,
      "cgstAmount": 630.00,
      "sgstAmount": 630.00,
      "totalAmount": 8260.00,
      "hsnCode": "84716060"
    },
    {
      "itemDescription": "Software Installation Service",
      "quantity": 1,
      "unit": "SERVICE",
      "rate": 5000.00,
      "taxableAmount": 5000.00,
      "gstRate": 18.00,
      "cgstAmount": 450.00,
      "sgstAmount": 450.00,
      "totalAmount": 5900.00,
      "sacCode": "998314"
    }
  ]
}
```

### Get Invoice Items Separately
```http
GET /api/invoices/{invoiceId}/items
```

**Response:**
```json
[
  {
    "id": "uuid",
    "productId": "uuid",
    "itemDescription": "Laptop Computer - Dell Inspiron 15",
    "quantity": 2,
    "unit": "PCS",
    "rate": 50000.00,
    "discountAmount": 5000.00,
    "taxableAmount": 95000.00,
    "gstRate": 18.00,
    "cgstAmount": 8550.00,
    "sgstAmount": 8550.00,
    "totalAmount": 112100.00,
    "hsnCode": "84713000",
    "product": {
      "id": "uuid",
      "productName": "Dell Inspiron 15",
      "productCode": "DELL-INS-15",
      "hsnCode": "84713000"
    }
  }
]
```

## Invoice Items Features

### 1. **Multiple Items per Invoice**
- Support for unlimited line items
- Proper sequence management
- Individual item calculations

### 2. **GST Compliance at Item Level**
- Item-wise HSN/SAC codes
- CGST/SGST for intra-state items
- IGST for inter-state items
- Cess calculations per item

### 3. **Product Integration**
- Link items to product master
- Inherit product details (HSN, rates)
- Product information in responses

### 4. **Comprehensive Item Details**
- Description, quantity, unit, rate
- Discount handling per item
- Tax calculations per item
- Total amount per item

### 5. **Validation and Business Rules**
- **Mandatory items**: Invoices must have at least one item
- **Positive quantities**: Validation for regular invoices
- **Negative quantities**: Supported for credit notes
- **Tax calculation**: Automatic validation

### 6. **Advanced Item Operations**
- **Batch updates**: All items updated together with invoice
- **Orphan removal**: Automatic cleanup of unused items
- **Sequence management**: Proper ordering of items
- **Cascade operations**: Items saved/updated with invoice

## Real-World Usage Examples

### 1. **Multi-Product Sales Invoice**
```java
// Create invoice with laptop, accessories, and services
InvoiceResponse invoice = invoiceService.createInvoice(request);
// All 3 items are automatically created and linked
```

### 2. **Inter-State B2B Invoice**
```java
// Server hardware with IGST
// No CGST/SGST for inter-state transactions
```

### 3. **Credit Note for Returns**
```java
// Negative quantities and amounts
// Proper reversal of GST
```

### 4. **Service Invoice with SAC Codes**
```java
// Professional services with SAC codes
// Different GST rates possible
```

## Technical Implementation Details

### **Bidirectional Relationship Management**
```java
// Automatic relationship setting
if (invoice.getInvoiceItems() != null) {
    invoice.getInvoiceItems().forEach(item -> item.setInvoice(invoice));
}
```

### **Validation Rules**
```java
// Ensure invoice has items
if (request.getInvoiceItems() == null || request.getInvoiceItems().isEmpty()) {
    throw new IllegalArgumentException("Invoice must contain at least one item");
}
```

### **Cascade Operations**
```java
// Items are automatically saved/updated with invoice
@OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, 
           fetch = FetchType.LAZY, orphanRemoval = true)
private List<InvoiceItems> invoiceItems;
```

This enhanced invoice management system now provides **complete invoice items support**, making it suitable for real-world business scenarios where invoices contain multiple line items with detailed GST calculations and product tracking.

#### Update Invoice
```http
PUT /api/invoices/{id}
Content-Type: application/json
```

#### Get Invoice by ID
```http
GET /api/invoices/{id}
```

#### Get Invoice by Number
```http
GET /api/invoices/number/{invoiceNumber}
```

#### Delete Invoice (Draft only)
```http
DELETE /api/invoices/{id}
```

### Listing and Filtering

#### Get All Invoices (Paginated)
```http
GET /api/invoices?page=0&size=20&sortBy=invoiceDate&sortDirection=DESC
```

#### Get Invoices by Company
```http
GET /api/invoices/company/{companyId}?page=0&size=20
```

#### Get Invoices by Customer
```http
GET /api/invoices/customer/{customerId}?page=0&size=20
```

#### Get Invoices by Date Range
```http
GET /api/invoices/date-range?startDate=2024-10-01&endDate=2024-11-02
```

#### Get Invoices by Company and Date Range
```http
GET /api/invoices/company/{companyId}/date-range?startDate=2024-10-01&endDate=2024-11-02
```

### Status-Based Queries

#### Get Overdue Invoices
```http
GET /api/invoices/overdue
GET /api/invoices/company/{companyId}/overdue
```

#### Get Unpaid Invoices
```http
GET /api/invoices/unpaid
GET /api/invoices/company/{companyId}/unpaid
```

### Workflow Operations

#### Approve Invoice
```http
PUT /api/invoices/{id}/approve
```

#### Reject Invoice
```http
PUT /api/invoices/{id}/reject?rejectionReason=Invalid data
```

#### Cancel Invoice
```http
PUT /api/invoices/{id}/cancel?cancellationReason=Customer request
```

### Advanced Search
```http
POST /api/invoices/search?page=0&size=20&sortBy=invoiceDate&sortDirection=DESC
Content-Type: application/json

{
  "companyId": "uuid",
  "customerId": "uuid",
  "invoiceType": "TAX_INVOICE",
  "paymentType": "CREDIT",
  "startDate": "2024-10-01",
  "endDate": "2024-11-02",
  "minAmount": 1000.00,
  "maxAmount": 50000.00,
  "approvalStatus": "APPROVED",
  "isCancelled": false,
  "financialYear": "2024-2025"
}
```

### Statistics

#### Get Invoice Count
```http
GET /api/invoices/statistics/count?companyId=uuid&startDate=2024-10-01&endDate=2024-11-02
```

#### Get Total Invoice Amount
```http
GET /api/invoices/statistics/total-amount?companyId=uuid&startDate=2024-10-01&endDate=2024-11-02
```

#### Get Outstanding Amount
```http
GET /api/invoices/statistics/outstanding-amount?companyId=uuid
```

### Reference Data

#### Get Invoice Types
```http
GET /api/invoices/invoice-types
```
Returns: `["TAX_INVOICE", "DEBIT_NOTE", "CREDIT_NOTE", "RECEIPT_VOUCHER", "PAYMENT_VOUCHER"]`

#### Get Payment Types
```http
GET /api/invoices/payment-types
```
Returns: `["CASH", "CREDIT", "DEBIT_CARD", "CREDIT_CARD", "NET_BANKING", "UPI", "CHEQUE", "DEMAND_DRAFT", "BANK_TRANSFER", "OTHER"]`

## Key Features

### 1. Comprehensive Invoice Types
- Tax Invoice
- Debit Note
- Credit Note
- Receipt Voucher
- Payment Voucher

### 2. GST Support
- CGST/SGST for intra-state transactions
- IGST for inter-state transactions
- Cess calculations
- Place of supply tracking

### 3. Payment Management
- Multiple payment types
- Due date tracking
- Outstanding amount calculation
- Payment status tracking

### 4. E-Invoice Ready
- IRN (Invoice Reference Number) support
- Acknowledgement tracking
- QR code storage
- Digital signature support

### 5. Advanced Features
- Recurring invoices
- Multi-currency support
- Approval workflow
- Document management
- Custom fields
- Shipping integration

### 6. Business Intelligence
- Statistical reporting
- Outstanding tracking
- Overdue monitoring
- Revenue analytics

### 7. Validation and Security
- Comprehensive input validation
- Business rule enforcement
- Audit trail
- Status-based access control

## Usage Examples

See `InvoiceExamples.java` for detailed examples including:
- Creating different types of invoices
- Inter-state vs intra-state invoices
- Credit notes and debit notes
- Advanced filtering
- Workflow operations
- Statistics retrieval

## Integration Notes

1. **Company and Customer Integration**: The service includes placeholders for company and customer repository integration
2. **User Context**: Audit fields require integration with security context for current user tracking
3. **File Storage**: PDF generation and attachment handling need integration with file storage service
4. **E-Invoice**: IRN generation requires integration with government e-invoice portal
5. **Notifications**: Approval workflow can be enhanced with notification service integration

This comprehensive invoice management system provides enterprise-grade functionality for handling all aspects of invoice lifecycle management in an accounting system.
