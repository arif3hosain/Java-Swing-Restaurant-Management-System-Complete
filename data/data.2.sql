truncate table bill_details, bill;
truncate table category, item ;
TRUNCATE TABLE  food_size;

ALTER SEQUENCE rms.public.app_user_id_seq RESTART WITH 1;
ALTER SEQUENCE rms.public.bill_details_id_seq RESTART WITH 1;
ALTER SEQUENCE rms.public.bill_id_seq RESTART WITH 1;
ALTER SEQUENCE rms.public.category_id_seq RESTART WITH 1;
ALTER SEQUENCE rms.public.food_size_id_seq RESTART WITH 1;
ALTER SEQUENCE rms.public.invoice_no_seq RESTART WITH 1;
ALTER SEQUENCE rms.public.item_id_seq RESTART WITH 1;
ALTER SEQUENCE rms.public.keyvalue_id_seq RESTART WITH 1;
ALTER SEQUENCE rms.public.role_id_seq RESTART WITH 1;


-- Insert Categories
INSERT INTO category (name, description, status, deleted) VALUES
                                                              ('Beef', 'Beef dishes and items', true, false),
                                                              ('Bhorta', 'Traditional mashed dishes', true, false),
                                                              ('Chicken', 'Chicken dishes and items', true, false),
                                                              ('Dessert', 'Sweet desserts and treats', true, false),
                                                              ('Drinks-Beverage', 'Beverages and drinks', true, false),
                                                              ('Fish', 'Fish dishes and items', true, false),
                                                              ('Mutton', 'Mutton dishes and items', true, false),
                                                              ('Rice', 'Rice dishes', true, false),
                                                              ('Sweets', 'Traditional sweets', true, false),
                                                              ('Tandoor-Griddle', 'Bread and tandoor items', true, false),
                                                              ('Vegetable', 'Vegetable dishes', true, false);

-- Insert Food Sizes (Units)
INSERT INTO food_size (name) VALUES
                                 ('Plate'),
                                 ('Half'),
                                 ('Quarter'),
                                 ('Full'),
                                 ('Pcs'),
                                 ('1 KG'),
                                 ('KG'),
                                 ('Cup'),
                                 ('Glass'),
                                 ('1 Liter'),
                                 ('250 ml'),
                                 ('500 ml'),
                                 ('1.5 Liter');

