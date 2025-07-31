CREATE TABLE bill (
                      id SERIAL PRIMARY KEY,
                      created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      amount NUMERIC(12, 2) NOT NULL,
                      vat_amt NUMERIC(12, 2) DEFAULT 0,
                      discount_amt NUMERIC(12, 2) DEFAULT 0,
                      total NUMERIC(12, 2) NOT NULL,
                      description TEXT
);

CREATE TABLE bill_details (
                              id SERIAL PRIMARY KEY,
                              food TEXT NOT NULL,
                              size VARCHAR(50),
                              quantity INTEGER NOT NULL,
                              per_unit_price NUMERIC(12, 2) NOT NULL,
                              total_price NUMERIC(12, 2) NOT NULL,
                              bill_id INTEGER NOT NULL,
                              FOREIGN KEY (bill_id) REFERENCES bill(id) ON DELETE CASCADE
);
CREATE TABLE category (
                          id SERIAL PRIMARY KEY,
                          name TEXT NOT NULL UNIQUE,
                          description TEXT,
                          status BOOLEAN DEFAULT TRUE,
                          deleted boolean default false
);

CREATE TABLE item (
                      id SERIAL PRIMARY KEY,
                      item_name TEXT NOT NULL,
                      description TEXT,
                      quantity VARCHAR(50),                        -- Represents size, e.g., Full/Half
                      price NUMERIC(12, 2) NOT NULL,
                      discount NUMERIC(5, 2) DEFAULT 0,            -- Discount percentage
                      vat NUMERIC(5, 2) DEFAULT 0,                 -- VAT percentage
                      available BOOLEAN DEFAULT TRUE,
                      cat_id INTEGER NOT NULL,
                      FOREIGN KEY (cat_id) REFERENCES category(id) ON DELETE CASCADE,
                      deleted boolean default false
);

CREATE TABLE keyvalue (
                          id SERIAL PRIMARY KEY,
                          report_path TEXT,
                          logo TEXT,
                          vat NUMERIC(5, 2),
                          discount NUMERIC(5, 2),
                          duration_count TEXT,
                          subscription_from TEXT
);

INSERT INTO keyvalue (
    id,
    report_path,
    vat,
    discount,
    duration_count,
    subscription_from
) VALUES (
             1,
             '/home/ahosain/Documents/personal/reports', -- or another valid path
             '10',        -- 10% VAT
             '5',         -- 5% Discount
             'yEx1rQ2LGWlmF0qPT3rUgA==', -- replace with encrypted value of initial days (e.g., Diff.encrypt("0"))
             'wCrV9e5Qt7PlZ3mpQmeVgA=='
         );
TRUNCATE TABLE item, category;
TRUNCATE TABLE item, category;

-- Restart sequences to start from 1
ALTER SEQUENCE category_id_seq RESTART WITH 1;
ALTER SEQUENCE item_id_seq RESTART WITH 1;




INSERT INTO category (name, description) VALUES
                                             ('Appetizers', 'Starters to begin your meal'),
                                             ('Soups', 'Warm and hearty soups'),
                                             ('Fried Rice', 'Fried rice varieties'),
                                             ('Noodles', 'Various noodle dishes'),
                                             ('Chowmein', 'Stir-fried noodles'),
                                             ('Chicken Dishes', 'Dishes with chicken as main ingredient'),
                                             ('Beef Dishes', 'Beef-based items'),
                                             ('Seafood', 'Fish, prawn and crab items'),
                                             ('Vegetarian', 'Vegetable-based meals'),
                                             ('Szechuan', 'Spicy Szechuan-style items'),
                                             ('Thai', 'Popular Thai dishes'),
                                             ('Set Menu', 'Pre-set combo meals'),
                                             ('Rice & Curry', 'Bengali rice with curry items'),
                                             ('Biryani', 'Special biryani dishes'),
                                             ('Snacks', 'Light bites'),
                                             ('Desserts', 'Sweet dishes'),
                                             ('Bengali Special', 'Traditional Bengali dishes'),
                                             ('Soft Drinks', 'Beverages'),
                                             ('Chinese Special', 'Signature Chinese items'),
                                             ('Tandoori', 'Tandoori-style grilled items');

