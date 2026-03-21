-- Stub: Insert More Categories
INSERT INTO CATEGORY (CATEGORY_GUID, CATEGORY_NAME, CREATED_USER) VALUES
('cat_fis_005', 'Fish', 'System'),
('cat_dai_006', 'Dairy', 'System'),
('cat_her_007', 'Herbs', 'System'),
('cat_pou_008', 'Poultry', 'System'),
('cat_spi_009', 'Spices', 'System');

-- Stub: Insert Sub-categories for Fish
INSERT INTO SUB_CATEGORY (SUB_CATEGORY_GUID, SUB_CATEGORY_NAME, CATEGORY_GUID, CREATED_USER) VALUES
('sub_fis_trout', 'Rainbow Trout', 'cat_fis_005', 'System'),
('sub_fis_carp', 'Silver Carp', 'cat_fis_005', 'System'),
('sub_fis_prawn', 'Prawns', 'cat_fis_005', 'System');

-- Stub: Insert Sub-categories for Dairy
INSERT INTO SUB_CATEGORY (SUB_CATEGORY_GUID, SUB_CATEGORY_NAME, CATEGORY_GUID, CREATED_USER) VALUES
('sub_dai_milk', 'Fresh Milk', 'cat_dai_006', 'System'),
('sub_dai_yog', 'Local Yogurt', 'cat_dai_006', 'System'),
('sub_dai_paneer', 'Paneer', 'cat_dai_006', 'System');

-- Stub: Insert Sub-categories for Herbs
INSERT INTO SUB_CATEGORY (SUB_CATEGORY_GUID, SUB_CATEGORY_NAME, CATEGORY_GUID, CREATED_USER) VALUES
('sub_her_mint', 'Fresh Mint', 'cat_her_007', 'System'),
('sub_her_basil', 'Basil', 'cat_her_007', 'System'),
('sub_her_cor', 'Coriander', 'cat_her_007', 'System');

-- Stub: Insert Sub-categories for Poultry
INSERT INTO SUB_CATEGORY (SUB_CATEGORY_GUID, SUB_CATEGORY_NAME, CATEGORY_GUID, CREATED_USER) VALUES
('sub_pou_egg', 'Free-range Eggs', 'cat_pou_008', 'System'),
('sub_pou_chi', 'Country Chicken', 'cat_pou_008', 'System');

-- Stub: Insert Sub-categories for Spices
INSERT INTO SUB_CATEGORY (SUB_CATEGORY_GUID, SUB_CATEGORY_NAME, CATEGORY_GUID, CREATED_USER) VALUES
('sub_spi_tur', 'Turmeric', 'cat_spi_009', 'System'),
('sub_spi_gin', 'Ginger', 'cat_spi_009', 'System'),
('sub_spi_gar', 'Garlic', 'cat_spi_009', 'System');

