import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ProductService } from '../../../services/product.service';
import { CompanyService } from '../../../services/company.service';
import { ProductRequest, ProductType } from '../../../models/product.model';
import { CompanyResponse } from '../../../models/company.model';

@Component({
  selector: 'app-product-form',
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './product-form.html',
  styleUrl: './product-form.scss',
})
export class ProductFormComponent implements OnInit {
  productForm: FormGroup;
  isEditMode = false;
  productId?: string;
  loading = false;
  companies: CompanyResponse[] = [];
  loadingCompanies: boolean = false;

  productTypes = Object.values(ProductType);

  constructor(
    private fb: FormBuilder,
    private productService: ProductService,
    private companyService: CompanyService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.productForm = this.createProductForm();
  }

  ngOnInit(): void {
    this.productId = this.route.snapshot.params['id'];
    this.isEditMode = !!this.productId;

    this.loadCompanies();

    if (this.isEditMode && this.productId) {
      this.loadProduct(this.productId);
    }
  }

  private createProductForm(): FormGroup {
    return this.fb.group({
      productName: ['', [Validators.required, Validators.maxLength(255)]],
      companyId: ['', Validators.required], // Added company selection
      productCode: ['', [Validators.maxLength(50)]],
      hsnSacCode: ['', [Validators.required, Validators.maxLength(10)]],
      productType: [ProductType.GOODS, [Validators.required]],
      unitOfMeasurement: ['NOS', [Validators.maxLength(20)]],
      gstRate: [0, [Validators.required, Validators.min(0), Validators.max(100)]],
      cessRate: [0, [Validators.min(0), Validators.max(100)]],
      purchasePrice: [0, [Validators.min(0)]],
      sellingPrice: [0, [Validators.required, Validators.min(0)]],
      mrp: [0, [Validators.min(0)]],
      description: ['', [Validators.maxLength(1000)]],
      category: ['', [Validators.maxLength(100)]],
      openingStock: [0, [Validators.min(0)]],
      currentStock: [0, [Validators.min(0)]],
      reorderLevel: [0, [Validators.min(0)]],
      baseCurrency: ['INR'],
      barcode: [''],
      sku: [''],
      batchTrackingEnabled: [false],
      serialTrackingEnabled: [false],
      expiryTrackingEnabled: [false],
      warrantyPeriodDays: [0, [Validators.min(0)]],
      discountApplicable: [true],
      maxDiscountPercentage: [100, [Validators.min(0), Validators.max(100)]],
      isActive: [true]
    });
  }

  private loadProduct(id: string): void {
    this.loading = true;
    this.productService.getProductById(id).subscribe({
      next: (product) => {
        this.productForm.patchValue({
          productName: product.productName,
          companyId: product.companyId,
          productCode: product.productCode,
          hsnSacCode: product.hsnSacCode,
          productType: product.productType,
          unitOfMeasurement: product.unitOfMeasurement,
          gstRate: product.gstRate,
          cessRate: product.cessRate,
          purchasePrice: product.purchasePrice,
          sellingPrice: product.sellingPrice,
          mrp: product.mrp,
          description: product.description,
          category: product.category,
          openingStock: product.openingStock,
          currentStock: product.currentStock,
          reorderLevel: product.reorderLevel,
          baseCurrency: product.baseCurrency,
          barcode: product.barcode,
          sku: product.sku,
          batchTrackingEnabled: product.batchTrackingEnabled,
          serialTrackingEnabled: product.serialTrackingEnabled,
          expiryTrackingEnabled: product.expiryTrackingEnabled,
          warrantyPeriodDays: product.warrantyPeriodDays,
          discountApplicable: product.discountApplicable,
          maxDiscountPercentage: product.maxDiscountPercentage,
          isActive: product.isActive
        });
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading product:', error);
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.productForm.valid) {
      this.loading = true;
      const request: ProductRequest = {
        ...this.productForm.value
      };

      const operation = this.isEditMode
        ? this.productService.updateProduct(this.productId!, request)
        : this.productService.createProduct(request);

      operation.subscribe({
        next: (product) => {
          console.log('Product saved:', product);
          this.router.navigate(['/products']);
        },
        error: (error) => {
          console.error('Error saving product:', error);
          this.loading = false;
          alert('Error saving product. Please try again.');
        }
      });
    } else {
      this.markFormGroupTouched();
    }
  }

  onCancel(): void {
    this.router.navigate(['/products']);
  }

  private markFormGroupTouched(): void {
    Object.keys(this.productForm.controls).forEach(key => {
      const control = this.productForm.get(key);
      control?.markAsTouched();
    });
  }

  get formControls() {
    return this.productForm.controls;
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.productForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  getFieldError(fieldName: string): string {
    const field = this.productForm.get(fieldName);
    if (field?.errors) {
      if (field.errors['required']) return `${fieldName} is required`;
      if (field.errors['maxLength']) return `${fieldName} is too long`;
      if (field.errors['min']) return `${fieldName} must be non-negative`;
      if (field.errors['max']) return `${fieldName} value is too high`;
    }
    return '';
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
}
