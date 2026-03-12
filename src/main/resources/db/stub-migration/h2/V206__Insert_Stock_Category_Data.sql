-- Stub: Insert Dummy Categories
INSERT INTO CATEGORY (CATEGORY_GUID, CATEGORY_NAME, CREATED_USER) VALUES
('cat_veg_001', 'Vegetables', 'System'),
('cat_fru_002', 'Fruits', 'System'),
('cat_gra_003', 'Grains', 'System'),
('cat_pul_004', 'Pulses', 'System');

-- Stub: Insert Sub-categories for Vegetables
INSERT INTO SUB_CATEGORY (SUB_CATEGORY_GUID, SUB_CATEGORY_NAME, CATEGORY_GUID, CREATED_USER) VALUES
('sub_v_tomato', 'Tomato', 'cat_veg_001', 'System'),
('sub_v_potato', 'Potato', 'cat_veg_001', 'System'),
('sub_v_onion', 'Onion', 'cat_veg_001', 'System'),
('sub_v_carrot', 'Carrot', 'cat_veg_001', 'System'),
('sub_v_cabbage', 'Cabbage', 'cat_veg_001', 'System');

-- Stub: Insert Sub-categories for Fruits
INSERT INTO SUB_CATEGORY (SUB_CATEGORY_GUID, SUB_CATEGORY_NAME, CATEGORY_GUID, CREATED_USER) VALUES
('sub_f_apple', 'Apple', 'cat_fru_002', 'System'),
('sub_f_banana', 'Banana', 'cat_fru_002', 'System'),
('sub_f_orange', 'Orange', 'cat_fru_002', 'System'),
('sub_f_mango', 'Mango', 'cat_fru_002', 'System'),
('sub_f_grapes', 'Grapes', 'cat_fru_002', 'System');

-- Stub: Insert Sub-categories for Grains
INSERT INTO SUB_CATEGORY (SUB_CATEGORY_GUID, SUB_CATEGORY_NAME, CATEGORY_GUID, CREATED_USER) VALUES
('sub_g_rice', 'Rice', 'cat_gra_003', 'System'),
('sub_g_wheat', 'Wheat', 'cat_gra_003', 'System'),
('sub_g_maize', 'Maize', 'cat_gra_003', 'System'),
('sub_g_barley', 'Barley', 'cat_gra_003', 'System'),
('sub_g_millet', 'Millet', 'cat_gra_003', 'System');

-- Stub: Insert Sub-categories for Pulses
INSERT INTO SUB_CATEGORY (SUB_CATEGORY_GUID, SUB_CATEGORY_NAME, CATEGORY_GUID, CREATED_USER) VALUES
('sub_p_lentils', 'Lentils', 'cat_pul_004', 'System'),
('sub_p_chickpeas', 'Chickpeas', 'cat_pul_004', 'System'),
('sub_p_beans', 'Beans', 'cat_pul_004', 'System'),
('sub_p_peas', 'Peas', 'cat_pul_004', 'System');

-- Stub: Insert Dummy Stocks for the Normal User (f0000000-0000-0000-0000-000000000001)
-- Linked to BOTH Category and Sub-category
INSERT INTO STOCKS (STOCK_GUID, STOCK_NAME, PRODUCT_NAME, STOCK_SLUG, DESCRIPTION, QUANTITY, PRICE_PER_UNIT, ACTIVE, FARMER_GUID, CATEGORY_GUID, SUB_CATEGORY_GUID, CREATED_USER) VALUES
('stock_001', 'Fresh Red Tomatoes', 'Tomato', 'fresh-red-tomatoes-v1', 'Organic farm fresh red tomatoes from Chitwan.', 100.0, 80.0, TRUE, 'f0000000-0000-0000-0000-000000000001', 'cat_veg_001', 'sub_v_tomato', 'System'),
('stock_002', 'Mustang Potatoes', 'Potato', 'mustang-potatoes-v1', 'Original tasty potatoes from Mustang region.', 500.0, 60.0, TRUE, 'f0000000-0000-0000-0000-000000000001', 'cat_veg_001', 'sub_v_potato', 'System'),
('stock_003', 'Sweet Organic Apples', 'Apple', 'sweet-organic-apples-v1', 'Crunchy and sweet organic apples.', 200.0, 150.0, TRUE, 'f0000000-0000-0000-0000-000000000001', 'cat_fru_002', 'sub_f_apple', 'System'),
('stock_004', 'Local Basmati Rice', 'Rice', 'local-basmati-rice-v1', 'Aromatic local basmati rice.', 1000.0, 120.0, TRUE, 'f0000000-0000-0000-0000-000000000001', 'cat_gra_003', 'sub_g_rice', 'System');
