truncate table bill_details, bill;
truncate table item, category;
TRUNCATE TABLE  food_size;



INSERT INTO category (id, name, description, status, deleted) VALUES
                                                                  (1, 'Beef', NULL, true, false),
                                                                  (2, 'Bhorta', NULL, true, false),
                                                                  (3, 'Chicken', NULL, true, false),
                                                                  (4, 'Dessert', NULL, true, false),
                                                                  (5, 'Drinks-Beverage', NULL, true, false),
                                                                  (6, 'Fish', NULL, true, false),
                                                                  (7, 'Mutton', NULL, true, false),
                                                                  (8, 'Rice', NULL, true, false),
                                                                  (9, 'Sweets', NULL, true, false),
                                                                  (10, 'Tandoor-Griddle', NULL, true, false),
                                                                  (11, 'Vegetable', NULL, true, false);

INSERT INTO food_size (id, name) VALUES
                                     (1, 'Plate'),
                                     (2, 'Half'),
                                     (3, 'Quarter'),
                                     (4, 'Full'),
                                     (5, 'Pcs'),
                                     (6, '1 KG'),
                                     (7, 'Cup'),
                                     (8, 'Glass'),
                                     (9, '1 Liter'),
                                     (10, '250 ml'),
                                     (11, '500 ml'),
                                     (12, '1.5 Liter'),
                                     (13, 'KG'),
                                     (14, 'Pcs');


INSERT INTO item (item_name, description, quantity, price, discount, vat, available, cat_id, created_date, deleted)
VALUES
    ('Beef Kolija', NULL, 'Plate', 180, 0, 0, true, 1, NOW(), false),
    ('Beef Speacial Nehari', NULL, 'Plate', 160, 0, 0, true, 1, NOW(), false),
    ('Beef Khichuri', NULL, 'Half', 0, 0, 0, true, 1, NOW(), false),
    ('Beef Khichuri', NULL, 'Quarter', 0, 0, 0, true, 1, NOW(), false),
    ('Beef Khichuri', NULL, 'Full', 0, 0, 0, true, 1, NOW(), false),
    ('Beef Chap Khichuri', NULL, 'Half', 180, 0, 0, true, 1, NOW(), false),
    ('Beef Chap Khichuri', NULL, 'Quarter', 0, 0, 0, true, 1, NOW(), false),
    ('Beef Chap Khichuri', NULL, 'Full', 0, 0, 0, true, 1, NOW(), false),
    ('Special Beef Biriyani', NULL, 'Half', 0, 0, 0, true, 1, NOW(), false),
    ('Special Beef Biriyani', NULL, 'Quarter', 0, 0, 0, true, 1, NOW(), false),
    ('Special Beef Biriyani', NULL, 'Full', 0, 0, 0, true, 1, NOW(), false),
    ('Kacchi Biriyani (Beef)', NULL, 'Half', 0, 0, 0, true, 1, NOW(), false),
    ('Kacchi Biriyani (Beef)', NULL, 'Quarter', 0, 0, 0, true, 1, NOW(), false),
    ('Kacchi Biriyani (Beef)', NULL, 'Full', 0, 0, 0, true, 1, NOW(), false),
    ('Beef Rejala Bhuna', NULL, 'Plate', 0, 0, 0, true, 1, NOW(), false),
    ('Khiri Kabab (Beef)', NULL, 'Plate', 0, 0, 0, true, 1, NOW(), false),
    ('Gurda Kabab', NULL, 'Plate', 0, 0, 0, true, 1, NOW(), false),
    ('Beef Shik Kabab', NULL, 'Pcs', 200, 0, 0, true, 1, NOW(), false),
    ('Beef Rejala Bhuna', NULL, 'Plate', 0, 0, 0, true, 1, NOW(), false),
    ('Beef Chap', NULL, 'Plate', 0, 0, 0, true, 1, NOW(), false),
    ('Kima Bhuna', NULL, 'Plate', 200, 0, 0, true, 1, NOW(), false),
    ('Beef', NULL, '1 KG', 0, 0, 0, true, 1, NOW(), false),
    ('Kala Bhuna', NULL, '1 KG', 0, 0, 0, true, 1, NOW(), false),
    ('Kima', NULL, '1 KG', 0, 0, 0, true, 1, NOW(), false),
    ('Speacial Kala Bhuna', NULL, 'Plate', 0, 0, 0, true, 1, NOW(), false),
    ('Kola Vorta', NULL, 'Plate', 30, 0, 0, true, 2, NOW(), false),
    ('Begun Vorta', NULL, 'Plate', 20, 0, 0, true, 2, NOW(), false),
    ('Borboti Vorta', NULL, 'Plate', 20, 0, 0, true, 2, NOW(), false),
    ('Tomato Vorta', NULL, 'Plate', 20, 0, 0, true, 2, NOW(), false),
    ('Kalojira Vorta', NULL, 'Plate', 40, 0, 0, true, 2, NOW(), false),
    ('Shutki Vorta', NULL, 'Plate', 50, 0, 0, true, 2, NOW(), false),
    ('Chicken Vorta', NULL, 'Plate', 50, 0, 0, true, 2, NOW(), false),
    ('Chingri Vorta', NULL, 'Plate', 40, 0, 0, true, 2, NOW(), false),
    ('Taki Fish Vorta', NULL, 'Plate', 50, 0, 0, true, 2, NOW(), false);
