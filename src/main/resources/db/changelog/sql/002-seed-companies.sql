-- Add default company for testing
INSERT INTO company_master (
    id,
    company_name,
    gstin,
    pan,
    address_line1,
    city,
    state,
    state_code,
    pincode,
    is_active,
    created_at,
    updated_at
) VALUES (
    '11111111-1111-1111-1111-111111111111'::uuid,
    'Default Company Ltd.',
    '29AAPFU0939F1ZV',
    'AAPFU0939F',
    '123 Business Street',
    'Bangalore',
    'Karnataka',
    '29',
    '560001',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (id) DO NOTHING;

-- Add another sample company
INSERT INTO company_master (
    id,
    company_name,
    gstin,
    pan,
    address_line1,
    city,
    state,
    state_code,
    pincode,
    is_active,
    created_at,
    updated_at
) VALUES (
    '22222222-2222-2222-2222-222222222222'::uuid,
    'Sample Industries Pvt Ltd',
    '36AABFU0123G1ZX',
    'AABFU0123G',
    '456 Industrial Area',
    'Hyderabad',
    'Telangana',
    '36',
    '500032',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (id) DO NOTHING;
