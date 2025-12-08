import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { CompanyContextService } from './company-context.service';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private readonly API_URL = 'http://localhost:8080/api/v1';

  constructor(
    private http: HttpClient,
    private companyContext: CompanyContextService
  ) {}

  // GET request with company context
  get<T>(endpoint: string, params?: HttpParams): Observable<T> {
    const companyId = this.companyContext.getSelectedCompanyId();
    if (!companyId) {
      return throwError(() => new Error('No company selected. Please select a company first.'));
    }

    // Add company filter to params
    let httpParams = params || new HttpParams();
    httpParams = httpParams.set('companyId', companyId);

    return this.http.get<T>(`${this.API_URL}${endpoint}`, { params: httpParams });
  }

  // POST request with company context
  post<T>(endpoint: string, data: any): Observable<T> {
    const companyId = this.companyContext.getSelectedCompanyId();
    if (!companyId) {
      return throwError(() => new Error('No company selected. Please select a company first.'));
    }

    // Ensure company ID is in the data
    const requestData = {
      ...data,
      companyId: data.companyId || companyId
    };

    return this.http.post<T>(`${this.API_URL}${endpoint}`, requestData);
  }

  // PUT request with company context
  put<T>(endpoint: string, data: any): Observable<T> {
    const companyId = this.companyContext.getSelectedCompanyId();
    if (!companyId) {
      return throwError(() => new Error('No company selected. Please select a company first.'));
    }

    // Ensure company ID is in the data
    const requestData = {
      ...data,
      companyId: data.companyId || companyId
    };

    return this.http.put<T>(`${this.API_URL}${endpoint}`, requestData);
  }

  // DELETE request with company context
  delete<T>(endpoint: string): Observable<T> {
    const companyId = this.companyContext.getSelectedCompanyId();
    if (!companyId) {
      return throwError(() => new Error('No company selected. Please select a company first.'));
    }

    const params = new HttpParams().set('companyId', companyId);
    return this.http.delete<T>(`${this.API_URL}${endpoint}`, { params });
  }

  // Generic request without company context (for auth, companies, etc.)
  requestWithoutCompany<T>(method: string, endpoint: string, data?: any, params?: HttpParams): Observable<T> {
    const url = `${this.API_URL}${endpoint}`;

    switch (method.toUpperCase()) {
      case 'GET':
        return this.http.get<T>(url, { params });
      case 'POST':
        return this.http.post<T>(url, data);
      case 'PUT':
        return this.http.put<T>(url, data);
      case 'DELETE':
        return this.http.delete<T>(url, { params });
      default:
        return throwError(() => new Error(`Unsupported HTTP method: ${method}`));
    }
  }

  // Helper method to get current company info for requests
  getCurrentCompanyId(): string | null {
    return this.companyContext.getSelectedCompanyId();
  }

  // Helper method to check if company is selected
  hasCompanySelected(): boolean {
    return this.companyContext.hasSelectedCompany();
  }
}