-- Insert Beef Items
INSERT INTO item (item_name, description, quantity, price, cat_id, available, deleted) VALUES
                                                                                           ('Beef Kolija', NULL, 'Plate', 180.00, 1, true, false),
                                                                                           ('Beef Speacial Nehari', NULL, 'Plate', 160.00, 1, true, false),
                                                                                           ('Beef Khichuri', NULL, 'Half', 0.00, 1, true, false),
                                                                                           ('Beef Khichuri', NULL, 'Quarter', 0.00, 1, true, false),
                                                                                           ('Beef Khichuri', NULL, 'Full', 0.00, 1, true, false),
                                                                                           ('Beef Chap Khichuri', NULL, 'Half', 180.00, 1, true, false),
                                                                                           ('Beef Chap Khichuri', NULL, 'Quarter', 0.00, 1, true, false),
                                                                                           ('Beef Chap Khichuri', NULL, 'Full', 0.00, 1, true, false),
                                                                                           ('Special Beef Biriyani', NULL, 'Half', 0.00, 1, true, false),
                                                                                           ('Special Beef Biriyani', NULL, 'Quarter', 0.00, 1, true, false),
                                                                                           ('Special Beef Biriyani', NULL, 'Full', 0.00, 1, true, false),
                                                                                           ('Kacchi Biriyani (Beef)', NULL, 'Half', 0.00, 1, true, false),
                                                                                           ('Kacchi Biriyani (Beef)', NULL, 'Quarter', 0.00, 1, true, false),
                                                                                           ('Kacchi Biriyani (Beef)', NULL, 'Full', 0.00, 1, true, false),
                                                                                           ('Beef Rejala Bhuna', NULL, 'Plate', 0.00, 1, true, false),
                                                                                           ('Khiri Kabab (Beef)', NULL, 'Plate', 0.00, 1, true, false),
                                                                                           ('Gurda Kabab', NULL, 'Plate', 0.00, 1, true, false),
                                                                                           ('Beef Shik Kabab', NULL, 'Pcs', 200.00, 1, true, false),
                                                                                           ('Beef Chap', NULL, 'Plate', 0.00, 1, true, false),
                                                                                           ('Kima Bhuna', NULL, 'Plate', 200.00, 1, true, false),
                                                                                           ('Beef', NULL, '1 KG', 0.00, 1, true, false),
                                                                                           ('Kala Bhuna', NULL, '1 KG', 0.00, 1, true, false),
                                                                                           ('Kima', NULL, '1 KG', 0.00, 1, true, false),
                                                                                           ('Speacial Kala Bhuna', NULL, 'Plate', 0.00, 1, true, false),
                                                                                           ('Kerli Asta', NULL, 'Pcs', 680.00, 1, true, false),
                                                                                           ('Beef Jhal Fry', NULL, 'Plate', 150.00, 1, true, false),
                                                                                           ('Tilli Kabab', NULL, 'Plate', 180.00, 1, true, false),
                                                                                           ('Bihari Kabab', NULL, 'Plate', 300.00, 1, true, false),
                                                                                           ('Bombay Kabab', NULL, 'Plate', 200.00, 1, true, false),
                                                                                           ('Afgani Kabab', NULL, 'Plate', 220.00, 1, true, false),
                                                                                           ('Beef Nehari', NULL, 'Plate', 160.00, 1, true, false),
                                                                                           ('Beef Bhuna', NULL, 'Plate', 180.00, 1, true, false),
                                                                                           ('Kolija', NULL, 'Plate', 180.00, 1, true, false),
                                                                                           ('Mashmoti Rice Kacchi (Beef)', NULL, 'Plate', 230.00, 1, true, false),
                                                                                           ('Mejban Lal Bhuna', NULL, 'Plate', 200.00, 1, true, false),
                                                                                           ('Kalo Bhuna', NULL, 'Plate', 200.00, 1, true, false),
                                                                                           ('Beef Handi', NULL, 'Plate', 250.00, 1, true, false),
                                                                                           ('Chuijhal Beef', NULL, 'Plate', 230.00, 1, true, false),
                                                                                           ('Peshwari Beef', NULL, 'Plate', 220.00, 1, true, false);

-- Insert Bhorta Items
INSERT INTO item (item_name, description, quantity, price, cat_id, available, deleted) VALUES
                                                                                           ('Kola Vorta', NULL, 'Plate', 30.00, 2, true, false),
                                                                                           ('Begun Vorta', NULL, 'Plate', 20.00, 2, true, false),
                                                                                           ('Borboti Vorta', NULL, 'Plate', 20.00, 2, true, false),
                                                                                           ('Tomato Vorta', NULL, 'Plate', 20.00, 2, true, false),
                                                                                           ('Kalojira Vorta', NULL, 'Plate', 40.00, 2, true, false),
                                                                                           ('Shutki Vorta', NULL, 'Plate', 50.00, 2, true, false),
                                                                                           ('Chicken Vorta', NULL, 'Plate', 50.00, 2, true, false),
                                                                                           ('Chingri Vorta', NULL, 'Plate', 40.00, 2, true, false),
                                                                                           ('Taki Fish Vorta', NULL, 'Plate', 50.00, 2, true, false);

