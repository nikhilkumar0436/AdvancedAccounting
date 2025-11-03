export interface Product {
  id?: string;
  productName: string;
  productCode?: string;
  hsnSacCode: string;
  productType?: ProductType;
  unitOfMeasurement?: string;
  gstRate?: number;
  cessRate?: number;
  purchasePrice?: number;
  sellingPrice: number;
  mrp?: number;
  description?: string;
  category?: string;
  openingStock?: number;
  currentStock?: number;
  reorderLevel?: number;
  baseCurrency?: string;
  barcode?: string;
  sku?: string;
  batchTrackingEnabled?: boolean;
  serialTrackingEnabled?: boolean;
  expiryTrackingEnabled?: boolean;
  warrantyPeriodDays?: number;
  hasVariants?: boolean;
  parentProductId?: string;
  variantAttributes?: string;
  preferredSupplierId?: string;
  minimumOrderQuantity?: number;
  leadTimeDays?: number;
  wholesalePrice?: number;
  retailPrice?: number;
  discountApplicable?: boolean;
  maxDiscountPercentage?: number;
  productImageUrl?: string;
  productImages?: string;
  weight?: number;
  dimensions?: string;
  isPublishedOnline?: boolean;
  seoTitle?: string;
  seoDescription?: string;
  tags?: string;
  customFields?: string;
  isActive?: boolean;
  createdBy?: string;
  updatedBy?: string;
  createdAt?: string;
  updatedAt?: string;
  companyId?: string;
  companyName?: string;
  parentProductName?: string;
  profitMargin?: number;
  profitPercentage?: number;
  isLowStock?: boolean;
  stockValue?: number;
}

export interface ProductRequest {
  productName: string;
  companyId: string;
  productCode?: string;
  hsnSacCode: string;
  productType?: ProductType;
  unitOfMeasurement?: string;
  gstRate?: number;
  cessRate?: number;
  purchasePrice?: number;
  sellingPrice: number;
  mrp?: number;
  description?: string;
  category?: string;
  openingStock?: number;
  currentStock?: number;
  reorderLevel?: number;
  baseCurrency?: string;
  barcode?: string;
  sku?: string;
  batchTrackingEnabled?: boolean;
  serialTrackingEnabled?: boolean;
  expiryTrackingEnabled?: boolean;
  warrantyPeriodDays?: number;
  hasVariants?: boolean;
  parentProductId?: string;
  variantAttributes?: string;
  preferredSupplierId?: string;
  minimumOrderQuantity?: number;
  leadTimeDays?: number;
  wholesalePrice?: number;
  retailPrice?: number;
  discountApplicable?: boolean;
  maxDiscountPercentage?: number;
  productImageUrl?: string;
  productImages?: string;
  weight?: number;
  dimensions?: string;
  isPublishedOnline?: boolean;
  seoTitle?: string;
  seoDescription?: string;
  tags?: string;
  customFields?: string;
  isActive?: boolean;
  createdBy?: string;
  updatedBy?: string;
}

export interface ProductSearchRequest {
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: string;
  filterBy?: ProductFilter;
}

export interface ProductFilter {
  productName?: string;
  productCode?: string;
  hsnSacCode?: string;
  productType?: ProductType;
  category?: string;
  companyId?: string;
  sellingPriceFrom?: number;
  sellingPriceTo?: number;
  purchasePriceFrom?: number;
  purchasePriceTo?: number;
  currentStockFrom?: number;
  currentStockTo?: number;
  isLowStock?: boolean;
  isActive?: boolean;
  isPublishedOnline?: boolean;
  gstRateFrom?: number;
  gstRateTo?: number;
  batchTrackingEnabled?: boolean;
  serialTrackingEnabled?: boolean;
  expiryTrackingEnabled?: boolean;
  hasVariants?: boolean;
  parentProductId?: string;
  preferredSupplierId?: string;
  createdDateFrom?: string;
  createdDateTo?: string;
  updatedDateFrom?: string;
  updatedDateTo?: string;
  searchKeyword?: string;
  barcode?: string;
  sku?: string;
  tags?: string;
}

export enum ProductType {
  GOODS = 'GOODS',
  SERVICES = 'SERVICES'
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
