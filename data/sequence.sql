-- Bill table
SELECT setval('bill_id_seq', COALESCE((SELECT MAX(id) FROM bill), 0) + 1, false);

-- Bill details table
SELECT setval('bill_details_id_seq', COALESCE((SELECT MAX(id) FROM bill_details), 0) + 1, false);

-- Category table
SELECT setval('category_id_seq', COALESCE((SELECT MAX(id) FROM category), 0) + 1, false);

-- Item table
SELECT setval('item_id_seq', COALESCE((SELECT MAX(id) FROM item), 0) + 1, false);

-- Keyvalue table
SELECT setval('keyvalue_id_seq', COALESCE((SELECT MAX(id) FROM keyvalue), 0) + 1, false);

-- Food size table
SELECT setval('food_size_id_seq', COALESCE((SELECT MAX(id) FROM food_size), 0) + 1, false);

-- Invoice no (if numeric incremental)
SELECT setval('invoice_no_seq', COALESCE((SELECT MAX(invoice_no) FROM bill), 100000), false);

-- Role table
SELECT setval('role_id_seq', COALESCE((SELECT MAX(id) FROM role), 0) + 1, false);

-- App user table
SELECT setval('app_user_id_seq', COALESCE((SELECT MAX(id) FROM app_user), 0) + 1, false);
