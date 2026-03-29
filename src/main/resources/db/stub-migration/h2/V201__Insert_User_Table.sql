-- Stub: Insert test user (testuser)
INSERT INTO USERS (USER_GUID, FULL_NAME, USERNAME, EMAIL, PASSWORD, PHONE_NUMBER, IS_ACTIVE, CREATED_TIME, CREATED_USER, MODIFIED_TIME, MODIFIED_USER)
VALUES ('550e8400-e29b-41d4-a716-446655440001', 'Test User', 'testuser', 'test@gmail.com',
        '$2a$10$hashed_password', '9800000000', TRUE,
        '2024-12-03 15:40:09.849', 'System', '2024-12-03 15:40:09.849', 'system');

-- Stub: Insert normal user (user / user123) - password is bcrypt of 'user123'
INSERT INTO USERS (USER_GUID, FULL_NAME, USERNAME, EMAIL, PASSWORD, PHONE_NUMBER, IS_ACTIVE, CREATED_TIME, CREATED_USER, MODIFIED_TIME, MODIFIED_USER)
VALUES ('550e8400-e29b-41d4-a716-446655440099', 'Normal User', 'user', 'user@example.com',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '9811111111', TRUE,
        '2024-12-03 15:40:09.849', 'System', '2024-12-03 15:40:09.849', 'system');

