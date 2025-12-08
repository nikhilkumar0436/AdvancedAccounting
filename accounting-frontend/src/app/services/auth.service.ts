import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { map, tap, catchError } from 'rxjs/operators';
import { LoginRequest, LoginResponse, AuthState } from '../models/auth.model';
import { CompanyResponse } from '../models/company.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = 'http://localhost:8080/api/v1';
  private readonly STORAGE_KEYS = {
    AUTH_TOKEN: 'auth_token',
    USER_DATA: 'user_data',
    SELECTED_COMPANY: 'selected_company'
  };

  private authState = new BehaviorSubject<AuthState>({
    isAuthenticated: false,
    token: null,
    user: null,
    selectedCompany: null
  });

  public authState$ = this.authState.asObservable();

  constructor(private http: HttpClient) {
    this.loadAuthStateFromStorage();
  }

  // Sample credentials for demo
  private sampleCredentials = [
    { username: 'admin', password: 'admin123' },
    { username: 'user', password: 'user123' },
    { username: 'demo', password: 'demo123' }
  ];

  // Sample fallback companies in case backend is unavailable
  private fallbackCompanies: CompanyResponse[] = [
    {
      id: '21c25ad7-fa40-4f0c-821c-b376bea6ce74',
      companyName: 'Demo Company Pvt Ltd',
      gstin: '29ABCDE1234F1Z5',
      pan: 'ABCDE1234F',
      addressLine1: '',
      addressLine2: '',
      city: 'Bangalore',
      state: 'Karnataka',
      stateCode: 'KA',
      pincode: '560001',
      isActive: true,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    }
  ];


  login(credentials: LoginRequest): Observable<LoginResponse> {
    // Normalize inputs
    const username = (credentials.username || '').toString().trim();
    const password = (credentials.password || '').toString().trim();

    console.log('Attempting login for user:', username);

    // Simulate API call with sample credentials
    const isValidCredential = this.sampleCredentials.some(
      cred => cred.username.toLowerCase() === username.toLowerCase() && cred.password === password
    );

    console.log('Credential check result:', isValidCredential);

    if (isValidCredential) {
      // Load companies from backend
      return this.loadCompaniesFromBackend().pipe(
        map(companies => {
          const mockResponse: LoginResponse = {
            token: 'mock-jwt-token-' + Date.now(),
            user: {
              id: 'user-' + username,
              username: username,
              email: username + '@company.com',
              firstName: username.charAt(0).toUpperCase() + username.slice(1),
              lastName: 'User'
            },
            companies: companies
          };

          this.setAuthData(mockResponse.token, mockResponse.user);
          return mockResponse;
        }),
        catchError(err => {
          console.error('Failed to load companies from backend during login:', err);
          // Provide clearer error message to the caller
          return throwError(() => new Error('Unable to load companies from server. Please try again later.'));
        })
      );
    } else {
      console.warn('Invalid login attempt for user:', username);
      // Return an Observable error rather than throwing synchronously
      return new Observable<LoginResponse>(subscriber => {
        subscriber.error(new Error('Invalid credentials'));
      });
    }
  }

  private loadCompaniesFromBackend(): Observable<CompanyResponse[]> {
    return this.http.get<CompanyResponse[]>(`${this.API_URL}/companies/active`).pipe(
      catchError(err => {
        console.warn('Could not load companies from backend, falling back to default:', err);
        return of(this.fallbackCompanies);
      })
    );
  }

  selectCompany(company: CompanyResponse): void {
    const currentState = this.authState.value;
    const newState: AuthState = {
      ...currentState,
      selectedCompany: company
    };

    this.authState.next(newState);
    localStorage.setItem(this.STORAGE_KEYS.SELECTED_COMPANY, JSON.stringify(company));

    // Set as environment variable (stored in localStorage for persistence)
    localStorage.setItem('SELECTED_COMPANY_ID', company.id);
    localStorage.setItem('SELECTED_COMPANY_NAME', company.companyName);

    console.log('Company selected and stored:', company);
  }

  getSelectedCompany(): CompanyResponse | null {
    return this.authState.value.selectedCompany;
  }

  getSelectedCompanyId(): string | null {
    const company = this.getSelectedCompany();
    return company ? company.id : localStorage.getItem('SELECTED_COMPANY_ID');
  }

  logout(): void {
    localStorage.removeItem(this.STORAGE_KEYS.AUTH_TOKEN);
    localStorage.removeItem(this.STORAGE_KEYS.USER_DATA);
    localStorage.removeItem(this.STORAGE_KEYS.SELECTED_COMPANY);
    localStorage.removeItem('SELECTED_COMPANY_ID');
    localStorage.removeItem('SELECTED_COMPANY_NAME');

    this.authState.next({
      isAuthenticated: false,
      token: null,
      user: null,
      selectedCompany: null
    });
  }

  isAuthenticated(): boolean {
    return this.authState.value.isAuthenticated;
  }

  getToken(): string | null {
    return this.authState.value.token;
  }

  getUser(): any {
    return this.authState.value.user;
  }

  private setAuthData(token: string, user: any): void {
    localStorage.setItem(this.STORAGE_KEYS.AUTH_TOKEN, token);
    localStorage.setItem(this.STORAGE_KEYS.USER_DATA, JSON.stringify(user));

    const currentState = this.authState.value;
    this.authState.next({
      ...currentState,
      isAuthenticated: true,
      token: token,
      user: user
    });
  }

  private loadAuthStateFromStorage(): void {
    const token = localStorage.getItem(this.STORAGE_KEYS.AUTH_TOKEN);
    const userData = localStorage.getItem(this.STORAGE_KEYS.USER_DATA);
    const selectedCompany = localStorage.getItem(this.STORAGE_KEYS.SELECTED_COMPANY);

    if (token && userData) {
      const user = JSON.parse(userData);
      const company = selectedCompany ? JSON.parse(selectedCompany) : null;

      this.authState.next({
        isAuthenticated: true,
        token: token,
        user: user,
        selectedCompany: company
      });
    }
  }

  // Helper method to get companies for selection
  getAvailableCompanies(): Observable<CompanyResponse[]> {
    return this.loadCompaniesFromBackend();
  }
}
