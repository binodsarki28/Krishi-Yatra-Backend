-- Stub: Insert Dummy Categories
INSERT INTO CATEGORY (CATEGORY_ID, CATEGORY_NAME, CREATED_USER) VALUES
(2, 'Vegetables', 'System'),
(3, 'Fruits', 'System'),
(4, 'Grains', 'System'),
(5, 'Pulses', 'System');

-- Stub: Insert Sub-categories for Vegetables
INSERT INTO SUB_CATEGORY (SUB_CATEGORY_ID, SUB_CATEGORY_NAME, CATEGORY_ID, CREATED_USER) VALUES
(2, 'Tomato', 2, 'System'),
(3, 'Potato', 2, 'System'),
(4, 'Onion', 2, 'System'),
(5, 'Carrot', 2, 'System'),
(6, 'Cabbage', 2, 'System');

-- Stub: Insert Sub-categories for Fruits
INSERT INTO SUB_CATEGORY (SUB_CATEGORY_ID, SUB_CATEGORY_NAME, CATEGORY_ID, CREATED_USER) VALUES
(7, 'Apple', 3, 'System'),
(8, 'Banana', 3, 'System'),
(9, 'Orange', 3, 'System'),
(10, 'Mango', 3, 'System'),
(11, 'Grapes', 3, 'System');

-- Stub: Insert Sub-categories for Grains
INSERT INTO SUB_CATEGORY (SUB_CATEGORY_ID, SUB_CATEGORY_NAME, CATEGORY_ID, CREATED_USER) VALUES
(12, 'Rice', 4, 'System'),
(13, 'Wheat', 4, 'System'),
(14, 'Maize', 4, 'System'),
(15, 'Barley', 4, 'System'),
(16, 'Millet', 4, 'System');

-- Stub: Insert Sub-categories for Pulses
INSERT INTO SUB_CATEGORY (SUB_CATEGORY_ID, SUB_CATEGORY_NAME, CATEGORY_ID, CREATED_USER) VALUES
(17, 'Lentils', 5, 'System'),
(18, 'Chickpeas', 5, 'System'),
(19, 'Beans', 5, 'System'),
(20, 'Peas', 5, 'System');

-- Stub: Insert Dummy Stocks for the Normal User (f0000000-0000-0000-0000-000000000001)
-- Linked to BOTH Category and Sub-category
INSERT INTO STOCKS (STOCK_GUID, STOCK_NAME, PRODUCT_NAME, STOCK_SLUG, DESCRIPTION, QUANTITY, PRICE_PER_UNIT, ACTIVE, FARMER_GUID, CATEGORY_ID, SUB_CATEGORY_ID, CREATED_USER) VALUES
('stock_001', 'Fresh Red Tomatoes', 'Tomato', 'fresh-red-tomatoes-v1', 'Organic farm fresh red tomatoes from Chitwan.', 100.0, 80.0, TRUE, 'f1111111-1111-1111-1111-111111111111', 2, 2, 'System'),
('stock_002', 'Mustang Potatoes', 'Potato', 'mustang-potatoes-v1', 'Original tasty potatoes from Mustang region.', 500.0, 60.0, TRUE, 'f1111111-1111-1111-1111-111111111111', 2, 3, 'System'),
('stock_003', 'Sweet Organic Apples', 'Apple', 'sweet-organic-apples-v1', 'Crunchy and sweet organic apples.', 200.0, 150.0, TRUE, 'f1111111-1111-1111-1111-111111111111', 3, 7, 'System'),
('stock_004', 'Local Basmati Rice', 'Rice', 'local-basmati-rice-v1', 'Aromatic local basmati rice.', 1000.0, 120.0, TRUE, 'f1111111-1111-1111-1111-111111111111', 4, 12, 'System');
