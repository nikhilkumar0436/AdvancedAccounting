import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { InvoiceService } from '../../../services/invoice.service';
import { CustomerService } from '../../../services/customer.service';
import {
  InvoiceResponse,
  InvoiceFilterRequest,
  PageResponse,
  InvoiceType,
  PaymentType
} from '../../../models/invoice.model';
import { CustomerResponse } from '../../../models/customer.model';

@Component({
  selector: 'app-invoice-list',
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './invoice-list.html',
  styleUrl: './invoice-list.scss',
})
export class InvoiceListComponent implements OnInit {
  invoices: InvoiceResponse[] = [];
  customers: CustomerResponse[] = [];
  totalElements: number = 0;
  totalPages: number = 0;
  currentPage: number = 0;
  pageSize: number = 10;
  loading: boolean = false;
  loadingCustomers: boolean = false;

  // Filter properties
  searchTerm: string = '';
  filterCustomerId: string = '';
  filterInvoiceType: InvoiceType | '' = '';
  filterPaymentType: PaymentType | '' = '';
  filterStartDate: string = '';
  filterEndDate: string = '';
  filterMinAmount: number | null = null;
  filterMaxAmount: number | null = null;
  filterFinancialYear: string = '';
  filterPlaceOfSupply: string = '';
  filterIsInterState: boolean | '' = '';
  filterReverseCharge: boolean | '' = '';

  // Sort properties
  sortBy: string = 'invoiceDate';
  sortDirection: string = 'DESC';

  // Enums for template
  invoiceTypes = Object.values(InvoiceType);
  paymentTypes = Object.values(PaymentType);

  // Statistics
  selectedCompanyId: string = ''; // This should come from user context
  statistics = {
    count: 0,
    totalAmount: 0,
    outstandingAmount: 0
  };

  constructor(
    private invoiceService: InvoiceService,
    private customerService: CustomerService
  ) {}

  ngOnInit(): void {
    this.loadCustomers();
    this.loadInvoices();
    this.loadStatistics();
  }

  loadCustomers(): void {
    this.loadingCustomers = true;
    this.customerService.getAllCustomers().subscribe({
      next: (customers: any[]) => {
        this.customers = customers;
        this.loadingCustomers = false;
      },
      error: (error: any) => {
        console.error('Error loading customers:', error);
        this.loadingCustomers = false;
      }
    });
  }

  loadInvoices(): void {
    this.loading = true;

    const filterRequest: InvoiceFilterRequest = {};
    if (this.filterCustomerId) filterRequest.customerId = this.filterCustomerId;
    if (this.filterInvoiceType) filterRequest.invoiceType = this.filterInvoiceType as InvoiceType;
    if (this.filterPaymentType) filterRequest.paymentType = this.filterPaymentType as PaymentType;
    if (this.filterStartDate) filterRequest.startDate = this.filterStartDate;
    if (this.filterEndDate) filterRequest.endDate = this.filterEndDate;
    if (this.filterMinAmount) filterRequest.minAmount = this.filterMinAmount;
    if (this.filterMaxAmount) filterRequest.maxAmount = this.filterMaxAmount;
    if (this.filterFinancialYear) filterRequest.financialYear = this.filterFinancialYear;
    if (this.filterPlaceOfSupply) filterRequest.placeOfSupply = this.filterPlaceOfSupply;
    if (this.filterIsInterState !== '') filterRequest.isInterState = this.filterIsInterState as boolean;
    if (this.filterReverseCharge !== '') filterRequest.reverseChargeApplicable = this.filterReverseCharge as boolean;

    // If we have any filters, use search API, otherwise use getAll
    if (Object.keys(filterRequest).length > 0 || this.searchTerm) {
      this.invoiceService.searchInvoices(
        filterRequest,
        this.currentPage,
        this.pageSize,
        this.sortBy,
        this.sortDirection
      ).subscribe({
        next: (response: any) => {
          this.invoices = response.content;
          this.totalElements = response.totalElements;
          this.totalPages = response.totalPages;
          this.loading = false;
        },
        error: (error: any) => {
          console.error('Error loading invoices:', error);
          this.loading = false;
        }
      });
    } else {
      this.invoiceService.getAllInvoices(
        this.currentPage,
        this.pageSize,
        this.sortBy,
        this.sortDirection
      ).subscribe({
        next: (response: any) => {
          this.invoices = response.content;
          this.totalElements = response.totalElements;
          this.totalPages = response.totalPages;
          this.loading = false;
        },
        error: (error: any) => {
          console.error('Error loading invoices:', error);
          this.loading = false;
        }
      });
    }
  }

  loadStatistics(): void {
    if (!this.selectedCompanyId) return;

    const currentDate = new Date();
    const currentYear = currentDate.getFullYear();
    const startDate = `${currentYear}-04-01`; // Financial year start
    const endDate = `${currentYear + 1}-03-31`; // Financial year end

    // Load count
    this.invoiceService.getInvoiceCount(this.selectedCompanyId, startDate, endDate).subscribe({
      next: (count: any) => this.statistics.count = count,
      error: (error: any) => console.error('Error loading invoice count:', error)
    });

    // Load total amount
    this.invoiceService.getTotalInvoiceAmount(this.selectedCompanyId, startDate, endDate).subscribe({
      next: (amount: any) => this.statistics.totalAmount = amount,
      error: (error: any) => console.error('Error loading total amount:', error)
    });

    // Load outstanding amount
    this.invoiceService.getOutstandingAmount(this.selectedCompanyId).subscribe({
      next: (amount: any) => this.statistics.outstandingAmount = amount,
      error: (error: any) => console.error('Error loading outstanding amount:', error)
    });
  }