-- Insert Chicken Items
INSERT INTO item (item_name, description, quantity, price, cat_id, available, deleted) VALUES
                                                                                           ('Chicken Soup', NULL, 'Plate', 120.00, 3, true, false),
                                                                                           ('Chiken Tandori', NULL, 'Plate', 140.00, 3, true, false),
                                                                                           ('Chicken Tikka', NULL, 'Pcs', 230.00, 3, true, false),
                                                                                           ('Chicken Gril', NULL, 'Half', 120.00, 3, true, false),
                                                                                           ('Chicken Gril', NULL, 'Quarter', 0.00, 3, true, false),
                                                                                           ('Chicken Gril', NULL, 'Full', 0.00, 3, true, false),
                                                                                           ('Chicken Reshmi Kabab', NULL, 'Half', 180.00, 3, true, false),
                                                                                           ('Chicken Reshmi Kabab', NULL, 'Quarter', 0.00, 3, true, false),
                                                                                           ('Chicken Hariyali Kabab', NULL, 'Full', 0.00, 3, true, false),
                                                                                           ('Chicken Sharma', NULL, 'Plate', 0.00, 3, true, false),
                                                                                           ('Chicken Body Kadad', NULL, 'Plate', 200.00, 3, true, false),
                                                                                           ('Chicken Masala Chap', NULL, 'Plate', 160.00, 3, true, false),
                                                                                           ('Chicken Jhal Fry', NULL, 'Plate', 150.00, 3, true, false),
                                                                                           ('Chicken Roast', NULL, 'Plate', 130.00, 3, true, false),
                                                                                           ('Naga Chicken (Cock)', NULL, 'Plate', 560.00, 3, true, false),
                                                                                           ('Chicken Manai Kabab', NULL, 'Plate', 200.00, 3, true, false),
                                                                                           ('Chicken Ruji Kabab', NULL, 'Plate', 180.00, 3, true, false),
                                                                                           ('Tanduri BBQ', NULL, 'Plate', 150.00, 3, true, false),
                                                                                           ('Chicken Khichuri', NULL, 'Plate', 160.00, 3, true, false),
                                                                                           ('Chicken Biriyani with Egg', NULL, 'Plate', 180.00, 3, true, false),
                                                                                           ('Lemon Chicken Cock', NULL, 'Plate', 150.00, 3, true, false);

-- Insert Dessert Items
INSERT INTO item (item_name, description, quantity, price, cat_id, available, deleted) VALUES
                                                                                           ('Doi', NULL, 'Cup', 30.00, 4, true, false),
                                                                                           ('Firni', NULL, 'Cup', 30.00, 4, true, false),
                                                                                           ('Halua', NULL, 'Plate', 30.00, 4, true, false);

-- Insert Drinks-Beverage Items
INSERT INTO item (item_name, description, quantity, price, cat_id, available, deleted) VALUES
                                                                                           ('Tea', NULL, 'Cup', 30.00, 5, true, false),
                                                                                           ('Cofee Regular', NULL, 'Cup', 0.00, 5, true, false),
                                                                                           ('Borhani', NULL, 'Glass', 90.00, 5, true, false),
                                                                                           ('Borhani', NULL, '1 Liter', 180.00, 5, true, false),
                                                                                           ('Coca-Cola', NULL, '250 ml', 0.00, 5, true, false),
                                                                                           ('Coca-Cola', NULL, '500 ml', 0.00, 5, true, false),
                                                                                           ('Coca-Cola', NULL, '1 Liter', 0.00, 5, true, false),
                                                                                           ('Coca-Cola', NULL, '1.5 Liter', 0.00, 5, true, false),
                                                                                           ('Sprite', NULL, '250 ml', 0.00, 5, true, false),
                                                                                           ('Sprite', NULL, '500 ml', 0.00, 5, true, false),
                                                                                           ('Sprite', NULL, '1 Liter', 0.00, 5, true, false),
                                                                                           ('Sprite', NULL, '1.5 Liter', 0.00, 5, true, false),
                                                                                           ('Mojo', NULL, '250 ml', 0.00, 5, true, false),
                                                                                           ('Mojo', NULL, '500 ml', 0.00, 5, true, false),
                                                                                           ('Mojo', NULL, '1 Liter', 0.00, 5, true, false),
                                                                                           ('Mojo', NULL, '1.5 Liter', 0.00, 5, true, false),
                                                                                           ('7 UP', NULL, '250 ml', 0.00, 5, true, false),
                                                                                           ('7 UP', NULL, '500 ml', 0.00, 5, true, false),
                                                                                           ('7 UP', NULL, '1 Liter', 0.00, 5, true, false),
                                                                                           ('7 UP', NULL, '1.5 Liter', 0.00, 5, true, false),
                                                                                           ('Mountain Dew', NULL, '250 ml', 0.00, 5, true, false),
                                                                                           ('Mountain Dew', NULL, '500 ml', 0.00, 5, true, false),
                                                                                           ('Mountain Dew', NULL, '1 Liter', 0.00, 5, true, false),
                                                                                           ('Mountain Dew', NULL, '1.5 Liter', 0.00, 5, true, false);

