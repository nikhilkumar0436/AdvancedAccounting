import { CompanyResponse } from './company.model';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  user: User;
  companies: CompanyResponse[];
}

export interface User {
  id: string;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
}


export interface AuthState {
  isAuthenticated: boolean;
  token: string | null;
  user: User | null;
  selectedCompany: CompanyResponse | null;
}

export type { CompanyResponse };

