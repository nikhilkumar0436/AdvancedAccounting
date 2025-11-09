export interface Invoice {
  id?: string;
  companyId: string;
  customerId: string;
  invoiceNumber: string;
  invoiceDate: string;
  invoiceType: InvoiceType;
  financialYear: string;
  placeOfSupply: string;
  placeOfSupplyStateCode: string;
  isInterState?: boolean;
  reverseChargeApplicable?: boolean;
  totalTaxableAmount: number;
  totalCgstAmount?: number;
  totalSgstAmount?: number;
  totalIgstAmount?: number;
  totalCessAmount?: number;
  totalAmount: number;
  roundOffAmount?: number;
  grandTotal: number;
  amountInWords?: string;
  notes?: string;
  termsAndConditions?: string;
  dueDate?: string;
  paymentType: PaymentType;
  paymentTerms?: string;
  createdAt?: string;
  updatedAt?: string;
  invoiceItems: InvoiceItem[];
}

export interface InvoiceRequest {
  companyId?: string;
  customerId: string;
  invoiceNumber: string;
  invoiceDate: string;
  invoiceType: InvoiceType;
  financialYear: string;
  placeOfSupply: string;
  placeOfSupplyStateCode: string;
  isInterState?: boolean;
  reverseChargeApplicable?: boolean;
  totalTaxableAmount: number;
  totalDiscountAmount?: number;
  cgstAmount?: number;
  sgstAmount?: number;
  igstAmount?: number;
  cessAmount?: number;
  roundOff?: number;
  totalInvoiceAmount: number;
  paymentType: PaymentType;
  dueDate?: string | null;
  paidAmount?: number;
  balanceAmount?: number;
  notes?: string | null;
  termsConditions?: string | null; // Backend expects 'termsConditions', not 'termsAndConditions'
  transportDetails?: string | null;
  vehicleNumber?: string | null;
  ewayBillNumber?: string | null;
  currency?: string;
  exchangeRate?: number;
  baseCurrencyAmount?: number;
  einvoiceIrn?: string | null;
  einvoiceAckNo?: string | null;
  approvalStatus?: string;
  isRecurring?: boolean;
  recurringFrequency?: string | null;
  recurringParentId?: string | null;
  nextInvoiceDate?: string | null;
  salesPersonId?: string | null;
  salesChannel?: string | null;
  orderReference?: string | null;
  shippingAddress?: string | null;
  shippingCost?: number;
  shippingTrackingNumber?: string | null;
  expectedDeliveryDate?: string | null;
  customFields?: string | null;
  invoiceItems: InvoiceItemRequest[];
}

export interface InvoiceResponse {
  id: string;
  company: CompanyInfo;
  customer: CustomerInfo;
  invoiceNumber: string;
  invoiceDate: string;
  invoiceType: InvoiceType;
  financialYear: string;
  placeOfSupply: string;
  placeOfSupplyStateCode: string;
  isInterState: boolean;
  reverseChargeApplicable: boolean;
  totalTaxableAmount: number;
  totalDiscountAmount?: number;
  cgstAmount: number;
  sgstAmount: number;
  igstAmount: number;
  cessAmount: number;
  roundOff?: number;
  totalInvoiceAmount: number;
  paymentType: PaymentType;
  dueDate?: string;
  paidAmount?: number;
  balanceAmount?: number;
  notes?: string;
  termsConditions?: string;
  transportDetails?: string;
  vehicleNumber?: string;
  ewayBillNumber?: string;
  currency?: string;
  exchangeRate?: number;
  baseCurrencyAmount?: number;
  einvoiceIrn?: string;
  einvoiceAckNo?: string;
  einvoiceAckDate?: string;
  einvoiceQrCode?: string;
  approvalStatus?: string;
  approvedBy?: string;
  approvedAt?: string;
  rejectionReason?: string;
  isRecurring?: boolean;
  recurringFrequency?: string;
  recurringParentId?: string;
  nextInvoiceDate?: string;
  pdfUrl?: string;
  attachments?: string;
  salesPersonId?: string;
  salesChannel?: string;
  orderReference?: string;
  shippingAddress?: string;
  shippingCost?: number;
  shippingTrackingNumber?: string;
  expectedDeliveryDate?: string;
  customFields?: string;
  isCancelled?: boolean;
  cancelledAt?: string;
  cancelledBy?: string;
  cancellationReason?: string;
  createdBy: string;
  updatedBy: string;
  createdAt: string;
  updatedAt: string;
  invoiceItems: InvoiceItemResponse[];
}

