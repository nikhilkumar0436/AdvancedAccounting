import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class InvoiceService {
  private readonly baseUrl = `${environment.apiUrl}/invoices`;

  constructor(
    private http: HttpClient,
    private apiService: ApiService
  ) {}

  // Create a new invoice (company-aware)
  createInvoice(request: any): Observable<any> {
    return this.apiService.post<any>('/invoices', request);
  }

  // Update an existing invoice (company-aware)
  updateInvoice(id: string, request: any): Observable<any> {
    return this.apiService.put<any>(`/invoices/${id}`, request);
  }

  // Get invoice by ID (company-aware)
  getInvoiceById(id: string): Observable<any> {
    return this.apiService.get<any>(`/invoices/${id}`);
  }

  // Get invoice by number (company-aware)
  getInvoiceByNumber(invoiceNumber: string): Observable<any> {
    const params = new HttpParams().set('invoiceNumber', invoiceNumber);
    return this.apiService.get<any>('/invoices/by-number', params);
  }

  // Get all invoices with pagination (company-aware)
  getAllInvoices(page: number = 0, size: number = 20, sortBy: string = 'invoiceDate', sortDirection: string = 'DESC'): Observable<any> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortDirection', sortDirection);

    return this.apiService.get<any>('/invoices', params);
  }

  // Get invoices by customer (company-aware)
  getInvoicesByCustomer(customerId: string, page: number = 0, size: number = 20): Observable<any> {
    const params = new HttpParams()
      .set('customerId', customerId)
      .set('page', page.toString())
      .set('size', size.toString());

    return this.apiService.get<any>('/invoices/by-customer', params);
  }

  // Get invoices by date range (company-aware)
  getInvoicesByDateRange(startDate: string, endDate: string): Observable<any[]> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);

    return this.apiService.get<any[]>('/invoices/by-date-range', params);
  }

  // Get overdue invoices (company-aware)
  getOverdueInvoices(): Observable<any[]> {
    return this.apiService.get<any[]>('/invoices/overdue');
  }

  // Get unpaid invoices (company-aware)
  getUnpaidInvoices(): Observable<any[]> {
    return this.apiService.get<any[]>('/invoices/unpaid');
  }

  // Cancel an invoice (company-aware)
  cancelInvoice(id: string, cancellationReason: string): Observable<any> {
    const data = { cancellationReason };
    return this.apiService.put<any>(`/invoices/${id}/cancel`, data);
  }

  // Approve an invoice (company-aware)
  approveInvoice(id: string): Observable<any> {
    return this.apiService.put<any>(`/invoices/${id}/approve`, {});
  }

  // Reject an invoice (company-aware)
  rejectInvoice(id: string, rejectionReason: string): Observable<any> {
    const data = { rejectionReason };
    return this.apiService.put<any>(`/invoices/${id}/reject`, data);
  }

  // Delete an invoice (company-aware)
  deleteInvoice(id: string): Observable<void> {
    return this.apiService.delete<void>(`/invoices/${id}`);
  }

  // Get invoice count by company and date range
  getInvoiceCount(companyId: string, startDate: string, endDate: string): Observable<number> {
    const params = new HttpParams()
      .set('companyId', companyId)
      .set('startDate', startDate)
      .set('endDate', endDate);

    return this.http.get<number>(`${this.baseUrl}/statistics/count`, { params });
  }

  // Get total invoice amount by company and date range
  getTotalInvoiceAmount(companyId: string, startDate: string, endDate: string): Observable<number> {
    const params = new HttpParams()
      .set('companyId', companyId)
      .set('startDate', startDate)
      .set('endDate', endDate);

    return this.http.get<number>(`${this.baseUrl}/statistics/total-amount`, { params });
  }

  // Get outstanding amount by company
  getOutstandingAmount(companyId: string): Observable<number> {
    const params = new HttpParams().set('companyId', companyId);
    return this.http.get<number>(`${this.baseUrl}/statistics/outstanding-amount`, { params });
  }

  // Search invoices with filters
  searchInvoices(
    filterRequest: any,
    page: number = 0,
    size: number = 20,
    sortBy: string = 'invoiceDate',
    sortDirection: string = 'DESC'
  ): Observable<any> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortDirection', sortDirection);

    return this.http.post<any>(`${this.baseUrl}/search`, filterRequest, { params });
  }

  // Get all invoice types
  getInvoiceTypes(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/invoice-types`);
  }

  // Get all payment types
  getPaymentTypes(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/payment-types`);
  }

  // Get invoice items
  getInvoiceItems(id: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/${id}/items`);
  }
}
