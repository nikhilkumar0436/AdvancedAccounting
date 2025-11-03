import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ProductFormComponent } from './product-form';
import { ProductService } from '../../../services/product.service';
import { Product, ProductType } from '../../../models/product.model';

describe('ProductFormComponent', () => {
  let component: ProductFormComponent;
  let fixture: ComponentFixture<ProductFormComponent>;
  let productService: jasmine.SpyObj<ProductService>;

  const mockProduct: Product = {
    id: '1',
    productName: 'Test Product',
    productCode: 'TEST001',
    hsnSacCode: '1234',
    productType: ProductType.GOODS,
    sellingPrice: 100,
    isActive: true
  };

  beforeEach(async () => {
    const productServiceSpy = jasmine.createSpyObj('ProductService', [
      'getProductById',
      'createProduct',
      'updateProduct'
    ]);

    await TestBed.configureTestingModule({
      imports: [
        ProductFormComponent,
        ReactiveFormsModule,
        RouterTestingModule,
        HttpClientTestingModule
      ],
      providers: [
        { provide: ProductService, useValue: productServiceSpy },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: { params: {} }
          }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProductFormComponent);
    component = fixture.componentInstance;
    productService = TestBed.inject(ProductService) as jasmine.SpyObj<ProductService>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form with default values', () => {
    component.ngOnInit();

    expect(component.productForm.get('productName')?.value).toBe('');
    expect(component.productForm.get('productType')?.value).toBe(ProductType.GOODS);
    expect(component.productForm.get('unitOfMeasurement')?.value).toBe('NOS');
    expect(component.productForm.get('isActive')?.value).toBe(true);
  });

  it('should load product data in edit mode', () => {
    component.productId = '1';
    component.isEditMode = true;
    productService.getProductById.and.returnValue(of(mockProduct));

    component.ngOnInit();

    expect(productService.getProductById).toHaveBeenCalledWith('1');
  });

  it('should validate required fields', () => {
    component.productForm.patchValue({
      productName: '',
      hsnSacCode: '',
      sellingPrice: null
    });

    expect(component.isFieldInvalid('productName')).toBeTruthy();
    expect(component.isFieldInvalid('hsnSacCode')).toBeTruthy();
    expect(component.isFieldInvalid('sellingPrice')).toBeTruthy();
  });

  it('should call create product service on form submission for new product', () => {
    component.isEditMode = false;
    component.productForm.patchValue({
      productName: 'New Product',
      hsnSacCode: '1234',
      sellingPrice: 100
    });

    productService.createProduct.and.returnValue(of(mockProduct));

    component.onSubmit();

    expect(productService.createProduct).toHaveBeenCalled();
  });

  it('should call update product service on form submission for existing product', () => {
    component.isEditMode = true;
    component.productId = '1';
    component.productForm.patchValue({
      productName: 'Updated Product',
      hsnSacCode: '1234',
      sellingPrice: 100
    });

    productService.updateProduct.and.returnValue(of(mockProduct));

    component.onSubmit();

    expect(productService.updateProduct).toHaveBeenCalledWith('1', jasmine.any(Object));
  });
});
