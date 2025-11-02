import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, FormArray, Validators, ReactiveFormsModule } from '@angular/forms';
import { CustomerService } from '../../../services/customer.service';
import { CompanyService } from '../../../services/company.service';
import {
  CustomerRequest,
  CustomerResponse,
  CustomerType,
  ContactPerson
} from '../../../models/customer.model';
import { CompanyResponse } from '../../../models/company.model';

@Component({
  selector: 'app-customer-form',
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './customer-form.html',
  styleUrl: './customer-form.scss',
})
export class CustomerFormComponent implements OnInit {
  customerForm: FormGroup;
  isEditMode: boolean = false;
  customerId: string | null = null;
  companies: CompanyResponse[] = [];
  loadingCompanies: boolean = false;
  loading: boolean = false;
  submitting: boolean = false;

  // Enum for template
  customerTypes = Object.values(CustomerType);

  constructor(
    private fb: FormBuilder,
    private customerService: CustomerService,
    private companyService: CompanyService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.customerForm = this.createForm();
  }

  ngOnInit(): void {
    this.customerId = this.route.snapshot.paramMap.get('id');
    this.isEditMode = !!this.customerId;

    this.loadCompanies();

    if (this.isEditMode && this.customerId) {
      this.loadCustomer(this.customerId);
    }
  }

  createForm(): FormGroup {
    return this.fb.group({
      customerName: ['', [Validators.required, Validators.maxLength(255)]],
      companyId: ['', Validators.required], // Now required with dropdown selection
      gstin: ['', [Validators.pattern('^$|^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$')]],
      customerType: [CustomerType.UNREGISTERED, Validators.required],
      pan: ['', [Validators.pattern('^$|^[A-Z]{5}[0-9]{4}[A-Z]{1}$')]],

      // Address Information
      addressLine1: ['', Validators.maxLength(500)],
      addressLine2: ['', Validators.maxLength(500)],
      city: ['', Validators.maxLength(100)],
      state: ['', Validators.maxLength(50)],
      pincode: ['', [Validators.pattern('^$|^[0-9]{6}$')]],
      country: ['India', Validators.maxLength(50)],

      // Contact Information
      phoneNumber: ['', [Validators.pattern('^$|^[0-9]{10}$')]],
      email: ['', [Validators.email, Validators.maxLength(255)]],

      // Business Information
      isActive: [true],
      creditLimit: [0, [Validators.min(0), Validators.max(999999999.99)]],
      isGstRegistered: [false],
      gstRegistrationDate: [''],

      // Billing Address
      billingAddressLine1: ['', Validators.maxLength(500)],
      billingAddressLine2: ['', Validators.maxLength(500)],
      billingCity: ['', Validators.maxLength(100)],
      billingState: ['', Validators.maxLength(50)],
      billingPincode: ['', [Validators.pattern('^$|^[0-9]{6}$')]],
      billingCountry: ['India', Validators.maxLength(50)],

      // Shipping Address
      shippingAddressLine1: ['', Validators.maxLength(500)],
      shippingAddressLine2: ['', Validators.maxLength(500)],
      shippingCity: ['', Validators.maxLength(100)],
      shippingState: ['', Validators.maxLength(50)],
      shippingPincode: ['', [Validators.pattern('^$|^[0-9]{6}$')]],
      shippingCountry: ['India', Validators.maxLength(50)],

      // Contact Persons
      contactPersons: this.fb.array([])
    });
  }

  get contactPersonsArray(): FormArray {
    return this.customerForm.get('contactPersons') as FormArray;
  }

  createContactPersonForm(): FormGroup {
    return this.fb.group({
      name: ['', [Validators.maxLength(255)]], // Remove required validation
      designation: ['', Validators.maxLength(100)],
      phoneNumber: ['', [Validators.pattern('^$|^[0-9]{10}$')]],
      email: ['', [Validators.email, Validators.maxLength(255)]]
    });
  }

  addContactPerson(): void {
    this.contactPersonsArray.push(this.createContactPersonForm());
  }

  removeContactPerson(index: number): void {
    this.contactPersonsArray.removeAt(index);
  }

  loadCustomer(id: string): void {
    this.loading = true;
    this.customerService.getCustomerById(id).subscribe({
      next: (customer: CustomerResponse) => {
        this.populateForm(customer);
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading customer:', error);
        this.loading = false;
        alert('Error loading customer. Please try again.');
      }
    });
  }

  loadCompanies(): void {
    this.loadingCompanies = true;
    this.companyService.getAllActiveCompanies().subscribe({
      next: (companies: CompanyResponse[]) => {
        this.companies = companies;
        this.loadingCompanies = false;
      },
      error: (error: any) => {
        console.error('Error loading companies:', error);
        this.loadingCompanies = false;
        alert('Error loading companies. Please try again.');
      }
    });
  }

