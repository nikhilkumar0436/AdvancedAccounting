import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { CompanyResponse } from '../models/company.model';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class CompanyContextService {
  private selectedCompanySubject = new BehaviorSubject<CompanyResponse | null>(null);
  public selectedCompany$ = this.selectedCompanySubject.asObservable();

  constructor(private authService: AuthService) {
    // Initialize with company from auth service
    const savedCompany = this.authService.getSelectedCompany();
    if (savedCompany) {
      this.selectedCompanySubject.next(savedCompany);
    }

    // Listen to auth state changes
    this.authService.authState$.subscribe(state => {
      this.selectedCompanySubject.next(state.selectedCompany);
    });
  }

  getSelectedCompany(): CompanyResponse | null {
    return this.selectedCompanySubject.value;
  }

  getSelectedCompanyId(): string | null {
    const company = this.getSelectedCompany();
    return company?.id || null;
  }

  getSelectedCompanyName(): string | null {
    const company = this.getSelectedCompany();
    return company?.companyName || null;
  }

  // Utility method to check if a company is selected
  hasSelectedCompany(): boolean {
    return this.getSelectedCompany() !== null;
  }

  // Method to get company for API calls (throws error if no company selected)
  getRequiredCompanyId(): string {
    const companyId = this.getSelectedCompanyId();
    if (!companyId) {
      throw new Error('No company selected. Please select a company first.');
    }
    return companyId;
  }
}
