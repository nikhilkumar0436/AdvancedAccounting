export interface Company {
  id: string;
  companyName: string;
  gstin: string;
  pan: string;
  addressLine1?: string;
  addressLine2?: string;
  city?: string;
  state: string;
  stateCode: string;
  pincode?: string;
  isActive?: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface CompanyResponse extends Company {
  id: string;
  createdAt: string;
  updatedAt: string;
}