  populateForm(customer: CustomerResponse): void {
    this.customerForm.patchValue({
      customerName: customer.customerName,
      companyId: customer.companyId,
      gstin: customer.gstin,
      customerType: customer.customerType,
      pan: customer.pan,
      addressLine1: customer.addressLine1,
      addressLine2: customer.addressLine2,
      city: customer.city,
      state: customer.state,
      pincode: customer.pincode,
      country: customer.country,
      phoneNumber: customer.phoneNumber,
      email: customer.email,
      isActive: customer.isActive,
      creditLimit: customer.creditLimit,
      isGstRegistered: customer.isGstRegistered,
      gstRegistrationDate: customer.gstRegistrationDate,
      billingAddressLine1: customer.billingAddressLine1,
      billingAddressLine2: customer.billingAddressLine2,
      billingCity: customer.billingCity,
      billingState: customer.billingState,
      billingPincode: customer.billingPincode,
      billingCountry: customer.billingCountry,
      shippingAddressLine1: customer.shippingAddressLine1,
      shippingAddressLine2: customer.shippingAddressLine2,
      shippingCity: customer.shippingCity,
      shippingState: customer.shippingState,
      shippingPincode: customer.shippingPincode,
      shippingCountry: customer.shippingCountry
    });

    // Populate contact persons
    this.contactPersonsArray.clear();
    if (customer.contactPersons) {
      customer.contactPersons.forEach(contact => {
        const contactForm = this.createContactPersonForm();
        contactForm.patchValue(contact);
        this.contactPersonsArray.push(contactForm);
      });
    }
  }

  copyAddressToBilling(): void {
    const formValue = this.customerForm.value;
    this.customerForm.patchValue({
      billingAddressLine1: formValue.addressLine1,
      billingAddressLine2: formValue.addressLine2,
      billingCity: formValue.city,
      billingState: formValue.state,
      billingPincode: formValue.pincode,
      billingCountry: formValue.country
    });
  }

  copyAddressToShipping(): void {
    const formValue = this.customerForm.value;
    this.customerForm.patchValue({
      shippingAddressLine1: formValue.addressLine1,
      shippingAddressLine2: formValue.addressLine2,
      shippingCity: formValue.city,
      shippingState: formValue.state,
      shippingPincode: formValue.pincode,
      shippingCountry: formValue.country
    });
  }

  copyBillingToShipping(): void {
    const formValue = this.customerForm.value;
    this.customerForm.patchValue({
      shippingAddressLine1: formValue.billingAddressLine1,
      shippingAddressLine2: formValue.billingAddressLine2,
      shippingCity: formValue.billingCity,
      shippingState: formValue.billingState,
      shippingPincode: formValue.billingPincode,
      shippingCountry: formValue.billingCountry
    });
  }

  onSubmit(): void {
    if (this.customerForm.invalid) {
      this.markFormGroupTouched(this.customerForm);
      return;
    }

    this.submitting = true;
    const formValue = this.customerForm.value;


    const request: CustomerRequest = formValue;

    const operation = this.isEditMode && this.customerId
      ? this.customerService.updateCustomer(this.customerId, request)
      : this.customerService.createCustomer(request);

    operation.subscribe({
      next: (response: CustomerResponse) => {
        this.submitting = false;
        alert(`Customer ${this.isEditMode ? 'updated' : 'created'} successfully!`);
        this.router.navigate(['/customers']);
      },
      error: (error) => {
        console.error('Error saving customer:', error);
        this.submitting = false;
        alert(`Error ${this.isEditMode ? 'updating' : 'creating'} customer. Please try again.`);
      }
    });
  }

  onCancel(): void {
    this.router.navigate(['/customers']);
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();

      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      } else if (control instanceof FormArray) {
        control.controls.forEach((arrayControl, index) => {
          if (arrayControl instanceof FormGroup) {
            this.markFormGroupTouched(arrayControl);
          }
        });
      }
    });
  }

  getFieldError(fieldName: string): string {
    const control = this.customerForm.get(fieldName);
    if (control && control.errors && control.touched) {
      if (control.errors['required']) return `${fieldName} is required`;
      if (control.errors['email']) return 'Invalid email format';
      if (control.errors['pattern']) return `Invalid ${fieldName} format`;
      if (control.errors['maxlength']) return `${fieldName} is too long`;
      if (control.errors['min']) return `${fieldName} cannot be negative`;
      if (control.errors['max']) return `${fieldName} is too large`;
    }
    return '';
  }

  isFieldInvalid(fieldName: string): boolean {
    const control = this.customerForm.get(fieldName);
    return !!(control && control.errors && control.touched);
  }

  // Helper method to debug form validation
  getFormErrors(): any {
    let formErrors: any = {};
    Object.keys(this.customerForm.controls).forEach(key => {
      const control = this.customerForm.get(key);
      if (control?.errors) {
        formErrors[key] = control.errors;
      }

      // Check FormArray errors
      if (control instanceof FormArray) {
        const arrayErrors: any = {};
        control.controls.forEach((arrayControl, index) => {
          if (arrayControl instanceof FormGroup) {
            const groupErrors: any = {};
            Object.keys(arrayControl.controls).forEach(groupKey => {
              const groupControl = arrayControl.get(groupKey);
              if (groupControl?.errors) {
                groupErrors[groupKey] = groupControl.errors;
              }
            });
            if (Object.keys(groupErrors).length > 0) {
              arrayErrors[index] = groupErrors;
            }
          }
        });
        if (Object.keys(arrayErrors).length > 0) {
          formErrors[key + '_array'] = arrayErrors;
        }
      }
    });
    return formErrors;
  }

  // Method to check if form is valid and log errors if not
  checkFormValidity(): boolean {
    if (this.customerForm.invalid) {
      console.log('Form is invalid. Errors:', this.getFormErrors());
      return false;
    }
    return true;
  }
}
