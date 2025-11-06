import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, FormArray, Validators, ReactiveFormsModule } from '@angular/forms';
import { InvoiceService } from '../../../services/invoice.service';
import { CustomerService } from '../../../services/customer.service';
import { ProductService } from '../../../services/product.service';
import { CompanyService } from '../../../services/company.service';
import {
  InvoiceRequest,
  InvoiceResponse,
  InvoiceType,
  PaymentType,
  InvoiceItemRequest
} from '../../../models/invoice.model';
import { CustomerResponse } from '../../../models/customer.model';
import { Product } from '../../../models/product.model';
import { CompanyResponse } from '../../../models/company.model';

@Component({
  selector: 'app-invoice-form',
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './invoice-form.html',
  styleUrl: './invoice-form.scss',
})
export class InvoiceFormComponent implements OnInit {
  invoiceForm: FormGroup;
  isEditMode: boolean = false;
  invoiceId: string | null = null;
  loading: boolean = false;
  submitting: boolean = false;
  customers: CustomerResponse[] = [];
  products: Product[] = [];
  companies: CompanyResponse[] = [];

  // Enums for template
  invoiceTypes = Object.values(InvoiceType);
  paymentTypes = Object.values(PaymentType);

  constructor(
    private fb: FormBuilder,
    private invoiceService: InvoiceService,
    private customerService: CustomerService,
    private productService: ProductService,
    private companyService: CompanyService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.invoiceForm = this.createForm();
  }

  ngOnInit(): void {
    this.invoiceId = this.route.snapshot.paramMap.get('id');
    this.isEditMode = !!this.invoiceId;

    this.loadCompanies();
    this.loadCustomers();
    this.loadProducts();

    if (this.isEditMode && this.invoiceId) {
      this.loadInvoice(this.invoiceId);
    }
  }