INSERT INTO item (item_name, description, quantity, price, discount, vat, cat_id, created_date) VALUES
                                                                                                    ('Spring Rolls', 'Crispy vegetable spring rolls', '6 pcs', 180.00, 0, 5, 1, now()),
                                                                                                    ('Chicken Wonton', 'Deep fried wontons with chicken', '8 pcs', 220.00, 0, 5, 1, now()),
                                                                                                    ('Hot & Sour Soup', 'Tangy and spicy soup', 'bowl', 150.00, 0, 5, 2, now()),
                                                                                                    ('Sweet Corn Soup', 'Sweet corn and chicken soup', 'bowl', 160.00, 0, 5, 2, now()),
                                                                                                    ('Egg Fried Rice', 'Rice with egg and vegetables', 'plate', 180.00, 0, 5, 3, now()),
                                                                                                    ('Chicken Fried Rice', 'Classic fried rice with chicken', 'plate', 200.00, 0, 5, 3, now()),
                                                                                                    ('Beef Fried Rice', 'Fried rice with beef slices', 'plate', 210.00, 0, 5, 3, now()),
                                                                                                    ('Mixed Fried Rice', 'Combination of chicken, egg, prawns', 'plate', 250.00, 0, 5, 3, now()),
                                                                                                    ('Vegetable Noodles', 'Noodles with fresh vegetables', 'plate', 170.00, 0, 5, 4, now()),
                                                                                                    ('Chicken Chowmein', 'Stir-fried noodles with chicken', 'plate', 220.00, 0, 5, 5, now()),
                                                                                                    ('Beef Chowmein', 'Chowmein with marinated beef', 'plate', 230.00, 0, 5, 5, now()),
                                                                                                    ('Chicken Manchurian', 'Boneless chicken in spicy sauce', 'plate', 250.00, 0, 5, 6, now()),
                                                                                                    ('Kung Pao Chicken', 'Spicy chicken with peanuts', 'plate', 260.00, 0, 5, 6, now()),
                                                                                                    ('Beef Chili Dry', 'Spicy dry beef strips', 'plate', 270.00, 0, 5, 7, now()),
                                                                                                    ('Beef with Oyster Sauce', 'Tender beef in oyster sauce', 'plate', 280.00, 0, 5, 7, now()),
                                                                                                    ('Prawn Tempura', 'Crispy fried prawns', '5 pcs', 300.00, 0, 5, 8, now()),
                                                                                                    ('Fish in Sweet & Sour Sauce', 'Fish cooked in tangy sauce', 'plate', 270.00, 0, 5, 8, now()),
                                                                                                    ('Vegetable Delight', 'Mixed vegetables stir-fried', 'plate', 180.00, 0, 5, 9, now()),
                                                                                                    ('Tofu Stir Fry', 'Tofu with vegetables in soy sauce', 'plate', 190.00, 0, 5, 9, now()),
                                                                                                    ('Szechuan Chicken', 'Chicken in fiery red Szechuan sauce', 'plate', 280.00, 0, 5, 10, now()),
                                                                                                    ('Szechuan Noodles', 'Spicy noodles with veggies and chicken', 'plate', 250.00, 0, 5, 10, now()),
                                                                                                    ('Thai Basil Chicken', 'Minced chicken with basil', 'plate', 270.00, 0, 5, 11, now()),
                                                                                                    ('Thai Green Curry', 'Coconut-based curry with chicken', 'bowl', 290.00, 0, 5, 11, now()),
                                                                                                    ('Set Menu A', 'Fried rice, Chicken Manchurian, soup', 'set', 380.00, 0, 5, 12, now()),
                                                                                                    ('Set Menu B', 'Fried rice, Beef Chili, soup', 'set', 400.00, 0, 5, 12, now()),
                                                                                                    ('White Rice', 'Plain steamed rice', 'plate', 60.00, 0, 5, 13, now()),
                                                                                                    ('Chicken Curry', 'Traditional chicken curry', 'plate', 180.00, 0, 5, 13, now()),
                                                                                                    ('Beef Rezala', 'Bengali-style beef curry', 'plate', 200.00, 0, 5, 13, now()),
                                                                                                    ('Kacchi Biryani', 'Mutton biryani with egg and potato', 'plate', 320.00, 0, 5, 14, now()),
                                                                                                    ('Chicken Biryani', 'Spiced rice with chicken leg', 'plate', 280.00, 0, 5, 14, now()),
                                                                                                    ('Shingara', 'Savory snack with potato filling', '3 pcs', 30.00, 0, 5, 15, now()),
                                                                                                    ('Alur Chop', 'Fried potato balls', '3 pcs', 30.00, 0, 5, 15, now()),
                                                                                                    ('Ice Cream', '2 scoops of vanilla/chocolate', 'cup', 100.00, 0, 5, 16, now()),
                                                                                                    ('Gulab Jamun', 'Sweet syrupy balls', '2 pcs', 60.00, 0, 5, 16, now()),
                                                                                                    ('Pitha', 'Traditional Bengali rice cake', '2 pcs', 50.00, 0, 5, 17, now()),
                                                                                                    ('Shutki Bhuna', 'Spicy dry fish curry', 'plate', 190.00, 0, 5, 17, now()),
                                                                                                    ('Coke', 'Chilled soft drink', '250ml', 40.00, 0, 5, 18, now()),
                                                                                                    ('Sprite', 'Lemon-lime soda', '250ml', 40.00, 0, 5, 18, now()),
                                                                                                    ('Chinese Special Soup', 'Chef’s special soup mix', 'bowl', 180.00, 0, 5, 19, now()),
                                                                                                    ('Dragon Chicken', 'Signature dragon-style chicken', 'plate', 300.00, 0, 5, 19, now()),
                                                                                                    ('Tandoori Chicken (Full)', 'Grilled whole chicken', 'full', 500.00, 0, 5, 20, now()),
                                                                                                    ('Tandoori Chicken (Half)', 'Grilled half chicken', 'half', 270.00, 0, 5, 20, now()),
                                                                                                    ('Beef Tehari', 'Basmati rice with spicy beef', 'plate', 250.00, 0, 5, 14, now()),
                                                                                                    ('Egg Curry', 'Boiled eggs in curry', 'plate', 140.00, 0, 5, 13, now()),
                                                                                                    ('Dal Fry', 'Yellow lentil cooked with spices', 'bowl', 100.00, 0, 5, 13, now()),
                                                                                                    ('Chicken Pakora', 'Fried chicken fritters', '6 pcs', 160.00, 0, 5, 15, now()),
                                                                                                    ('Chinese Mixed Platter', 'Spring roll, wonton, pakora', 'combo', 300.00, 0, 5, 1, now()),
                                                                                                    ('Chicken Ball', 'Crispy chicken balls', '8 pcs', 220.00, 0, 5, 1, now()),
                                                                                                    ('Paneer Chili', 'Indian cottage cheese in chili sauce', 'plate', 240.00, 0, 5, 9, now());



select * from bill where created_date between '2025-07-25 00:00:01' and '2025-07-25 23:59:59' order by id desc