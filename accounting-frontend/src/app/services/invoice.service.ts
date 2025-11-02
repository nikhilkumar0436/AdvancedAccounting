import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class InvoiceService {
  private readonly baseUrl = `${environment.apiUrl}/invoices`;

  constructor(private http: HttpClient) {}

  // Create a new invoice
  createInvoice(request: any): Observable<any> {
    return this.http.post<any>(this.baseUrl, request);
  }

  // Update an existing invoice
  updateInvoice(id: string, request: any): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/${id}`, request);
  }

  // Get invoice by ID
  getInvoiceById(id: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/${id}`);
  }

  // Get invoice by number
  getInvoiceByNumber(invoiceNumber: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/number/${invoiceNumber}`);
  }

  // Get all invoices with pagination
  getAllInvoices(page: number = 0, size: number = 20, sortBy: string = 'invoiceDate', sortDirection: string = 'DESC'): Observable<any> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortDirection', sortDirection);

    return this.http.get<any>(this.baseUrl, { params });
  }

  // Get invoices by company
  getInvoicesByCompany(companyId: string, page: number = 0, size: number = 20): Observable<any> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<any>(`${this.baseUrl}/company/${companyId}`, { params });
  }

  // Get invoices by customer
  getInvoicesByCustomer(customerId: string, page: number = 0, size: number = 20): Observable<any> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<any>(`${this.baseUrl}/customer/${customerId}`, { params });
  }

  // Get invoices by date range
  getInvoicesByDateRange(startDate: string, endDate: string): Observable<any[]> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);

    return this.http.get<any[]>(`${this.baseUrl}/date-range`, { params });
  }

  // Get invoices by company and date range
  getInvoicesByCompanyAndDateRange(companyId: string, startDate: string, endDate: string): Observable<any[]> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);

    return this.http.get<any[]>(`${this.baseUrl}/company/${companyId}/date-range`, { params });
  }

  // Get overdue invoices
  getOverdueInvoices(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/overdue`);
  }

  // Get overdue invoices by company
  getOverdueInvoicesByCompany(companyId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/company/${companyId}/overdue`);
  }

  // Get unpaid invoices
  getUnpaidInvoices(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/unpaid`);
  }

  // Get unpaid invoices by company
  getUnpaidInvoicesByCompany(companyId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/company/${companyId}/unpaid`);
  }

  // Cancel an invoice
  cancelInvoice(id: string, cancellationReason: string): Observable<any> {
    const params = new HttpParams().set('cancellationReason', cancellationReason);
    return this.http.put<any>(`${this.baseUrl}/${id}/cancel`, null, { params });
  }

  // Approve an invoice
  approveInvoice(id: string): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/${id}/approve`, null);
  }

  // Reject an invoice
  rejectInvoice(id: string, rejectionReason: string): Observable<any> {
    const params = new HttpParams().set('rejectionReason', rejectionReason);
    return this.http.put<any>(`${this.baseUrl}/${id}/reject`, null, { params });
  }

  // Delete an invoice
  deleteInvoice(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
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
