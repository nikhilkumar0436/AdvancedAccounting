import { Routes } from '@angular/router';
import { DashboardComponent } from './components/dashboard/dashboard';
import { CustomerListComponent } from './components/customer/customer-list/customer-list';
import { CustomerFormComponent } from './components/customer/customer-form/customer-form';
import { InvoiceListComponent } from './components/invoice/invoice-list/invoice-list';
import { InvoiceFormComponent } from './components/invoice/invoice-form/invoice-form';

export const routes: Routes = [
  { path: '', component: DashboardComponent },
  { path: 'dashboard', component: DashboardComponent },

  // Customer routes
  { path: 'customers', component: CustomerListComponent },
  { path: 'customers/new', component: CustomerFormComponent },
  { path: 'customers/:id', component: CustomerFormComponent },
  { path: 'customers/:id/edit', component: CustomerFormComponent },

  // Invoice routes
  { path: 'invoices', component: InvoiceListComponent },
  { path: 'invoices/new', component: InvoiceFormComponent },
  { path: 'invoices/:id', component: InvoiceFormComponent },
  { path: 'invoices/:id/edit', component: InvoiceFormComponent },

  // Wildcard route
  { path: '**', redirectTo: '/dashboard' }
];
