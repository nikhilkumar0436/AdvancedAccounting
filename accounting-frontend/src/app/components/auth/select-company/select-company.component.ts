import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { CompanyResponse } from '../../../models/company.model';

@Component({
  selector: 'app-select-company',
  imports: [CommonModule],
  templateUrl: './select-company.component.html',
  styleUrl: './select-company.component.scss'
})
export class SelectCompanyComponent implements OnInit {
  companies: CompanyResponse[] = [];
  selectedCompany: CompanyResponse | null = null;
  loading = false;
  user: any = null;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Check if user is authenticated
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }

    this.user = this.authService.getUser();

    // Load companies from backend
    this.loading = true;
    this.authService.getAvailableCompanies().subscribe({
      next: (companies) => {
        this.companies = companies;
        this.loading = false;

        // If already has selected company, show it as selected
        const currentSelected = this.authService.getSelectedCompany();
        if (currentSelected) {
          this.selectedCompany = currentSelected;
        }
      },
      error: (error) => {
        console.error('Error loading companies:', error);
        this.loading = false;
        // Fallback: redirect to login if companies can't be loaded
        this.router.navigate(['/login']);
      }
    });
  }

  selectCompany(company: CompanyResponse): void {
    this.selectedCompany = company;
  }

  confirmSelection(): void {
    if (this.selectedCompany) {
      this.loading = true;

      // Store the selected company
      this.authService.selectCompany(this.selectedCompany);

      // Navigate to dashboard
      setTimeout(() => {
        this.loading = false;
        this.router.navigate(['/dashboard']);
      }, 500);
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  isSelected(company: CompanyResponse): boolean {
    return this.selectedCompany?.id === company.id;
  }
}
