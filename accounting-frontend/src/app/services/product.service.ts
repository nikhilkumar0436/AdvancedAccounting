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
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private readonly baseUrl = `${environment.apiUrl}/products`;

  constructor(private http: HttpClient) {}

  // Search products with filters and pagination
  searchProducts(request: ProductSearchRequest): Observable<PageResponse<Product>> {
    return this.http.post<PageResponse<Product>>(`${this.baseUrl}/search`, request);
  }

  // Get product by ID
  getProductById(id: string): Observable<Product> {
    return this.http.get<Product>(`${this.baseUrl}/${id}`);
  }

  // Get products by company
  getProductsByCompany(companyId: string): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.baseUrl}/company/${companyId}`);
  }

  // Get product by product code
  getProductByCode(productCode: string): Observable<Product> {
    return this.http.get<Product>(`${this.baseUrl}/code/${productCode}`);
  }

  // Get products by HSN/SAC code
  getProductsByHsnSacCode(hsnSacCode: string): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.baseUrl}/hsn/${hsnSacCode}`);
  }

  // Get products by category
  getProductsByCategory(category: string): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.baseUrl}/category/${category}`);
  }

  // Get low stock products
  getLowStockProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.baseUrl}/low-stock`);
  }

  // Create new product
  createProduct(request: ProductRequest): Observable<Product> {
    return this.http.post<Product>(this.baseUrl, request);
  }

  // Update product
  updateProduct(id: string, request: ProductRequest): Observable<Product> {
    return this.http.put<Product>(`${this.baseUrl}/${id}`, request);
  }

  // Update product status
  updateProductStatus(id: string, isActive: boolean): Observable<Product> {
    const params = new HttpParams().set('isActive', isActive.toString());
    return this.http.patch<Product>(`${this.baseUrl}/${id}/status`, null, { params });
  }

  // Delete product
  deleteProduct(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  // Get all products (for dropdowns)
  getAllProducts(): Observable<Product[]> {
    const request: ProductSearchRequest = {
      page: 0,
      size: 1000,
      sortBy: 'productName',
      sortDirection: 'ASC'
    };
    return this.searchProducts(request).pipe(
      map(response => response.content)
    );
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