export interface InvoiceItem {
  id?: string;
  productId: string;
  description: string;
  hsnCode?: string;
  quantity: number;
  unit: string;
  rate: number;
  amount: number;
  discountPercentage?: number;
  discountAmount?: number;
  taxableAmount: number;
  cgstRate?: number;
  cgstAmount?: number;
  sgstRate?: number;
  sgstAmount?: number;
  igstRate?: number;
  igstAmount?: number;
  cessRate?: number;
  cessAmount?: number;
  totalAmount: number;
}

export interface InvoiceItemRequest {
  productId: string;
  itemDescription: string; // Backend expects 'itemDescription', not 'description'
  hsnCode?: string | null;
  sacCode?: string | null;
  quantity: number;
  unit: string;
  rate: number;
  discountAmount?: number;
  taxableAmount: number;
  gstRate?: number;
  cgstAmount?: number;
  sgstAmount?: number;
  igstAmount?: number;
  cessRate?: number;
  cessAmount?: number;
  totalAmount: number;
}

export interface InvoiceItemResponse {
  id: string;
  productId: string;
  itemDescription: string;
  hsnCode?: string;
  sacCode?: string;
  quantity: number;
  unit: string;
  rate: number;
  discountAmount: number;
  taxableAmount: number;
  gstRate?: number;
   cgstRate?: number;
    sgstRate?: number;
    igstRate?: number;
  cgstAmount: number;
  sgstAmount: number;
  igstAmount: number;
  cessRate: number;
  cessAmount: number;
  totalAmount: number;
  product?: ProductInfo;
}

export interface CompanyInfo {
  id: string;
  companyName: string;
  gstin?: string;
  pan?: string;
  addressLine1?: string;
  addressLine2?: string;
  city?: string;
  state?: string;
  pincode?: string;
  country?: string;
  phoneNumber?: string;
  email?: string;
}

export interface CustomerInfo {
  id: string;
  customerName: string;
  gstin?: string;
  pan?: string;
  addressLine1?: string;
  addressLine2?: string;
  city?: string;
  state?: string;
  pincode?: string;
  country?: string;
  phoneNumber?: string;
  email?: string;
}

export interface ProductInfo {
  id: string;
  productName: string;
  productCode?: string;
  hsnCode?: string;
  unit: string;
  rate: number;
}

export interface InvoiceFilterRequest {
  companyId?: string;
  customerId?: string;
  invoiceType?: InvoiceType;
  paymentType?: PaymentType;
  startDate?: string;
  endDate?: string;
  minAmount?: number;
  maxAmount?: number;
  financialYear?: string;
  placeOfSupply?: string;
  isInterState?: boolean;
  reverseChargeApplicable?: boolean;
}

export enum InvoiceType {
  TAX_INVOICE = 'TAX_INVOICE',
  CREDIT_NOTE = 'CREDIT_NOTE',
  DEBIT_NOTE = 'DEBIT_NOTE',
  BILL_OF_SUPPLY = 'BILL_OF_SUPPLY',
  EXPORT_INVOICE = 'EXPORT_INVOICE',
  IMPORT_INVOICE = 'IMPORT_INVOICE'
}

export enum PaymentType {
  CASH = 'CASH',
  CREDIT = 'CREDIT',
  CHEQUE = 'CHEQUE',
  NEFT = 'NEFT',
  RTGS = 'RTGS',
  UPI = 'UPI',
  CARD = 'CARD'
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  numberOfElements: number;
}
