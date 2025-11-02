import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CustomerService } from '../../../services/customer.service';
import {
  Customer,
  CustomRequest,
  CustomerType,
  CustomerFilter
} from '../../../models/customer.model';

@Component({
  selector: 'app-customer-list',
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './customer-list.html',
  styleUrl: './customer-list.scss',
})
export class CustomerListComponent implements OnInit {
  customers: Customer[] = [];
  totalElements: number = 0;
  totalPages: number = 0;
  currentPage: number = 0;
  pageSize: number = 10;
  loading: boolean = false;

  // Filter properties
  searchTerm: string = '';
  filterCity: string = '';
  filterState: string = '';
  filterCustomerType: CustomerType | '' = '';
  filterIsActive: boolean | '' = '';
  filterIsGstRegistered: boolean | '' = '';

  // Sort properties
  sortBy: string = 'customerName';
  sortDirection: string = 'ASC';

  // Enum for template
  customerTypes = Object.values(CustomerType);

  constructor(private customerService: CustomerService) {}

  ngOnInit(): void {
    this.loadCustomers();
  }

  loadCustomers(): void {
    this.loading = true;

    const filter: CustomerFilter = {};
    if (this.searchTerm) filter.customerName = this.searchTerm;
    if (this.filterCity) filter.city = this.filterCity;
    if (this.filterState) filter.state = this.filterState;
    if (this.filterCustomerType) filter.customerType = this.filterCustomerType as CustomerType;
    if (this.filterIsActive !== '') filter.isActive = this.filterIsActive as boolean;
    if (this.filterIsGstRegistered !== '') filter.isGstRegistered = this.filterIsGstRegistered as boolean;

    const request: CustomRequest = {
      page: this.currentPage,
      size: this.pageSize,
      sortBy: this.sortBy,
      sortDirection: this.sortDirection,
      filters: filter
    };

    this.customerService.searchCustomers(request).subscribe({
      next: (customers: Customer[]) => {
        this.customers = customers;
        this.totalElements = customers.length;
        this.totalPages = Math.ceil(customers.length / this.pageSize);
        this.loading = false;
      },
      error: (error: any) => {
        console.error('Error loading customers:', error);
        this.loading = false;
      }
    });
  }

  onSearch(): void {
    this.currentPage = 0;
    this.loadCustomers();
  }

  onClearFilters(): void {
    this.searchTerm = '';
    this.filterCity = '';
    this.filterState = '';
    this.filterCustomerType = '';
    this.filterIsActive = '';
    this.filterIsGstRegistered = '';
    this.currentPage = 0;
    this.loadCustomers();
  }

  onSort(column: string): void {
    if (this.sortBy === column) {
      this.sortDirection = this.sortDirection === 'ASC' ? 'DESC' : 'ASC';
    } else {
      this.sortBy = column;
      this.sortDirection = 'ASC';
    }
    this.currentPage = 0;
    this.loadCustomers();
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.loadCustomers();
  }

  onPageSizeChange(): void {
    this.currentPage = 0;
    this.loadCustomers();
  }

  toggleCustomerStatus(customer: Customer): void {
    if (!customer.id) {
      console.error('Customer ID is required');
      return;
    }

    const newStatus = !customer.isActive;
    this.customerService.updateCustomerStatus(customer.id, newStatus).subscribe({
      next: (updatedCustomer) => {
        const index = this.customers.findIndex(c => c.id === customer.id);
        if (index !== -1) {
          this.customers[index] = updatedCustomer;
        }
      },
      error: (error) => {
        console.error('Error updating customer status:', error);
      }
    });
  }

  deleteCustomer(customer: Customer): void {
    if (!customer.id) {
      console.error('Customer ID is required');
      return;
    }

    if (confirm(`Are you sure you want to delete customer "${customer.customerName}"?`)) {
      this.customerService.deleteCustomer(customer.id).subscribe({
        next: () => {
          this.loadCustomers();
        },
        error: (error) => {
          console.error('Error deleting customer:', error);
          alert('Error deleting customer. Please try again.');
        }
      });
    }
  }

  getPages(): number[] {
    const pages: number[] = [];
    const startPage = Math.max(0, this.currentPage - 2);
    const endPage = Math.min(this.totalPages - 1, this.currentPage + 2);

    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }
    return pages;
  }

  getMaxShownItems(): number {
    return Math.min((this.currentPage + 1) * this.pageSize, this.totalElements);
  }
}
