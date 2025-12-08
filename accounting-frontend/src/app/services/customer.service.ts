import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { map } from 'rxjs/operators';
import {
  Customer,
  CustomerRequest,
  CustomerResponse,
  CustomRequest,
  PageResponse
} from '../models/customer.model';
import { ApiService } from './api.service';
import { environment } from '../../environments/environment';
import { CompanyContextService } from './company-context.service';

@Injectable({
  providedIn: 'root'
})
export class CustomerService {
  private readonly baseUrl = `${environment.apiUrl}/customers`;

  constructor(
    private http: HttpClient,
    private apiService: ApiService,
    private companyContext: CompanyContextService
  ) {}

  // Search customers with filters and pagination (company-aware)
  searchCustomers(request: CustomRequest): Observable<Customer[]> {
    return this.apiService.post<Customer[]>('/customers/search', request);
  }

  // Get customer by ID (company-aware)
  getCustomerById(id: string): Observable<CustomerResponse> {
    return this.apiService.get<CustomerResponse>(`/customers/${id}`);
  }

  // Get all customers for selected company
  getAllCustomers(): Observable<Customer[]> {
    return this.apiService.get<Customer[]>('/customers');
  }

  // Get customer by GSTIN (company-aware)
  getCustomerByGstin(gstin: string): Observable<Customer> {
    const params = new HttpParams().set('gstin', gstin);
    return this.apiService.get<Customer>('/customers/by-gstin', params);
  }

  // Get customer by PAN (company-aware)
  getCustomerByPan(pan: string): Observable<Customer> {
    const params = new HttpParams().set('pan', pan);
    return this.apiService.get<Customer>('/customers/by-pan', params);
  }

  // Create new customer (company-aware)
  createCustomer(request: CustomerRequest): Observable<CustomerResponse> {
    return this.apiService.post<CustomerResponse>('/customers', request);
  }

  // Update customer (company-aware)
  updateCustomer(id: string, request: CustomerRequest): Observable<CustomerResponse> {
    return this.apiService.put<CustomerResponse>(`/customers/${id}`, request);
  }

  // Update customer status (company-aware)
  updateCustomerStatus(id: string, isActive: boolean): Observable<CustomerResponse> {
    const params = new HttpParams().set('isActive', isActive.toString());
    return this.apiService.get<CustomerResponse>(`/customers/${id}/status`, params);
  }

  // Delete customer (company-aware)
  deleteCustomer(id: string): Observable<void> {
    return this.apiService.delete<void>(`/customers/${id}`);
  }

  // New: fetch flat customers list for selected company (no pagination)
  getCustomersList(): Observable<Customer[]> {
    const companyId = this.companyContext.getSelectedCompanyId();
    if (!companyId) {
      return throwError(() => new Error('No company selected'));
    }
    const params = new HttpParams().set('companyId', companyId);
    return this.apiService.requestWithoutCompany<Customer[]>('GET', '/customers/list', undefined, params);
  }
}
