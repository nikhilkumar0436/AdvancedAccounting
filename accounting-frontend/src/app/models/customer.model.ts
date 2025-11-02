export interface Customer {
  id?: string;
  customerName: string;
  gstin?: string;
  customerType?: CustomerType;
  pan?: string;
  addressLine1?: string;
  addressLine2?: string;
  city?: string;
  state?: string;
  stateCode?: string;
  pincode?: string;
  email?: string;
  phone?: string;
  mobile?: string;
  whatsappNumber?: string;
  creditLimit?: number;
  openingBalance?: number;
  outstandingBalance?: number;
  contactPersonName?: string;
  contactPersonPhone?: string;
  notes?: string;
  preferredCurrency?: string;
  customerCategory?: string;
  customerSegment?: string;
  tags?: string[];
  paymentTerms?: string;
  creditDays?: number;
  discountPercentage?: number;
  externalCustomerId?: string;
  source?: string;
  billingAddressSameAsShipping?: boolean;
  shippingAddressLine1?: string;
  shippingAddressLine2?: string;
  shippingCity?: string;
  shippingState?: string;
  shippingPincode?: string;
  isActive?: boolean;
  isCashCustomer?: boolean;
  createdBy?: string;
  updatedBy?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface CustomerRequest {
  customerName: string;
  companyId: string;
  gstin?: string;
  customerType?: CustomerType;
  pan?: string;
  addressLine1?: string;
  addressLine2?: string;
  city?: string;
  state?: string;
  pincode?: string;
  country?: string;
  phoneNumber?: string;
  email?: string;
  isActive?: boolean;
  creditLimit?: number;
  billingAddressLine1?: string;
  billingAddressLine2?: string;
  billingCity?: string;
  billingState?: string;
  billingPincode?: string;
  billingCountry?: string;
  shippingAddressLine1?: string;
  shippingAddressLine2?: string;
  shippingCity?: string;
  shippingState?: string;
  shippingPincode?: string;
  shippingCountry?: string;
  contactPersons?: ContactPerson[];
  isGstRegistered?: boolean;
  gstRegistrationDate?: string;
}

export interface CustomerResponse {
  id: string;
  customerName: string;
  companyId: string;
  gstin?: string;
  customerType: CustomerType;
  pan?: string;
  addressLine1?: string;
  addressLine2?: string;
  city?: string;
  state?: string;
  pincode?: string;
  country?: string;
  phoneNumber?: string;
  email?: string;
  isActive: boolean;
  creditLimit?: number;
  outstandingAmount?: number;
  billingAddressLine1?: string;
  billingAddressLine2?: string;
  billingCity?: string;
  billingState?: string;
  billingPincode?: string;
  billingCountry?: string;
  shippingAddressLine1?: string;
  shippingAddressLine2?: string;
  shippingCity?: string;
  shippingState?: string;
  shippingPincode?: string;
  shippingCountry?: string;
  contactPersons?: ContactPerson[];
  isGstRegistered?: boolean;
  gstRegistrationDate?: string;
  createdAt: string;
  updatedAt: string;
}

export interface ContactPerson {
  name: string;
  designation?: string;
  phoneNumber?: string;
  email?: string;
}

export interface CustomRequest {
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: string;
  filters?: CustomerFilter;
}

export interface CustomerFilter {
  customerName?: string;
  customerType?: CustomerType;
  gstin?: string;
  pan?: string;
  city?: string;
  state?: string;
  isActive?: boolean;
  isGstRegistered?: boolean;
}

export enum CustomerType {
  REGISTERED = 'REGISTERED',
  UNREGISTERED = 'UNREGISTERED',
  COMPOSITION = 'COMPOSITION',
  SEZ = 'SEZ',
  EXEMPT = 'EXEMPT'
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