-- Insert Fish Items
INSERT INTO item (item_name, description, quantity, price, cat_id, available, deleted) VALUES
                                                                                           ('Ilish Fish', NULL, 'Pcs', 0.00, 6, true, false),
                                                                                           ('Rui Fish', NULL, 'Pcs', 0.00, 6, true, false),
                                                                                           ('Boal Fish', NULL, 'Pcs', 0.00, 6, true, false),
                                                                                           ('Pabda Fish', NULL, 'Pcs', 0.00, 6, true, false),
                                                                                           ('Coral Fish', NULL, 'Pcs', 0.00, 6, true, false),
                                                                                           ('Aair Fish', NULL, 'Pcs', 0.00, 6, true, false),
                                                                                           ('Chingri Fish', NULL, 'Plate', 0.00, 6, true, false),
                                                                                           ('Sea Big Fish', NULL, 'Pcs', 0.00, 6, true, false),
                                                                                           ('Ilish Polau', NULL, 'Plate', 350.00, 6, true, false),
                                                                                           ('Telapia', NULL, 'Pcs', 0.00, 6, true, false),
                                                                                           ('Koi Fish', NULL, 'Pcs', 0.00, 6, true, false);

-- Insert Mutton Items
INSERT INTO item (item_name, description, quantity, price, cat_id, available, deleted) VALUES
                                                                                           ('Mutton', NULL, 'Plate', 0.00, 7, true, false),
                                                                                           ('Mutton Khichuri', NULL, 'Plate', 0.00, 7, true, false),
                                                                                           ('Speacial Mutton Biriyani', NULL, 'Half', 0.00, 7, true, false),
                                                                                           ('Speacial Mutton Biriyani', NULL, 'Quarter', 0.00, 7, true, false),
                                                                                           ('Speacial Mutton Biriyani', NULL, 'Full', 0.00, 7, true, false),
                                                                                           ('Kacchi Biriyani (Mutton)', NULL, 'Full', 0.00, 7, true, false),
                                                                                           ('Mutton Body Kabab', NULL, 'Pcs', 200.00, 7, true, false),
                                                                                           ('khiri Kabab ( Mutton)', NULL, 'Pcs', 0.00, 7, true, false),
                                                                                           ('Mutton Alu Kosha', NULL, 'Plate', 0.00, 7, true, false),
                                                                                           ('Mutton leg roast', NULL, 'Plate', 0.00, 7, true, false),
                                                                                           ('Mutton', NULL, '1 KG', 0.00, 7, true, false),
                                                                                           ('Mutton Paya', NULL, 'Plate', 130.00, 7, true, false),
                                                                                           ('Bashmoti Rice Kacchi (Mutton)', NULL, 'Plate', 250.00, 7, true, false),
                                                                                           ('Chuijhal Mutton', NULL, 'Plate', 250.00, 7, true, false),
                                                                                           ('Mutton Bhuna', NULL, 'Plate', 220.00, 7, true, false);