-- Stub: Bulk Stock Data (50 entries total)
INSERT INTO STOCKS (STOCK_GUID, STOCK_NAME, PRODUCT_NAME, STOCK_SLUG, DESCRIPTION, QUANTITY, PRICE_PER_UNIT, ACTIVE, FARMER_GUID, CATEGORY_GUID, SUB_CATEGORY_GUID, CREATED_USER, CREATED_TIME) VALUES
('stk_5', 'Hilly Rainbow Trout', 'Trout', 'hilly-trout-1', 'Fresh trout from cold mountain water.', 50.0, 1200.0, TRUE, 'f_id_05', 'cat_fis_005', 'sub_fis_trout', 'System', CURRENT_TIMESTAMP),
('stk_6', 'Pure Cow Milk', 'Milk', 'pure-cow-milk-1', 'Daily fresh unadulterated cow milk.', 200.0, 90.0, TRUE, 'f_id_06', 'cat_dai_006', 'sub_dai_milk', 'System', CURRENT_TIMESTAMP),
('stk_7', 'Organic Lemon Mint', 'Mint', 'organic-mint-1', 'Refreshing mint leaves for tea and food.', 30.0, 50.0, TRUE, 'f_id_01', 'cat_her_007', 'sub_her_mint', 'System', CURRENT_TIMESTAMP),
('stk_8', 'Fresh Country Chicken', 'Chicken', 'country-chicken-1', 'Healthy free-range country chicken meat.', 20.0, 850.0, TRUE, 'f_id_05', 'cat_pou_008', 'sub_pou_chi', 'System', CURRENT_TIMESTAMP),
('stk_9', 'Organic Turmeric Powder', 'Turmeric', 'org-turmeric-1', 'Pure ground turmeric with high curcumin.', 100.0, 350.0, TRUE, 'f_id_12', 'cat_spi_009', 'sub_spi_tur', 'System', CURRENT_TIMESTAMP),
('stk_10', 'Large Red Onions', 'Onion', 'large-red-onions-1', 'Crispy and pungent large red onions.', 500.0, 70.0, TRUE, 'f_id_04', 'cat_veg_001', 'sub_v_onion', 'System', CURRENT_TIMESTAMP),
('stk_11', 'Sweet Cavendish Bananas', 'Banana', 'sweet-bananas-1', 'Perfectly ripe local cavendish bananas.', 150.0, 120.0, TRUE, 'f_id_01', 'cat_fru_002', 'sub_f_banana', 'System', CURRENT_TIMESTAMP),
('stk_12', 'Premium Long Grain Rice', 'Rice', 'premium-rice-1', 'Finest quality long grain white rice.', 2000.0, 110.0, TRUE, 'f_id_10', 'cat_gra_003', 'sub_g_rice', 'System', CURRENT_TIMESTAMP),
('stk_13', 'Split Red Lentils', 'Lentils', 'red-lentils-1', 'Protein rich organic red lentils.', 300.0, 160.0, TRUE, 'f_id_02', 'cat_pul_004', 'sub_p_lentils', 'System', CURRENT_TIMESTAMP),
('stk_14', 'Fresh Cauliflower', 'Cabbage', 'fresh-cauliflower-1', 'Large white cauliflower heads.', 80.0, 45.0, TRUE, 'f_id_04', 'cat_veg_001', 'sub_v_cabbage', 'System', CURRENT_TIMESTAMP),
('stk_15', 'Juicy Valencia Oranges', 'Orange', 'valencia-oranges-1', 'Sweet and juicy valencia oranges.', 120.0, 180.0, TRUE, 'f_id_08', 'cat_fru_002', 'sub_f_orange', 'System', CURRENT_TIMESTAMP),
('stk_16', 'Whole Wheat Grain', 'Wheat', 'whole-wheat-1', 'Fiber rich farm whole wheat grain.', 1500.0, 55.0, TRUE, 'f_id_10', 'cat_gra_003', 'sub_g_wheat', 'System', CURRENT_TIMESTAMP),
('stk_17', 'Organic Chickpeas', 'Chickpeas', 'org-chickpeas-1', 'Direct from field organic chickpeas.', 400.0, 140.0, TRUE, 'f_id_11', 'cat_pul_004', 'sub_p_chickpeas', 'System', CURRENT_TIMESTAMP),
('stk_18', 'Farm Fresh Spinach', 'Vegetable', 'fresh-spinach-1', 'Nutrient rich green spinach leaves.', 60.0, 30.0, TRUE, 'f_id_04', 'cat_veg_001', 'sub_v_tomato', 'System', CURRENT_TIMESTAMP),
('stk_19', 'Alphonso Mangoes', 'Mango', 'alphonso-mango-1', 'King of mangoes from local orchard.', 250.0, 400.0, TRUE, 'f_id_08', 'cat_fru_002', 'sub_f_mango', 'System', CURRENT_TIMESTAMP),
('stk_20', 'Yellow Corn Maize', 'Maize', 'yellow-maize-1', 'High quality yellow corn for various uses.', 80.0, 40.0, TRUE, 'f_id_10', 'cat_gra_003', 'sub_g_maize', 'System', CURRENT_TIMESTAMP),
('stk_21', 'Black Soybeans', 'Beans', 'black-beans-1', 'Traditional local black soybeans.', 200.0, 180.0, TRUE, 'f_id_11', 'cat_pul_004', 'sub_p_beans', 'System', CURRENT_TIMESTAMP),
('stk_22', 'Sweet Green Peas', 'Peas', 'green-peas-1', 'Freshly shelled sweet green peas.', 100.0, 90.0, TRUE, 'f_id_04', 'cat_pul_004', 'sub_p_peas', 'System', CURRENT_TIMESTAMP),
('stk_23', 'Silver Carp Fish', 'Carp', 'silver-carp-1', 'Large silver carp from pond harvest.', 40.0, 450.0, TRUE, 'f_id_05', 'cat_fis_005', 'sub_fis_carp', 'System', CURRENT_TIMESTAMP),
('stk_24', 'Thick Buffalo Yogurt', 'Yogurt', 'buffalo-yogurt-1', 'Creamy local buffalo curd.', 50.0, 150.0, TRUE, 'f_id_06', 'cat_dai_006', 'sub_dai_yog', 'System', CURRENT_TIMESTAMP),
('stk_25', 'Italian Sweet Basil', 'Basil', 'sweet-basil-1', 'Aromatic basil leaves for cooking.', 15.0, 120.0, TRUE, 'f_id_01', 'cat_her_007', 'sub_her_basil', 'System', CURRENT_TIMESTAMP),
('stk_26', 'Fresh Brown Eggs', 'Eggs', 'brown-eggs-1', 'Natural brown eggs from happy hens.', 300.0, 15.0, TRUE, 'f_id_05', 'cat_pou_008', 'sub_pou_egg', 'System', CURRENT_TIMESTAMP),
('stk_27', 'Raw Ginger Roots', 'Ginger', 'raw-ginger-1', 'Freshly harvested spicy ginger roots.', 80.0, 220.0, TRUE, 'f_id_12', 'cat_spi_009', 'sub_spi_gin', 'System', CURRENT_TIMESTAMP),
('stk_28', 'White Garlic Bulbs', 'Garlic', 'white-garlic-1', 'Strong flavored white garlic bulbs.', 120.0, 250.0, TRUE, 'f_id_12', 'cat_spi_009', 'sub_spi_gar', 'System', CURRENT_TIMESTAMP),
('stk_29', 'Long Green Beans', 'Beans', 'green-beans-1', 'Tender long green beans.', 100.0, 85.0, TRUE, 'f_id_01', 'cat_veg_001', 'sub_v_potato', 'System', CURRENT_TIMESTAMP),
('stk_30', 'Black Grapes', 'Grapes', 'black-grapes-1', 'Sweet seedless black grapes.', 90.0, 280.0, TRUE, 'f_id_08', 'cat_fru_002', 'sub_f_grapes', 'System', CURRENT_TIMESTAMP),
('stk_31', 'Local Millet Grain', 'Millet', 'local-millet-1', 'Nutritious local millet grain.', 500.0, 95.0, TRUE, 'f_id_10', 'cat_gra_003', 'sub_g_millet', 'System', CURRENT_TIMESTAMP),
('stk_32', 'Fresh Broccoli', 'Vegetable', 'fresh-broccoli-1', 'Nutritive green broccoli florets.', 40.0, 150.0, TRUE, 'f_id_04', 'cat_veg_001', 'sub_v_onion', 'System', CURRENT_TIMESTAMP),
('stk_33', 'Red Fuji Apples', 'Apple', 'fuji-apples-1', 'Crispy red fuji apples.', 180.0, 200.0, TRUE, 'f_id_08', 'cat_fru_002', 'sub_f_apple', 'System', CURRENT_TIMESTAMP),
('stk_34', 'Pearl Barley', 'Barley', 'pearl-barley-1', 'High fiber pearl barley.', 300.0, 130.0, TRUE, 'f_id_10', 'cat_gra_003', 'sub_g_barley', 'System', CURRENT_TIMESTAMP),
('stk_35', 'Yellow Mung Dal', 'Lentils', 'mung-dal-1', 'Easy to digest yellow mung lentils.', 250.0, 210.0, TRUE, 'f_id_11', 'cat_pul_004', 'sub_p_lentils', 'System', CURRENT_TIMESTAMP),
('stk_36', 'Fresh Cilantro', 'Coriander', 'fresh-cilantro-1', 'Flavorful green coriander leaves.', 40.0, 40.0, TRUE, 'f_id_01', 'cat_her_007', 'sub_her_cor', 'System', CURRENT_TIMESTAMP),
('stk_37', 'Farm Fresh Paneer', 'Paneer', 'fresh-paneer-1', 'Soft and fresh homemade paneer.', 25.0, 750.0, TRUE, 'f_id_06', 'cat_dai_006', 'sub_dai_paneer', 'System', CURRENT_TIMESTAMP),
('stk_38', 'Fresh Prawns', 'Prawns', 'fresh-prawns-1', 'Large freshwater prawns.', 15.0, 1500.0, TRUE, 'f_id_05', 'cat_fis_005', 'sub_fis_prawn', 'System', CURRENT_TIMESTAMP),
('stk_39', 'Bitter Gourd', 'Vegetable', 'bitter-gourd-1', 'Fresh organic bitter gourd.', 70.0, 110.0, TRUE, 'f_id_01', 'cat_veg_001', 'sub_v_potato', 'System', CURRENT_TIMESTAMP),
('stk_40', 'Sweet Papaya', 'Fruits', 'sweet-papaya-1', 'Large ripe sweet papayas.', 60.0, 80.0, TRUE, 'f_id_01', 'cat_fru_002', 'sub_f_banana', 'System', CURRENT_TIMESTAMP),
('stk_41', 'Brown Basmati Rice', 'Rice', 'brown-rice-1', 'Healthy brown basmati rice.', 400.0, 160.0, TRUE, 'f_id_10', 'cat_gra_003', 'sub_g_rice', 'System', CURRENT_TIMESTAMP),
('stk_42', 'Green Chilies', 'Vegetable', 'green-chilies-1', 'Spicy green chilies.', 50.0, 120.0, TRUE, 'f_id_04', 'cat_veg_001', 'sub_v_tomato', 'System', CURRENT_TIMESTAMP),
('stk_43', 'Organic Strawberries', 'Fruits', 'strawberries-1', 'Fresh sweet strawberries.', 20.0, 600.0, TRUE, 'f_id_08', 'cat_fru_002', 'sub_f_grapes', 'System', CURRENT_TIMESTAMP),
('stk_44', 'Buckwheat Grain', 'Grains', 'buckwheat-1', 'Nutritious mountain buckwheat.', 350.0, 180.0, TRUE, 'f_id_10', 'cat_gra_003', 'sub_g_millet', 'System', CURRENT_TIMESTAMP),
('stk_45', 'Black Eyed Peas', 'Beans', 'black-eyed-peas-1', 'Dried black eyed peas.', 200.0, 145.0, TRUE, 'f_id_11', 'cat_pul_004', 'sub_p_peas', 'System', CURRENT_TIMESTAMP),
('stk_46', 'Fresh Lemongrass', 'Herbs', 'lemongrass-1', 'Aromatic lemongrass stalks.', 20.0, 90.0, TRUE, 'f_id_01', 'cat_her_007', 'sub_her_mint', 'System', CURRENT_TIMESTAMP),
('stk_47', 'Goat Milk', 'Milk', 'goat-milk-1', 'Pure and fresh hilly goat milk.', 30.0, 150.0, TRUE, 'f_id_06', 'cat_dai_006', 'sub_dai_milk', 'System', CURRENT_TIMESTAMP),
('stk_48', 'Dried Red Chilies', 'Spices', 'red-chilies-1', 'Extra spicy dried red chilies.', 50.0, 450.0, TRUE, 'f_id_12', 'cat_spi_009', 'sub_spi_tur', 'System', CURRENT_TIMESTAMP),
('stk_49', 'Fresh Radish', 'Vegetable', 'fresh-radish-1', 'Crunchy white radish.', 100.0, 35.0, TRUE, 'f_id_04', 'cat_veg_001', 'sub_v_carrot', 'System', CURRENT_TIMESTAMP),
('stk_50', 'Ripe Guavas', 'Fruits', 'guava-1', 'Sweet and fragrant pink guavas.', 80.0, 130.0, TRUE, 'f_id_08', 'cat_fru_002', 'sub_f_mango', 'System', CURRENT_TIMESTAMP),
('stk_51', 'Fresh Rosemary', 'Herbs', 'rosemary-1', 'Culinary rosemary sprigs.', 10.0, 180.0, TRUE, 'f_id_01', 'cat_her_007', 'sub_her_basil', 'System', CURRENT_TIMESTAMP),
('stk_52', 'Buffalo Butter', 'Dairy', 'buffalo-butter-1', 'Pure churned buffalo butter.', 40.0, 950.0, TRUE, 'f_id_06', 'cat_dai_006', 'sub_dai_paneer', 'System', CURRENT_TIMESTAMP),
('stk_53', 'Catfish', 'Fish', 'catfish-1', 'Fresh catfish for grilling.', 25.0, 400.0, TRUE, 'f_id_05', 'cat_fis_005', 'sub_fis_carp', 'System', CURRENT_TIMESTAMP),
('stk_54', 'Dill Leaves', 'Herbs', 'dill-leaves-1', 'Fresh dill leaves for garnish.', 15.0, 60.0, TRUE, 'f_id_01', 'cat_her_007', 'sub_her_mint', 'System', CURRENT_TIMESTAMP);
