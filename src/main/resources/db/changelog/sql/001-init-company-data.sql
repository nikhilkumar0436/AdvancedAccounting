--liquibase formatted sql

--changeset system:001-init-company-data
--comment: Initialize company_master table with sample data

-- Insert default company data
INSERT INTO company_master (
    id, company_name, gstin, pan, address_line1, address_line2, city, state,
    state_code, pincode, email, phone, mobile, website, financial_year_start,
    financial_year_end, invoice_prefix, terms_and_conditions, bank_name,
    bank_account_number, bank_ifsc_code, bank_branch, base_currency,
    currency_symbol, company_type, subscription_plan, subscription_valid_until,
    max_users, max_branches, is_active, created_at, updated_at
) VALUES (
    gen_random_uuid(),
    'Demo Company Pvt Ltd',
    '29ABCDE1234F1Z5',
    'ABCDE1234F',
    '123, Business District',
    'Tech Park, Phase 1',
    'Bangalore',
    'Karnataka',
    '29',
    '560001',
    'info@democompany.com',
    '080-12345678',
    '9876543210',
    'https://www.democompany.com',
    '2024-04-01'::date,
    '2025-03-31'::date,
    'DC',
    'Standard payment terms apply'::text,
    'State Bank of India',
    '1234567890123456',
    'SBIN0001234',
    'MG Road Branch',
    'INR',
    '₹',
    'HEAD_OFFICE'::company_type,
    'PROFESSIONAL',
    '2025-12-31'::date,
    10,
    5,
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (gstin) DO NOTHING;

-- Insert a second company for testing multi-company scenarios
INSERT INTO company_master (
    id, company_name, gstin, pan, address_line1, address_line2, city, state,
    state_code, pincode, email, phone, mobile, website, financial_year_start,
    financial_year_end, invoice_prefix, terms_and_conditions, bank_name,
    bank_account_number, bank_ifsc_code, bank_branch, base_currency,
    currency_symbol, company_type, subscription_plan, subscription_valid_until,
    max_users, max_branches, is_active, created_at, updated_at
) VALUES (
    gen_random_uuid(),
    'Tech Solutions Pvt Ltd',
    '29XYZAB5678G2H9',
    'XYZAB5678G',
    '456, IT Corridor',
    'Software Park, Block B',
    'Hyderabad',
    'Telangana',
    '36',
    '500001',
    'contact@techsolutions.com',
    '040-87654321',
    '9123456789',
    'https://www.techsolutions.com',
    '2024-04-01'::date,
    '2025-03-31'::date,
    'TS',
    'Fast payment preferred'::text,
    'HDFC Bank',
    '9876543210987654',
    'HDFC0001234',
    'Hitech City Branch',
    'INR',
    '₹',
    'HEAD_OFFICE'::company_type,
    'ENTERPRISE',
    '2025-12-31'::date,
    50,
    20,
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (gstin) DO NOTHING;

--rollback DELETE FROM company_master WHERE gstin IN ('29ABCDE1234F1Z5', '29XYZAB5678G2H9');
