-- Insert addresses for core test users
INSERT INTO ADDRESS (
    ADDRESS_GUID,
    PROVINCE,
    DISTRICT,
    MUNICIPALITY,
    WARD_NO,
    STREET_NAME,
    CREATED_TIME,
    CREATED_USER,
    MODIFIED_TIME,
    MODIFIED_USER,
    USER_GUID
) VALUES (
    -- Address for 'testuser' (Farmer)
    'addr-testuser-001',
    'Bagmati',
    'Kathmandu',
    'Kathmandu',
    '10',
    'New Road',
    '2024-12-03 15:40:09.849',
    'System',
    '2024-12-03 15:40:09.849',
    'system',
    '550e8400-e29b-41d4-a716-446655440001'
), (
    -- Address for 'user' (Buyer)
    'addr-user-099',
    'Bagmati',
    'Lalitpur',
    'Patan',
    '4',
    'Jawalakhel Marg',
    '2024-12-03 15:40:09.849',
    'System',
    '2024-12-03 15:40:09.849',
    'system',
    '550e8400-e29b-41d4-a716-446655440099'
), (
    -- Address for 'hariba' (Farmer u_f_01)
    'addr-hariba-f01',
    'Bagmati',
    'Dhading',
    'Dhading Besi',
    '3',
    'Main Road',
    '2024-12-03 15:40:09.849',
    'System',
    '2024-12-03 15:40:09.849',
    'system',
    'u_f_01'
), (
    -- Address for 'buyer' (Explicit Buyer)
    'addr-buyer-001',
    'Bagmati',
    'Bhaktapur',
    'Bhaktapur',
    '2',
    'Durbar Square Marg',
    '2024-12-03 15:40:09.849',
    'System',
    '2024-12-03 15:40:09.849',
    'system',
    'buyer'
);
