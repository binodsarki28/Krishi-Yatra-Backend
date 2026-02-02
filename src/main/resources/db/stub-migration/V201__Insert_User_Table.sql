INSERT INTO USERS (
    USER_GUID,
    FULL_NAME,
    USERNAME,
    EMAIL,
    PASSWORD,
    PHONE_NUMBER,
    IS_ACTIVE,
    CREATED_TIME,
    CREATED_USER,
    MODIFIED_TIME,
    MODIFIED_USER
) VALUES (
             '550e8400-e29b-41d4-a716-446655440001',
             'Test User',
             'testuser',
             'test@gmail.com',
             '$2a$10$hashed_password',
             '9800000000',
             TRUE,
             '2024-12-03 15:40:09.849',
             'System',
             '2024-12-03 15:40:09.849',
             'system'
         );
