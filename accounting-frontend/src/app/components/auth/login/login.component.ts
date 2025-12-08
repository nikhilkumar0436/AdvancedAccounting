import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { LoginRequest } from '../../../models/auth.model';

@Component({
  selector: 'app-login',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  loading = false;
  errorMessage = '';

  sampleCredentials = [
    { username: 'admin', password: 'admin123', description: 'Admin User' },
    { username: 'user', password: 'user123', description: 'Regular User' },
    { username: 'demo', password: 'demo123', description: 'Demo User' }
  ];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  ngOnInit(): void {
    // If already authenticated, redirect to company selection or dashboard
    if (this.authService.isAuthenticated()) {
      if (this.authService.getSelectedCompany()) {
        this.router.navigate(['/dashboard']);
      } else {
        this.router.navigate(['/select-company']);
      }
    }
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      this.loading = true;
      this.errorMessage = '';

      // Extract and normalize credentials
      let rawUsername = (this.loginForm.get('username')?.value || '').toString().trim();
      let rawPassword = (this.loginForm.get('password')?.value || '').toString().trim();

      // Parse combined credentials if user pasted them into username field
      const parsed = this.parseCombinedCredentials(rawUsername, rawPassword);
      rawUsername = parsed.username;
      rawPassword = parsed.password;

      console.log('Submitting login with username:', rawUsername);

      const credentials: LoginRequest = {
        username: rawUsername,
        password: rawPassword
      };

      this.authService.login(credentials).subscribe({
        next: (response: any) => {
          this.loading = false;
          console.log('Login successful:', response);

          // Navigate to company selection
          this.router.navigate(['/select-company']);
        },
        error: (error: any) => {
          this.loading = false;
          console.error('Login error from service:', error);
          this.errorMessage = error?.message || 'Invalid username or password';
        }
      });
    } else {
      this.markFormGroupTouched();
    }
  }

  // Helper: parse combined credential strings like "admin / admin123" or "admin admin123"
  private parseCombinedCredentials(rawUsername: string, rawPassword: string): { username: string; password: string } {
    let username = rawUsername;
    let password = rawPassword;

    if (!password) {
      // split by slash first
      const slashParts: string[] = rawUsername.split('/').map((s: string) => s.trim()).filter((s: string) => s.length > 0);
      if (slashParts.length === 2) {
        username = slashParts[0];
        password = slashParts[1];
        return { username, password };
      }

      // fallback: split by whitespace
      const spaceParts: string[] = rawUsername.split(/\s+/).map((s: string) => s.trim()).filter((s: string) => s.length > 0);
      if (spaceParts.length === 2) {
        username = spaceParts[0];
        password = spaceParts[1];
      }
    }

    return { username, password };
  }

  fillSampleCredentials(credential: any): void {
    this.loginForm.patchValue({
      username: credential.username,
      password: credential.password
    });
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.loginForm.get(fieldName);
    return field ? field.invalid && (field.dirty || field.touched) : false;
  }

  getFieldError(fieldName: string): string {
    const field = this.loginForm.get(fieldName);
    if (field?.errors && (field.dirty || field.touched)) {
      if (field.errors['required']) return `${fieldName} is required`;
      if (field.errors['minlength']) return `${fieldName} must be at least ${field.errors['minlength'].requiredLength} characters`;
    }
    return '';
  }

  private markFormGroupTouched(): void {
    Object.keys(this.loginForm.controls).forEach(key => {
      const control = this.loginForm.get(key);
      control?.markAsTouched();
    });
  }
}
