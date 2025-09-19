SELECT * FROM products WHERE stock > 0;


INSERT INTO cart_items (user_id, product_id, quantity)
VALUES (1, 1, 2) -- Alice 加 2 支 iPhone
ON CONFLICT (user_id, product_id) DO UPDATE SET quantity = cart_items.quantity + EXCLUDED.quantity;
