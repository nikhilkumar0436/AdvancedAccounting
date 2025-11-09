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
  viewOnly: boolean = false;

  // Enums for template
  invoiceTypes = Object.values(InvoiceType);
  paymentTypes = Object.values(PaymentType);

  private loadedInvoice: InvoiceResponse | null = null;
  private companiesLoaded = false;
  private invoiceLoaded = false;

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
    this.isEditMode = !!this.invoiceId && this.router.url.endsWith('edit');
    this.viewOnly = !!this.invoiceId && !this.isEditMode;
    this.companiesLoaded = false;
    this.invoiceLoaded = false;
    this.loadedInvoice = null;

    this.loadCompanies(() => {
      this.companiesLoaded = true;
      this.loadCustomers();
      this.loadProducts();
      if (this.isEditMode && this.invoiceId) {
        if (this.invoiceLoaded && this.loadedInvoice) {
          this.ensureCompanyInListAndPatch(this.loadedInvoice);
        }
        this.loadInvoice(this.invoiceId);
      }
    });
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
    const newItem = this.createInvoiceItemForm();
    this.setDefaultTaxRates(newItem);
    this.invoiceItemsArray.push(newItem);
  }

  setDefaultTaxRates(itemForm: FormGroup): void {
    const isInterState = this.invoiceForm.get('isInterState')?.value || false;

    if (isInterState) {
      // Inter-state: Use IGST (typically 18% for most goods)
      itemForm.patchValue({
        cgstRate: 0,
        sgstRate: 0,
        igstRate: 18, // Default IGST rate
        cessRate: 0
      });
    } else {
      // Intra-state: Use CGST + SGST (typically 9% each = 18% total)
      itemForm.patchValue({
        cgstRate: 9, // Default CGST rate
        sgstRate: 9, // Default SGST rate
        igstRate: 0,
        cessRate: 0
      });
    }
  }

  removeInvoiceItem(index: number): void {
    this.invoiceItemsArray.removeAt(index);
    this.calculateTotals();
  }

  loadCompanies(callback?: () => void): void {
    this.companyService.getAllActiveCompanies().subscribe({
      next: (companies: CompanyResponse[]) => {
        this.companies = companies;
        // Only auto-select first company if NOT in edit mode and companyId is empty
        if (!this.isEditMode && companies.length > 0 && !this.invoiceForm.get('companyId')?.value) {
          this.invoiceForm.get('companyId')?.setValue(companies[0].id);
          console.log('Auto-selected company:', companies[0].id, companies[0].companyName);
        }
        if (callback) callback();
      },
      error: (error: any) => {
        console.error('Error loading companies:', error);
        if (callback) callback();
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

  onInterStateChange(): void {
    // Update tax rates for all existing items when inter-state selection changes
    for (let i = 0; i < this.invoiceItemsArray.length; i++) {
      const item = this.invoiceItemsArray.at(i) as FormGroup;
      this.setDefaultTaxRates(item);
      this.calculateTaxes(i);
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
        this.loadedInvoice = invoice;
        this.invoiceLoaded = true;
        if (this.companiesLoaded) {
          this.ensureCompanyInListAndPatch(invoice);
        }
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

  ensureCompanyInListAndPatch(invoice: InvoiceResponse): void {
    const companyId = invoice.company?.id;
    if (!companyId) {
      console.warn('No companyId found in invoice data');
      return;
    }

    const existingCompany = this.companies.find(c => c.id === companyId);

    if (!existingCompany) {
      // Company not found in list, add it temporarily for display
      const tempCompany: CompanyResponse = {
        id: companyId,
        companyName: invoice.company?.companyName || 'Unknown Company',
        gstin: invoice.company?.gstin || '',
        pan: invoice.company?.pan || '',
        addressLine1: invoice.company?.addressLine1 || '',
        addressLine2: invoice.company?.addressLine2 || '',
        city: invoice.company?.city || '',
        state: invoice.company?.state || '',
        stateCode: '', // We don't have stateCode in the backend response, so provide default
        pincode: invoice.company?.pincode || '',
        isActive: true,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      };
      this.companies = [tempCompany, ...this.companies];
      console.log('Added missing company to list:', tempCompany);
    }

    // Always patch the form with the companyId from invoice
    this.invoiceForm.get('companyId')?.setValue(companyId);
    console.log('Patched companyId:', companyId);
  }

  populateForm(invoice: InvoiceResponse): void {
    this.invoiceForm.patchValue({
      companyId: invoice.company?.id || '',
      customerId: invoice.customer?.id || '',
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
      termsAndConditions: invoice.termsConditions,
      cgstAmount: invoice.cgstAmount,
      sgstAmount: invoice.sgstAmount,
      igstAmount: invoice.igstAmount,
      cessAmount: invoice.cessAmount,
      roundOff: invoice.roundOff,
      totalTaxableAmount: invoice.totalTaxableAmount,
      totalDiscountAmount: invoice.totalDiscountAmount,
      totalInvoiceAmount: invoice.totalInvoiceAmount,
      paidAmount: invoice.paidAmount,
      balanceAmount: invoice.balanceAmount,
      transportDetails: invoice.transportDetails,
      vehicleNumber: invoice.vehicleNumber,
      ewayBillNumber: invoice.ewayBillNumber,
      currency: invoice.currency,
      exchangeRate: invoice.exchangeRate,
      baseCurrencyAmount: invoice.baseCurrencyAmount,
      einvoiceIrn: invoice.einvoiceIrn,
      einvoiceAckNo: invoice.einvoiceAckNo,
      approvalStatus: invoice.approvalStatus,
      isRecurring: invoice.isRecurring,
      recurringFrequency: invoice.recurringFrequency,
      recurringParentId: invoice.recurringParentId,
      nextInvoiceDate: invoice.nextInvoiceDate,
      salesPersonId: invoice.salesPersonId,
      salesChannel: invoice.salesChannel,
      orderReference: invoice.orderReference,
      shippingAddress: invoice.shippingAddress,
      shippingCost: invoice.shippingCost,
      shippingTrackingNumber: invoice.shippingTrackingNumber,
      expectedDeliveryDate: invoice.expectedDeliveryDate,
      customFields: invoice.customFields
    });
    // Populate invoice items
    this.invoiceItemsArray.clear();
    invoice.invoiceItems.forEach(item => {
      const itemForm = this.createInvoiceItemForm();
      // Calculate tax rates from amounts (since backend doesn't provide rates)
      const cgstRate = item.taxableAmount > 0 ? (item.cgstAmount / item.taxableAmount) * 100 : 0;
      const sgstRate = item.taxableAmount > 0 ? (item.sgstAmount / item.taxableAmount) * 100 : 0;
      const igstRate = item.taxableAmount > 0 ? (item.igstAmount / item.taxableAmount) * 100 : 0;

      itemForm.patchValue({
        productId: item.productId,
        description: item.itemDescription,
        hsnCode: item.hsnCode,
        quantity: item.quantity,
        unit: item.unit,
        rate: item.rate,
        amount: item.quantity * item.rate,
        discountAmount: item.discountAmount,
        taxableAmount: item.taxableAmount,
        cgstRate: Math.round(cgstRate * 100) / 100, // Round to 2 decimal places
        cgstAmount: item.cgstAmount,
        sgstRate: Math.round(sgstRate * 100) / 100,
        sgstAmount: item.sgstAmount,
        igstRate: Math.round(igstRate * 100) / 100,
        igstAmount: item.igstAmount,
        cessRate: item.cessRate || 0,
        cessAmount: item.cessAmount,
        totalAmount: item.totalAmount
      });
      this.invoiceItemsArray.push(itemForm);
    });

    // Ensure companyId is properly set after form population
    if (invoice.company?.id) {
      this.invoiceForm.get('companyId')?.setValue(invoice.company.id);
      console.log('Final companyId patch after form population:', invoice.company.id);
    }

    // DO NOT disable the form, just use [readonly] in template for viewOnly
    // if (this.viewOnly) {
    //   this.invoiceForm.disable();
    // }
  }

  onSubmit(): void {
    if (this.invoiceForm.invalid) {
      this.markFormGroupTouched(this.invoiceForm);
      console.error('Form is invalid. Errors:', this.invoiceForm.errors);
      return;
    }

    this.submitting = true;
    const formValue = this.invoiceForm.value;

    // Calculate totals before submitting
    this.calculateTotals();

    // Get companyId with fallback
    const companyId = this.getCompanyIdForInvoice();

    const totalAmount = this.calculateTotalAmount();
    const totalTaxableAmount = this.calculateTotalTaxableAmount();

    // Map invoice items to match backend DTO
    const mappedInvoiceItems = formValue.invoiceItems.map((item: any) => ({
      productId: item.productId,
      itemDescription: item.description, // Backend expects 'itemDescription', not 'description'
      quantity: item.quantity,
      unit: item.unit,
      rate: item.rate,
      discountAmount: item.discountAmount || 0,
      taxableAmount: item.taxableAmount,
      cgstAmount: item.cgstAmount || 0,
      sgstAmount: item.sgstAmount || 0,
      igstAmount: item.igstAmount || 0,
      cessRate: item.cessRate || 0,
      cessAmount: item.cessAmount || 0,
      totalAmount: item.totalAmount,
      hsnCode: item.hsnCode || null
    }));

    // Calculate aggregated tax amounts from all items
    const aggregatedTaxes = this.calculateAggregatedTaxes();

    const request: InvoiceRequest = {
      companyId, // Ensure companyId is sent
      customerId: formValue.customerId,
      invoiceNumber: formValue.invoiceNumber,
      invoiceDate: formValue.invoiceDate,
      invoiceType: formValue.invoiceType,
      financialYear: formValue.financialYear,
      placeOfSupply: formValue.placeOfSupply,
      placeOfSupplyStateCode: formValue.placeOfSupplyStateCode,
      isInterState: formValue.isInterState,
      reverseChargeApplicable: formValue.reverseChargeApplicable,
      paymentType: formValue.paymentType,
      dueDate: formValue.dueDate || null,
      notes: formValue.notes || null,
      termsConditions: formValue.termsAndConditions || null, // Backend expects 'termsConditions'
      totalTaxableAmount: totalTaxableAmount,
      totalDiscountAmount: this.calculateTotalDiscountAmount(),
      cgstAmount: aggregatedTaxes.cgst,
      sgstAmount: aggregatedTaxes.sgst,
      igstAmount: aggregatedTaxes.igst,
      cessAmount: aggregatedTaxes.cess,
      totalInvoiceAmount: totalAmount,
      invoiceItems: mappedInvoiceItems
    };

    console.log('Submitting invoice request with companyId:', companyId);
    console.log('Full request payload:', request);

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
    const controlValue = this.invoiceForm.get('companyId')?.value;
    if (controlValue) {
      console.log('Using companyId from form control:', controlValue);
      return controlValue;
    }
    // Fallback to first company if form control is empty
    if (this.companies && this.companies.length > 0) {
      console.warn('companyId was empty, falling back to first company:', this.companies[0].id);
      return this.companies[0].id;
    }
    console.error('No companyId found and no companies available!');
    return '';
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

  private calculateTotalDiscountAmount(): number {
    return this.invoiceItemsArray.controls.reduce((total, item) => {
      return total + (item.get('discountAmount')?.value || 0);
    }, 0);
  }

  private calculateAggregatedTaxes(): { cgst: number, sgst: number, igst: number, cess: number } {
    return this.invoiceItemsArray.controls.reduce((totals, item) => {
      return {
        cgst: totals.cgst + (item.get('cgstAmount')?.value || 0),
        sgst: totals.sgst + (item.get('sgstAmount')?.value || 0),
        igst: totals.igst + (item.get('igstAmount')?.value || 0),
        cess: totals.cess + (item.get('cessAmount')?.value || 0)
      };
    }, { cgst: 0, sgst: 0, igst: 0, cess: 0 });
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
