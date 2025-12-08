import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import {
  Product,
  ProductRequest,
  ProductSearchRequest,
  PageResponse
} from '../models/product.model';
import { ApiService } from './api.service';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private readonly baseUrl = `${environment.apiUrl}/products`;

  constructor(
    private http: HttpClient,
    private apiService: ApiService
  ) {}

  // Search products with filters and pagination (company-aware)
  searchProducts(request: ProductSearchRequest): Observable<PageResponse<Product>> {
    return this.apiService.post<PageResponse<Product>>('/products/search', request);
  }

  // Get product by ID
  getProductById(id: string): Observable<Product> {
    return this.apiService.get<Product>(`/products/${id}`);
  }

  // Get all products for selected company
  getAllProducts(): Observable<Product[]> {
    return this.apiService.get<Product[]>('/products');
  }

  // Get product by product code (company-aware)
  getProductByCode(productCode: string): Observable<Product> {
    const params = new HttpParams().set('productCode', productCode);
    return this.apiService.get<Product>('/products/by-code', params);
  }

  // Get products by HSN/SAC code (company-aware)
  getProductsByHsnSacCode(hsnSacCode: string): Observable<Product[]> {
    const params = new HttpParams().set('hsnSacCode', hsnSacCode);
    return this.apiService.get<Product[]>('/products/by-hsn', params);
  }

  // Get products by category (company-aware)
  getProductsByCategory(category: string): Observable<Product[]> {
    const params = new HttpParams().set('category', category);
    return this.apiService.get<Product[]>('/products/by-category', params);
  }

  // Get low stock products (company-aware)
  getLowStockProducts(): Observable<Product[]> {
    return this.apiService.get<Product[]>('/products/low-stock');
  }

  // Create new product (company-aware)
  createProduct(request: ProductRequest): Observable<Product> {
    return this.apiService.post<Product>('/products', request);
  }

  // Update product (company-aware)
  updateProduct(id: string, request: ProductRequest): Observable<Product> {
    return this.apiService.put<Product>(`/products/${id}`, request);
  }

  // Update product status (company-aware)
  updateProductStatus(id: string, isActive: boolean): Observable<Product> {
    const params = new HttpParams().set('isActive', isActive.toString());
    return this.apiService.get<Product>(`/products/${id}/status`, params);
  }

  // Delete product (company-aware)
  deleteProduct(id: string): Observable<void> {
    return this.apiService.delete<void>(`/products/${id}`);
  }



  // Get active products only
  getActiveProducts(): Observable<Product[]> {
    const request: ProductSearchRequest = {
      page: 0,
      size: 1000,
      sortBy: 'productName',
      sortDirection: 'ASC',
      filterBy: {
        isActive: true
      }
    };
    return this.searchProducts(request).pipe(
      map(response => response.content)
    );
  }
}
