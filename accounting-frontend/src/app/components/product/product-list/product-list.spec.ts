import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { FormsModule } from '@angular/forms';
import { of } from 'rxjs';

import { ProductListComponent } from './product-list';
import { ProductService } from '../../../services/product.service';
import { Product, ProductType, PageResponse } from '../../../models/product.model';

describe('ProductListComponent', () => {
  let component: ProductListComponent;
  let fixture: ComponentFixture<ProductListComponent>;
  let productService: jasmine.SpyObj<ProductService>;

  const mockProducts: Product[] = [
    {
      id: '1',
      productName: 'Test Product 1',
      productCode: 'TEST001',
      hsnSacCode: '1234',
      productType: ProductType.GOODS,
      sellingPrice: 100,
      currentStock: 50,
      isActive: true
    },
    {
      id: '2',
      productName: 'Test Product 2',
      productCode: 'TEST002',
      hsnSacCode: '5678',
      productType: ProductType.SERVICES,
      sellingPrice: 200,
      currentStock: 25,
      isActive: false
    }
  ];

  const mockPageResponse: PageResponse<Product> = {
    content: mockProducts,
    totalElements: 2,
    totalPages: 1,
    size: 10,
    number: 0,
    first: true,
    last: true,
    numberOfElements: 2
  };

  beforeEach(async () => {
    const productServiceSpy = jasmine.createSpyObj('ProductService', [
      'searchProducts',
      'updateProductStatus',
      'deleteProduct'
    ]);

    await TestBed.configureTestingModule({
      imports: [
        ProductListComponent,
        HttpClientTestingModule,
        RouterTestingModule,
        FormsModule
      ],
      providers: [
        { provide: ProductService, useValue: productServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProductListComponent);
    component = fixture.componentInstance;
    productService = TestBed.inject(ProductService) as jasmine.SpyObj<ProductService>;

    productService.searchProducts.and.returnValue(of(mockPageResponse));
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load products on init', () => {
    component.ngOnInit();
    expect(productService.searchProducts).toHaveBeenCalled();
    expect(component.products.length).toBe(2);
    expect(component.totalElements).toBe(2);
  });

  it('should search products when onSearch is called', () => {
    component.searchTerm = 'Test';
    component.onSearch();
    expect(productService.searchProducts).toHaveBeenCalled();
    expect(component.currentPage).toBe(0);
  });

  it('should clear filters when onClearFilters is called', () => {
    component.searchTerm = 'Test';
    component.filterCategory = 'Electronics';
    component.onClearFilters();

    expect(component.searchTerm).toBe('');
    expect(component.filterCategory).toBe('');
    expect(component.currentPage).toBe(0);
  });

  it('should change sort direction when sorting by same column', () => {
    component.sortBy = 'productName';
    component.sortDirection = 'ASC';

    component.onSort('productName');

    expect(component.sortDirection).toBe('DESC');
  });

  it('should format currency correctly', () => {
    const result = component.formatCurrency(1000);
    expect(result).toContain('₹');
    expect(result).toContain('1,000');
  });

  it('should get correct profit margin class', () => {
    expect(component.getProfitMarginClass(5)).toBe('text-danger');
    expect(component.getProfitMarginClass(15)).toBe('text-warning');
    expect(component.getProfitMarginClass(30)).toBe('text-success');
  });
});
