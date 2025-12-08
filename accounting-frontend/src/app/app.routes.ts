import { Routes } from '@angular/router';
import { DashboardComponent } from './components/dashboard/dashboard';
import { CustomerListComponent } from './components/customer/customer-list/customer-list';
import { CustomerFormComponent } from './components/customer/customer-form/customer-form';
import { ProductListComponent } from './components/product/product-list/product-list';
import { ProductFormComponent } from './components/product/product-form/product-form';
import { InvoiceListComponent } from './components/invoice/invoice-list/invoice-list';
import { InvoiceFormComponent } from './components/invoice/invoice-form/invoice-form';
import { LoginComponent } from './components/auth/login/login.component';
import { SelectCompanyComponent } from './components/auth/select-company/select-company.component';
import { AuthGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'select-company', component: SelectCompanyComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] },

  // Customer routes
  { path: 'customers', component: CustomerListComponent, canActivate: [AuthGuard] },
  { path: 'customers/new', component: CustomerFormComponent, canActivate: [AuthGuard] },
  { path: 'customers/:id', component: CustomerFormComponent, canActivate: [AuthGuard] },
  { path: 'customers/:id/edit', component: CustomerFormComponent, canActivate: [AuthGuard] },

  // Product routes
  { path: 'products', component: ProductListComponent, canActivate: [AuthGuard] },
  { path: 'products/new', component: ProductFormComponent, canActivate: [AuthGuard] },
  { path: 'products/:id', component: ProductFormComponent, canActivate: [AuthGuard] },
  { path: 'products/:id/edit', component: ProductFormComponent, canActivate: [AuthGuard] },

  // Invoice routes
  { path: 'invoices', component: InvoiceListComponent, canActivate: [AuthGuard] },
  { path: 'invoices/new', component: InvoiceFormComponent, canActivate: [AuthGuard] },
  { path: 'invoices/:id', component: InvoiceFormComponent, canActivate: [AuthGuard] },
  { path: 'invoices/:id/edit', component: InvoiceFormComponent, canActivate: [AuthGuard] },

  { path: '**', redirectTo: '/login' }
];
