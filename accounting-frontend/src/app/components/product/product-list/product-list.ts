import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../../services/product.service';
import {
  Product,
  ProductSearchRequest,
  PageResponse,
  ProductType,
  ProductFilter
} from '../../../models/product.model';

@Component({
  selector: 'app-product-list',
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './product-list.html',
  styleUrl: './product-list.scss',
})
export class ProductListComponent implements OnInit {
  products: Product[] = [];
  totalElements: number = 0;
  totalPages: number = 0;
  currentPage: number = 0;
  pageSize: number = 10;
  loading: boolean = false;

  // Math for template usage
  Math = Math;

  // Filter properties
  searchTerm: string = '';
  filterCategory: string = '';
  filterProductType: ProductType | '' = '';
  filterIsActive: boolean | '' = '';
  filterIsLowStock: boolean | '' = '';
  filterHsnSacCode: string = '';

  // Sort properties
  sortBy: string = 'productName';
  sortDirection: string = 'ASC';

  // Enum for template
  productTypes = Object.values(ProductType);

  constructor(private productService: ProductService) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.loading = true;

    const filter: ProductFilter = {};
    if (this.searchTerm) filter.searchKeyword = this.searchTerm;
    if (this.filterCategory) filter.category = this.filterCategory;
    if (this.filterProductType) filter.productType = this.filterProductType as ProductType;
    if (this.filterIsActive !== '') filter.isActive = this.filterIsActive as boolean;
    if (this.filterIsLowStock !== '') filter.isLowStock = this.filterIsLowStock as boolean;
    if (this.filterHsnSacCode) filter.hsnSacCode = this.filterHsnSacCode;

    const request: ProductSearchRequest = {
      page: this.currentPage,
      size: this.pageSize,
      sortBy: this.sortBy,
      sortDirection: this.sortDirection,
      filterBy: filter
    };

    this.productService.searchProducts(request).subscribe({
      next: (response: PageResponse<Product>) => {
        this.products = response.content;
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
        this.loading = false;
      },
      error: (error: any) => {
        console.error('Error loading products:', error);
        this.loading = false;
      }
    });
  }

  onSearch(): void {
    this.currentPage = 0;
    this.loadProducts();
  }

  onClearFilters(): void {
    this.searchTerm = '';
    this.filterCategory = '';
    this.filterProductType = '';
    this.filterIsActive = '';
    this.filterIsLowStock = '';
    this.filterHsnSacCode = '';
    this.currentPage = 0;
    this.loadProducts();
  }

  onSort(column: string): void {
    if (this.sortBy === column) {
      this.sortDirection = this.sortDirection === 'ASC' ? 'DESC' : 'ASC';
    } else {
      this.sortBy = column;
      this.sortDirection = 'ASC';
    }
    this.currentPage = 0;
    this.loadProducts();
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.loadProducts();
  }

  onPageSizeChange(): void {
    this.currentPage = 0;
    this.loadProducts();
  }

  toggleProductStatus(product: Product): void {
    if (!product.id) {
      console.error('Product ID is required');
      return;
    }

    const newStatus = !product.isActive;
    this.productService.updateProductStatus(product.id, newStatus).subscribe({
      next: (updatedProduct) => {
        const index = this.products.findIndex(p => p.id === product.id);
        if (index !== -1) {
          this.products[index] = updatedProduct;
        }
      },
      error: (error) => {
        console.error('Error updating product status:', error);
      }
    });
  }

  deleteProduct(product: Product): void {
    if (!product.id) {
      console.error('Product ID is required');
      return;
    }

    if (confirm(`Are you sure you want to delete product "${product.productName}"?`)) {
      this.productService.deleteProduct(product.id).subscribe({
        next: () => {
          this.loadProducts();
        },
        error: (error) => {
          console.error('Error deleting product:', error);
          alert('Error deleting product. Please try again.');
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

  getProfitMarginClass(profitPercentage: number | undefined): string {
    if (!profitPercentage) return '';

    if (profitPercentage < 10) return 'text-danger';
    if (profitPercentage < 25) return 'text-warning';
    return 'text-success';
  }

  getStockLevelClass(product: Product): string {
    if (product.isLowStock) return 'badge-danger';
    if (product.currentStock && product.currentStock > (product.reorderLevel || 0) * 2) return 'badge-success';
    return 'badge-warning';
  }

  formatCurrency(amount: number | undefined): string {
    if (!amount) return '₹0.00';
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR'
    }).format(amount);
  }

  formatNumber(value: number | undefined): string {
    if (!value) return '0';
    return new Intl.NumberFormat('en-IN').format(value);
  }
}
