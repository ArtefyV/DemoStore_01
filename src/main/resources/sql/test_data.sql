DELETE FROM order_items;
DELETE FROM orders;
DELETE FROM products;

MERGE INTO products (id, name, price, stock, version)
KEY(id)
VALUES
(1, 'Pilsner Urquell světlý ležák', 31.90, 10, 0),
(2, 'Velkopopovický Kozel 11', 19.90, 15, 0),
(3, 'Radegast Ryze hořká 12', 23.90, 30, 0),
(4, 'Svijanský Máz', 18.90, 25, 0),
(5, 'Krušovice Bohém', 15.90, 40, 0);

ALTER TABLE products ALTER COLUMN id RESTART WITH 6;

MERGE INTO orders (id, created_at, paid) KEY(id) VALUES (1,  NOW(), true);
MERGE INTO orders (id, created_at, paid) KEY(id) VALUES (2,  NOW(), false);

ALTER TABLE orders ALTER COLUMN id RESTART WITH 3;

MERGE INTO order_items (order_id, product_id, quantity) KEY(order_id, product_id) VALUES (1, 3, 3);
MERGE INTO order_items (order_id, product_id, quantity) KEY(order_id, product_id) VALUES (2, 1, 2);
MERGE INTO order_items (order_id, product_id, quantity) KEY(order_id, product_id) VALUES (2, 2, 1);

