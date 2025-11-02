--liquibase formatted sql

--changeset system:002-add-sample-companies
--comment: Add sample companies for customer selection

-- Insert sample companies with specific UUIDs for frontend selection
INSERT INTO company_master (
    id, company_name, gstin, pan, address_line1, address_line2, city, state,
    state_code, pincode, email, phone, mobile, website, financial_year_start,
    financial_year_end, invoice_prefix, terms_and_conditions, bank_name,
    bank_account_number, bank_ifsc_code, bank_branch, base_currency,
    currency_symbol, company_type, subscription_plan, subscription_valid_until,
    max_users, max_branches, is_active, created_at, updated_at
) VALUES (
    '11111111-1111-1111-1111-111111111111'::uuid,
    'Default Company Ltd',
    '29AAPFU0939F1ZV',
    'AAPFU0939F',
    '123 Business Street',
    'Tech Hub',
    'Bangalore',
    'Karnataka',
    '29',
    '560001',
    'info@defaultcompany.com',
    '080-12345678',
    '9876543210',
    'https://www.defaultcompany.com',
    '2024-04-01'::date,
    '2025-03-31'::date,
    'DEF',
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
) ON CONFLICT (id) DO NOTHING;

-- Insert another sample company
INSERT INTO company_master (
    id, company_name, gstin, pan, address_line1, address_line2, city, state,
    state_code, pincode, email, phone, mobile, website, financial_year_start,
    financial_year_end, invoice_prefix, terms_and_conditions, bank_name,
    bank_account_number, bank_ifsc_code, bank_branch, base_currency,
    currency_symbol, company_type, subscription_plan, subscription_valid_until,
    max_users, max_branches, is_active, created_at, updated_at
) VALUES (
    '22222222-2222-2222-2222-222222222222'::uuid,
    'Sample Industries Pvt Ltd',
    '36AABFU0123G1ZX',
    'AABFU0123G',
    '456 Industrial Area',
    'Manufacturing Zone',
    'Hyderabad',
    'Telangana',
    '36',
    '500032',
    'contact@sampleindustries.com',
    '040-87654321',
    '9123456789',
    'https://www.sampleindustries.com',
    '2024-04-01'::date,
    '2025-03-31'::date,
    'SMP',
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
) ON CONFLICT (id) DO NOTHING;

--rollback DELETE FROM company_master WHERE id IN ('11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222');