  createForm(): FormGroup {
    return this.fb.group({
      companyId: ['', Validators.required], // <-- Add companyId control
      customerId: ['', Validators.required],
      invoiceNumber: ['', [Validators.required, Validators.maxLength(50)]],
      invoiceDate: ['', Validators.required],
      invoiceType: [InvoiceType.TAX_INVOICE, Validators.required],
      financialYear: ['', [Validators.required, Validators.minLength(9), Validators.maxLength(9)]],
      placeOfSupply: ['', [Validators.required, Validators.maxLength(50)]],
      placeOfSupplyStateCode: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(2)]],
      isInterState: [false],
      reverseChargeApplicable: [false],
      paymentType: [PaymentType.CREDIT, Validators.required],
      dueDate: [''],
      notes: [''],
      termsAndConditions: [''],
      invoiceItems: this.fb.array([])
    });
  }

  get invoiceItemsArray(): FormArray {
    return this.invoiceForm.get('invoiceItems') as FormArray;
  }

  createInvoiceItemForm(): FormGroup {
    return this.fb.group({
      productId: ['', Validators.required],
      description: ['', [Validators.required, Validators.maxLength(500)]],
      hsnCode: [''],
      quantity: [1, [Validators.required, Validators.min(0.01)]],
      unit: ['Nos', Validators.required],
      rate: [0, [Validators.required, Validators.min(0)]],
      amount: [0],
      discountPercentage: [0, [Validators.min(0), Validators.max(100)]],
      discountAmount: [0],
      taxableAmount: [0],
      cgstRate: [0, [Validators.min(0), Validators.max(100)]],
      cgstAmount: [0],
      sgstRate: [0, [Validators.min(0), Validators.max(100)]],
      sgstAmount: [0],
      igstRate: [0, [Validators.min(0), Validators.max(100)]],
      igstAmount: [0],
      cessRate: [0, [Validators.min(0), Validators.max(100)]],
      cessAmount: [0],
      totalAmount: [0]
    });
  }

  addInvoiceItem(): void {
    this.invoiceItemsArray.push(this.createInvoiceItemForm());
  }

  removeInvoiceItem(index: number): void {
    this.invoiceItemsArray.removeAt(index);
    this.calculateTotals();
  }

  loadCompanies(): void {
    this.companyService.getAllActiveCompanies().subscribe({
      next: (companies: CompanyResponse[]) => {
        this.companies = companies;
        if (companies.length === 1) {
          this.invoiceForm.get('companyId')?.setValue(companies[0].id);
        } else if (companies.length > 1) {
          this.invoiceForm.get('companyId')?.setValue(companies[0].id); // TODO: allow user selection if needed
        }
      },
      error: (error: any) => {
        console.error('Error loading companies:', error);
      }
    });
  }

  loadCustomers(): void {
    this.customerService.getAllCustomers().subscribe({
      next: (customers: any) => {
        this.customers = customers;
      },
      error: (error: any) => {
        console.error('Error loading customers:', error);
      }
    });
  }

  loadProducts(): void {
    this.productService.getAllProducts().subscribe({
      next: (products: any) => {
        this.products = products;
      },
      error: (error: any) => {
        console.error('Error loading products:', error);
      }
    });
  }

  onProductSelected(index: number, productId: string): void {
    if (productId) {
      const product = this.products.find(p => p.id === productId);
      if (product) {
        const item = this.invoiceItemsArray.at(index);
        item.patchValue({
          description: product.productName,
          hsnCode: product.hsnSacCode || '',
          unit: product.unitOfMeasurement || 'Nos',
          rate: product.sellingPrice || 0
        });
        this.calculateItemAmount(index);
      }
    }
  }

  calculateItemAmount(index: number): void {
    const item = this.invoiceItemsArray.at(index);
    const quantity = item.get('quantity')?.value || 0;
    const rate = item.get('rate')?.value || 0;
    const discountPercentage = item.get('discountPercentage')?.value || 0;

    const amount = quantity * rate;
    const discountAmount = (amount * discountPercentage) / 100;
    const taxableAmount = amount - discountAmount;

    item.patchValue({
      amount: amount,
      discountAmount: discountAmount,
      taxableAmount: taxableAmount
    });

    this.calculateTaxes(index);
  }

  calculateTaxes(index: number): void {
    const item = this.invoiceItemsArray.at(index);
    const taxableAmount = item.get('taxableAmount')?.value || 0;
    const isInterState = this.invoiceForm.get('isInterState')?.value || false;

    if (isInterState) {
      const igstRate = item.get('igstRate')?.value || 0;
      const igstAmount = (taxableAmount * igstRate) / 100;
      item.patchValue({
        igstAmount: igstAmount,
        cgstAmount: 0,
        sgstAmount: 0
      });
    } else {
      const cgstRate = item.get('cgstRate')?.value || 0;
      const sgstRate = item.get('sgstRate')?.value || 0;
      const cgstAmount = (taxableAmount * cgstRate) / 100;
      const sgstAmount = (taxableAmount * sgstRate) / 100;
      item.patchValue({
        cgstAmount: cgstAmount,
        sgstAmount: sgstAmount,
        igstAmount: 0
      });
    }

    const cessRate = item.get('cessRate')?.value || 0;
    const cessAmount = (taxableAmount * cessRate) / 100;

    const cgstAmount = item.get('cgstAmount')?.value || 0;
    const sgstAmount = item.get('sgstAmount')?.value || 0;
    const igstAmount = item.get('igstAmount')?.value || 0;

    const totalAmount = taxableAmount + cgstAmount + sgstAmount + igstAmount + cessAmount;

    item.patchValue({
      cessAmount: cessAmount,
      totalAmount: totalAmount
    });
  }

  calculateTotals(): void {
    // This method can be used to recalculate all totals
    for (let i = 0; i < this.invoiceItemsArray.length; i++) {
      this.calculateItemAmount(i);
    }
  }

  loadInvoice(id: string): void {
    this.loading = true;
    this.invoiceService.getInvoiceById(id).subscribe({
      next: (invoice: any) => {
        this.populateForm(invoice);
        this.loading = false;
      },
      error: (error: any) => {
        console.error('Error loading invoice:', error);
        this.loading = false;
        alert('Error loading invoice. Please try again.');
      }
    });
  }

  populateForm(invoice: InvoiceResponse): void {
    this.invoiceForm.patchValue({
      customerId: invoice.customerInfo.id,
      invoiceNumber: invoice.invoiceNumber,
      invoiceDate: invoice.invoiceDate,
      invoiceType: invoice.invoiceType,
      financialYear: invoice.financialYear,
      placeOfSupply: invoice.placeOfSupply,
      placeOfSupplyStateCode: invoice.placeOfSupplyStateCode,
      isInterState: invoice.isInterState,
      reverseChargeApplicable: invoice.reverseChargeApplicable,
      paymentType: invoice.paymentType,
      dueDate: invoice.dueDate,
      notes: invoice.notes,
      termsAndConditions: invoice.termsAndConditions
    });

    // Populate invoice items
    this.invoiceItemsArray.clear();
    invoice.invoiceItems.forEach(item => {
      const itemForm = this.createInvoiceItemForm();
      itemForm.patchValue({
        productId: item.productInfo.id,
        description: item.description,
        hsnCode: item.hsnCode,
        quantity: item.quantity,
        unit: item.unit,
        rate: item.rate,
        amount: item.amount,
        discountPercentage: item.discountPercentage,
        discountAmount: item.discountAmount,
        taxableAmount: item.taxableAmount,
        cgstRate: item.cgstRate,
        cgstAmount: item.cgstAmount,
        sgstRate: item.sgstRate,
        sgstAmount: item.sgstAmount,
        igstRate: item.igstRate,
        igstAmount: item.igstAmount,
        cessRate: item.cessRate,
        cessAmount: item.cessAmount,
        totalAmount: item.totalAmount
      });
      this.invoiceItemsArray.push(itemForm);
    });
  }

  onSubmit(): void {
    if (this.invoiceForm.invalid) {
      this.markFormGroupTouched(this.invoiceForm);
      return;
    }

    this.submitting = true;
    const formValue = this.invoiceForm.value;

    // Calculate totals before submitting
    this.calculateTotals();

    // TODO: Replace this with actual companyId selection or retrieval logic
    // For now, set a default or hardcoded companyId (should be dynamic in real app)
    const companyId = this.getCompanyIdForInvoice();

    const totalAmount = this.calculateTotalAmount();

    const request: InvoiceRequest = {
      ...formValue,
      companyId, // Ensure companyId is sent
      totalTaxableAmount: this.calculateTotalTaxableAmount(),
      totalAmount: totalAmount,
      totalInvoiceAmount: totalAmount, // Ensure this is sent to backend
      grandTotal: this.calculateGrandTotal()
    };

    const operation = this.isEditMode && this.invoiceId
      ? this.invoiceService.updateInvoice(this.invoiceId, request)
      : this.invoiceService.createInvoice(request);

    operation.subscribe({
      next: (response: any) => {
        this.submitting = false;
        alert(`Invoice ${this.isEditMode ? 'updated' : 'created'} successfully!`);
        this.router.navigate(['/invoices']);
      },
      error: (error: any) => {
        console.error('Error saving invoice:', error);
        this.submitting = false;
        alert(`Error ${this.isEditMode ? 'updating' : 'creating'} invoice. Please try again.`);
      }
    });
  }

  // Add this helper to get companyId (replace with real logic as needed)
  private getCompanyIdForInvoice(): string {
    return this.invoiceForm.get('companyId')?.value || '';
  }

  onCancel(): void {
    this.router.navigate(['/invoices']);
  }

  isFormReadyToSubmit(): boolean {
    return this.invoiceForm.valid && this.invoiceItemsArray.length > 0;
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.invoiceForm.get(fieldName);
    return field ? field.invalid && (field.dirty || field.touched) : false;
  }

  getFieldError(fieldName: string): string {
    const field = this.invoiceForm.get(fieldName);
    if (field?.errors && (field.dirty || field.touched)) {
      if (field.errors['required']) return `${fieldName} is required`;
      if (field.errors['maxlength']) return `${fieldName} is too long`;
      if (field.errors['minlength']) return `${fieldName} is too short`;
      if (field.errors['min']) return `${fieldName} must be greater than 0`;
      if (field.errors['max']) return `${fieldName} is too large`;
    }
    return '';
  }

  logFormErrors(): void {
    console.log('Form errors:', this.invoiceForm.errors);
    console.log('Form value:', this.invoiceForm.value);
    console.log('Form valid:', this.invoiceForm.valid);
    Object.keys(this.invoiceForm.controls).forEach(key => {
      const control = this.invoiceForm.get(key);
      if (control?.invalid) {
        console.log(`Field ${key} errors:`, control.errors);
      }
    });
  }

  private calculateTotalTaxableAmount(): number {
    return this.invoiceItemsArray.controls.reduce((total, item) => {
      return total + (item.get('taxableAmount')?.value || 0);
    }, 0);
  }

  private calculateTotalAmount(): number {
    return this.invoiceItemsArray.controls.reduce((total, item) => {
      return total + (item.get('totalAmount')?.value || 0);
    }, 0);
  }

  private calculateGrandTotal(): number {
    return this.calculateTotalAmount();
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();

      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }
}
