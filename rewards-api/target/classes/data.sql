-- Customers
INSERT INTO customers (id, customer_name, customer_email,customer_contactNumber) VALUES (1, 'Ram Kumar', 'ramkumar@example.com',"1234567890");
INSERT INTO customers (id, customer_name, customer_email,customer_contactNumber) VALUES (2, 'Jane Smith', 'jane@example.com',"212344221");

-- Transactions for ram kumar
INSERT INTO transactions (id, amount, transaction_date, customer_id) VALUES (1, 120, '2025-06-15', 1);
INSERT INTO transactions (id, amount, transaction_date, customer_id) VALUES (2, 75, '2025-07-05', 1);
INSERT INTO transactions (id, amount, transaction_date, customer_id) VALUES (3, 200, '2025-08-20', 1);

-- Transactions for Jane
INSERT INTO transactions (id, amount, transaction_date, customer_id) VALUES (4, 60, '2025-06-10', 2);
INSERT INTO transactions (id, amount, transaction_date, customer_id) VALUES (5, 155, '2025-07-22', 2);