-- Explicit Buyer and Delivery for automated testing
INSERT INTO USERS (USER_GUID, FULL_NAME, USERNAME, EMAIL, PASSWORD, PHONE_NUMBER, IS_ACTIVE) VALUES
('buyer', 'Verified Buyer', 'buyer', 'buyer@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '9888888888', TRUE),
('delivery', 'Verified Delivery', 'delivery', 'delivery@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '9899999999', TRUE);

-- Adding 45 dummy users for each role (15 Farmers, 15 Buyers, 15 Delivery)
-- Farmer Users (f_user_1 to f_user_15)
INSERT INTO USERS (USER_GUID, FULL_NAME, USERNAME, EMAIL, PASSWORD, PHONE_NUMBER, IS_ACTIVE) VALUES
('u_f_01', 'Hari Bahadur', 'hariba', 'hari@test.com', 'none', '9841000001', TRUE),
('u_f_02', 'Sita Devi', 'sitadevi', 'sita@test.com', 'none', '9841000002', TRUE),
('u_f_03', 'Ram Prasad', 'ramprasad', 'ram@test.com', 'none', '9841000003', TRUE),
('u_f_04', 'Gita Kumari', 'gitak', 'gita@test.com', 'none', '9841000004', TRUE),
('u_f_05', 'Shyam Lal', 'shyam', 'shyam@test.com', 'none', '9841000005', TRUE),
('u_f_06', 'Krishna Dev', 'krishna', 'krishna@test.com', 'none', '9841000006', TRUE),
('u_f_07', 'Maya Thapa', 'mayat', 'maya@test.com', 'none', '9841000007', TRUE),
('u_f_08', 'Bishal Rai', 'bishal', 'bishal@test.com', 'none', '9841000008', TRUE),
('u_f_09', 'Sunita Gurung', 'sunita', 'sunita@test.com', 'none', '9841000009', TRUE),
('u_f_10', 'Deepak Kc', 'deepakkc', 'deepak@test.com', 'none', '9841000010', TRUE),
('u_f_11', 'Prakash Oli', 'prakash', 'prakash@test.com', 'none', '9841000011', TRUE),
('u_f_12', 'Anjali Shah', 'anjali', 'anjali@test.com', 'none', '9841000012', TRUE),
('u_f_13', 'Rajesh Hamal', 'rajesh', 'rajesh@test.com', 'none', '9841000013', TRUE),
('u_f_14', 'Manoj Shrestha', 'manoj', 'manoj@test.com', 'none', '9841000014', TRUE),
('u_f_15', 'Kabita Rana', 'kabita', 'kabita@test.com', 'none', '9841000015', TRUE);

-- Buyer Users (b_user_1 to b_user_15)
INSERT INTO USERS (USER_GUID, FULL_NAME, USERNAME, EMAIL, PASSWORD, PHONE_NUMBER, IS_ACTIVE) VALUES
('u_b_01', 'Big Mart', 'bigmart', 'info@bigmart.com', 'none', '9851000001', TRUE),
('u_b_02', 'Bhatbhateni', 'bbsm', 'info@bbsm.com', 'none', '9851000002', TRUE),
('u_b_03', 'Green Grocery', 'greeng', 'orders@greeng.com', 'none', '9851000003', TRUE),
('u_b_04', 'Hotel Annapurna', 'annapurna', 'chef@annapurna.com', 'none', '9851000004', TRUE),
('u_b_05', 'Local Dealer', 'dealer1', 'dealer1@test.com', 'none', '9851000005', TRUE),
('u_b_06', 'Fresh Mart', 'freshmart', 'fresh@mart.com', 'none', '9851000006', TRUE),
('u_b_07', 'Organic World', 'organicw', 'info@organicw.com', 'none', '9851000007', TRUE),
('u_b_08', 'Namaste Supermarket', 'namaste', 'namaste@sm.com', 'none', '9851000008', TRUE),
('u_b_09', 'Valley Cold Store', 'valley', 'valley@cs.com', 'none', '9851000009', TRUE),
('u_b_10', 'Restaurant Hub', 'reshub', 'info@reshub.com', 'none', '9851000010', TRUE),
('u_b_11', 'Wholesale Agri', 'wholesale', 'sales@wholesale.com', 'none', '9851000011', TRUE),
('u_b_12', 'Direct Buy', 'directbuy', 'buy@direct.com', 'none', '9851000012', TRUE),
('u_b_13', 'Fair Trade', 'fairtrade', 'info@fairtrade.org', 'none', '9851000013', TRUE),
('u_b_14', 'City Mart', 'citymart', 'admin@citymart.com', 'none', '9851000014', TRUE),
('u_b_15', 'Rural Link', 'rurallink', 'link@rural.com', 'none', '9851000015', TRUE);

-- Delivery Users (d_user_1 to d_user_15)
INSERT INTO USERS (USER_GUID, FULL_NAME, USERNAME, EMAIL, PASSWORD, PHONE_NUMBER, IS_ACTIVE) VALUES
('u_d_01', 'Krishna Prasad', 'krishnap', 'kp@test.com', 'none', '9861000001', TRUE),
('u_d_02', 'Bikash Thapa', 'bikash', 'bikash@test.com', 'none', '9861000002', TRUE),
('u_d_03', 'Sandip Rai', 'sandip', 'sandip@test.com', 'none', '9861000003', TRUE),
('u_d_04', 'Roshan Kc', 'roshan', 'roshan@test.com', 'none', '9861000004', TRUE),
('u_d_05', 'Umesh Lal', 'umesh', 'umesh@test.com', 'none', '9861000005', TRUE),
('u_d_06', 'Anup Shrestha', 'anup', 'anup@test.com', 'none', '9861000006', TRUE),
('u_d_07', 'Subash Gurung', 'subash', 'subash@test.com', 'none', '9861000007', TRUE),
('u_d_08', 'Prem Oli', 'prem', 'prem@test.com', 'none', '9861000008', TRUE),
('u_d_09', 'Santosh Rana', 'santosh', 'santosh@test.com', 'none', '9861000009', TRUE),
('u_d_10', 'Arjun Thapa', 'arjun', 'arjun@test.com', 'none', '9861000010', TRUE),
('u_d_11', 'Nabin Kc', 'nabin', 'nabin@test.com', 'none', '9861000011', TRUE),
('u_d_12', 'Gopal Rai', 'gopal', 'gopal@test.com', 'none', '9861000012', TRUE),
('u_d_13', 'Kiran Shah', 'kiran', 'kiran@test.com', 'none', '9861000013', TRUE),
('u_d_14', 'Sujan Hamal', 'sujan', 'sujan@test.com', 'none', '9861000014', TRUE),
('u_d_15', 'Pawan Giri', 'pawan', 'pawan@test.com', 'none', '9861000015', TRUE);