-- Insert Rice Items
INSERT INTO item (item_name, description, quantity, price, cat_id, available, deleted) VALUES
                                                                                           ('Plain Rice', NULL, 'Plate', 20.00, 8, true, false),
                                                                                           ('Khichuri Rice', NULL, 'Plate', 60.00, 8, true, false),
                                                                                           ('Polao', NULL, 'Plate', 320.00, 8, true, false),
                                                                                           ('Khichuri', NULL, 'Plate', 0.00, 8, true, false);

-- Insert Sweets Items
INSERT INTO item (item_name, description, quantity, price, cat_id, available, deleted) VALUES
                                                                                           ('Lalmohon', NULL, 'KG', 350.00, 9, true, false),
                                                                                           ('Rosh Malai Kalachana', NULL, 'KG', 600.00, 9, true, false),
                                                                                           ('Shor Malai', NULL, 'KG', 700.00, 9, true, false),
                                                                                           ('Jafran Bhog', NULL, 'KG', 80.00, 9, true, false),
                                                                                           ('Kalojam', NULL, 'KG', 300.00, 9, true, false),
                                                                                           ('Kala Mohon', NULL, 'KG', 350.00, 9, true, false),
                                                                                           ('Kacha Golla', NULL, 'KG', 600.00, 9, true, false),
                                                                                           ('Madraras Roshogolla', NULL, 'KG', 600.00, 9, true, false),
                                                                                           ('Shor Golla', NULL, 'KG', 800.00, 9, true, false),
                                                                                           ('Rosh Golla', NULL, 'KG', 460.00, 9, true, false),
                                                                                           ('Laddu Man', NULL, 'KG', 400.00, 9, true, false);

-- Insert Tandoor-Griddle Items
INSERT INTO item (item_name, description, quantity, price, cat_id, available, deleted) VALUES
                                                                                           ('Ruti', NULL, 'Pcs', 10.00, 10, true, false),
                                                                                           ('Porata', NULL, 'Pcs', 10.00, 10, true, false),
                                                                                           ('Butter Nun', NULL, 'Pcs', 35.00, 10, true, false),
                                                                                           ('Garlic Nun', NULL, 'Pcs', 60.00, 10, true, false),
                                                                                           ('Kabli Nun', NULL, 'Pcs', 80.00, 10, true, false),
                                                                                           ('Kashmiri Nun', NULL, 'Pcs', 120.00, 10, true, false),
                                                                                           ('Sweet Nun', NULL, 'Pcs', 0.00, 10, true, false),
                                                                                           ('Tana Porata', NULL, 'Pcs', 30.00, 10, true, false),
                                                                                           ('Laccha Porata', NULL, 'Pcs', 70.00, 10, true, false),
                                                                                           ('Ghee Porata', NULL, 'Pcs', 50.00, 10, true, false),
                                                                                           ('Shahi Porata', NULL, 'Pcs', 30.00, 10, true, false),
                                                                                           ('Patla', NULL, 'Pcs', 20.00, 10, true, false),
                                                                                           ('Special Nun', NULL, 'Pcs', 90.00, 10, true, false),
                                                                                           ('Afgani Gril', NULL, 'Pcs', 520.00, 10, true, false),
                                                                                           ('Pech Porata', NULL, 'Pcs', 20.00, 10, true, false);

-- Insert Vegetable Items
INSERT INTO item (item_name, description, quantity, price, cat_id, available, deleted) VALUES
                                                                                           ('Mug Dal', NULL, 'Plate', 30.00, 11, true, false),
                                                                                           ('Vaji', NULL, 'Plate', 20.00, 11, true, false),
                                                                                           ('Patla Dal', NULL, 'Plate', 25.00, 11, true, false),
                                                                                           ('Korolla Vaji', NULL, 'Plate', 30.00, 11, true, false),
                                                                                           ('Shak Vaji', NULL, 'Plate', 30.00, 11, true, false),
                                                                                           ('Vegetable', NULL, 'Plate', 20.00, 11, true, false),
                                                                                           ('Buter Dal', NULL, 'Plate', 20.00, 11, true, false),
                                                                                           ('Vendi', NULL, 'Plate', 30.00, 11, true, false);