import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import {
  Customer,
  CustomerRequest,
  CustomerResponse,
  CustomRequest,
  PageResponse
} from '../models/customer.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CustomerService {
  private readonly baseUrl = `${environment.apiUrl}/customers`;

  constructor(private http: HttpClient) {}

  // Search customers with filters and pagination
  searchCustomers(request: CustomRequest): Observable<Customer[]> {
    return this.http.post<Customer[]>(`${this.baseUrl}/search`, request);
  }

  // Get customer by ID
  getCustomerById(id: string): Observable<CustomerResponse> {
    return this.http.get<CustomerResponse>(`${this.baseUrl}/${id}`);
  }

  // Get customers by company
  getCustomersByCompany(companyId: string): Observable<CustomerResponse[]> {
    return this.http.get<CustomerResponse[]>(`${this.baseUrl}/company/${companyId}`);
  }

  // Get customer by GSTIN
  getCustomerByGstin(gstin: string): Observable<Customer> {
    return this.http.get<Customer>(`${this.baseUrl}/gstin/${gstin}`);
  }

  // Get customer by PAN
  getCustomerByPan(pan: string): Observable<Customer> {
    return this.http.get<Customer>(`${this.baseUrl}/pan/${pan}`);
  }

  // Create new customer
  createCustomer(request: CustomerRequest): Observable<CustomerResponse> {
    return this.http.post<CustomerResponse>(this.baseUrl, request);
  }

  // Update customer
  updateCustomer(id: string, request: CustomerRequest): Observable<CustomerResponse> {
    return this.http.put<CustomerResponse>(`${this.baseUrl}/${id}`, request);
  }

  // Update customer status
  updateCustomerStatus(id: string, isActive: boolean): Observable<CustomerResponse> {
    const params = new HttpParams().set('isActive', isActive.toString());
    return this.http.patch<CustomerResponse>(`${this.baseUrl}/${id}/status`, null, { params });
  }

  // Delete customer
  deleteCustomer(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  // Get all customers (for dropdowns)
  getAllCustomers(): Observable<Customer[]> {
    const request: CustomRequest = {
      page: 0,
      size: 1000,
      sortBy: 'customerName',
      sortDirection: 'ASC'
    };
    return this.searchCustomers(request);
  }
}
