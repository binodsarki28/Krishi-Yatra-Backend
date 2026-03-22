-- Insert addresses for core test users
INSERT INTO ADDRESS (
    ADDRESS_GUID,
    PROVINCE,
    DISTRICT,
    MUNICIPALITY,
    CITY,
    WARD_NO,
    STREET_NAME,
    OTHER,
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
    'Kathmandu Metropolitan City',
    'Kathmandu',
    '10',
    'New Road',
    'House 25',
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
    'Lalitpur Metropolitan City',
    'Patan',
    '4',
    'Jawalakhel Marg',
    'Near Zoo Gate',
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
    'Dhading Besi Municipality',
    'Dhading Besi',
    '3',
    'Main Road',
    'Near Hospital',
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
    'Bhaktapur Municipality',
    'Bhaktapur',
    '2',
    'Durbar Square Marg',
    'Home Shop',
    '2024-12-03 15:40:09.849',
    'System',
    '2024-12-03 15:40:09.849',
    'system',
    'buyer'
);