  onSearch(): void {
    this.currentPage = 0;
    this.loadInvoices();
  }

  onClearFilters(): void {
    this.searchTerm = '';
    this.filterCustomerId = '';
    this.filterInvoiceType = '';
    this.filterPaymentType = '';
    this.filterStartDate = '';
    this.filterEndDate = '';
    this.filterMinAmount = null;
    this.filterMaxAmount = null;
    this.filterFinancialYear = '';
    this.filterPlaceOfSupply = '';
    this.filterIsInterState = '';
    this.filterReverseCharge = '';
    this.currentPage = 0;
    this.loadInvoices();
  }

  onSort(column: string): void {
    if (this.sortBy === column) {
      this.sortDirection = this.sortDirection === 'ASC' ? 'DESC' : 'ASC';
    } else {
      this.sortBy = column;
      this.sortDirection = 'ASC';
    }
    this.currentPage = 0;
    this.loadInvoices();
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.loadInvoices();
  }

  onPageSizeChange(): void {
    this.currentPage = 0;
    this.loadInvoices();
  }

  approveInvoice(invoice: InvoiceResponse): void {
    if (confirm(`Are you sure you want to approve invoice "${invoice.invoiceNumber}"?`)) {
      this.invoiceService.approveInvoice(invoice.id).subscribe({
        next: (updatedInvoice: any) => {
          const index = this.invoices.findIndex(i => i.id === invoice.id);
          if (index !== -1) {
            this.invoices[index] = updatedInvoice;
          }
          alert('Invoice approved successfully!');
        },
        error: (error: any) => {
          console.error('Error approving invoice:', error);
          alert('Error approving invoice. Please try again.');
        }
      });
    }
  }

  rejectInvoice(invoice: InvoiceResponse): void {
    const reason = prompt(`Enter rejection reason for invoice "${invoice.invoiceNumber}":`);
    if (reason) {
      this.invoiceService.rejectInvoice(invoice.id, reason).subscribe({
        next: (updatedInvoice: any) => {
          const index = this.invoices.findIndex(i => i.id === invoice.id);
          if (index !== -1) {
            this.invoices[index] = updatedInvoice;
          }
          alert('Invoice rejected successfully!');
        },
        error: (error: any) => {
          console.error('Error rejecting invoice:', error);
          alert('Error rejecting invoice. Please try again.');
        }
      });
    }
  }

  cancelInvoice(invoice: InvoiceResponse): void {
    const reason = prompt(`Enter cancellation reason for invoice "${invoice.invoiceNumber}":`);
    if (reason) {
      this.invoiceService.cancelInvoice(invoice.id, reason).subscribe({
        next: (updatedInvoice: any) => {
          const index = this.invoices.findIndex(i => i.id === invoice.id);
          if (index !== -1) {
            this.invoices[index] = updatedInvoice;
          }
          alert('Invoice cancelled successfully!');
        },
        error: (error: any) => {
          console.error('Error cancelling invoice:', error);
          alert('Error cancelling invoice. Please try again.');
        }
      });
    }
  }

  deleteInvoice(invoice: InvoiceResponse): void {
    if (confirm(`Are you sure you want to delete invoice "${invoice.invoiceNumber}"?`)) {
      this.invoiceService.deleteInvoice(invoice.id).subscribe({
        next: () => {
          this.loadInvoices();
          alert('Invoice deleted successfully!');
        },
        error: (error: any) => {
          console.error('Error deleting invoice:', error);
          alert('Error deleting invoice. Please try again.');
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

  getCustomerName(customerId: string): string {
    const customer = this.customers.find(c => c.id === customerId);
    return customer ? customer.customerName : 'Unknown Customer';
  }

  getInvoiceTypeClass(type: InvoiceType): string {
    switch (type) {
      case InvoiceType.TAX_INVOICE: return 'badge-primary';
      case InvoiceType.CREDIT_NOTE: return 'badge-success';
      case InvoiceType.DEBIT_NOTE: return 'badge-warning';
      case InvoiceType.BILL_OF_SUPPLY: return 'badge-info';
      case InvoiceType.EXPORT_INVOICE: return 'badge-secondary';
      case InvoiceType.IMPORT_INVOICE: return 'badge-dark';
      default: return 'badge-light';
    }
  }

  getPaymentTypeClass(type: PaymentType): string {
    switch (type) {
      case PaymentType.CASH: return 'badge-success';
      case PaymentType.CREDIT: return 'badge-warning';
      case PaymentType.CHEQUE: return 'badge-info';
      case PaymentType.NEFT: return 'badge-primary';
      case PaymentType.RTGS: return 'badge-primary';
      case PaymentType.UPI: return 'badge-success';
      case PaymentType.CARD: return 'badge-secondary';
      default: return 'badge-light';
    }
  }

  exportToCSV(): void {
    // Implementation for CSV export would go here
    alert('CSV export feature coming soon!');
  }

  exportToPDF(): void {
    // Implementation for PDF export would go here
    alert('PDF export feature coming soon!');
  }
}
