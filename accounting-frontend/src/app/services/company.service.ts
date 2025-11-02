import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Company, CompanyResponse } from '../models/company.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CompanyService {
  private readonly baseUrl = `${environment.apiUrl}/companies`;

  constructor(private http: HttpClient) {}

  // Get all companies
  getAllCompanies(): Observable<CompanyResponse[]> {
    return this.http.get<CompanyResponse[]>(this.baseUrl);
  }

  // Get all active companies
  getAllActiveCompanies(): Observable<CompanyResponse[]> {
    return this.http.get<CompanyResponse[]>(`${this.baseUrl}/active`);
  }

  // Get company by ID
  getCompanyById(id: string): Observable<CompanyResponse> {
    return this.http.get<CompanyResponse>(`${this.baseUrl}/${id}`);
  }

  // Get company by GSTIN
  getCompanyByGstin(gstin: string): Observable<CompanyResponse> {
    return this.http.get<CompanyResponse>(`${this.baseUrl}/gstin/${gstin}`);
  }

  // Search companies by name
  searchCompaniesByName(name: string): Observable<CompanyResponse[]> {
    return this.http.get<CompanyResponse[]>(`${this.baseUrl}/search`, {
      params: { name }
    });
  }
}
