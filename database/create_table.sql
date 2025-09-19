-- 使用者（顧客）
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       email VARCHAR(150) UNIQUE NOT NULL,
                       password VARCHAR(200) NOT NULL,
                       phone VARCHAR(20),
                       address TEXT,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 商品分類
CREATE TABLE categories (
                            id SERIAL PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            description TEXT
);

-- 商品
CREATE TABLE products (
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(150) NOT NULL,
                          description TEXT,
                          price NUMERIC(10,2) NOT NULL CHECK (price >= 0),
                          stock INT NOT NULL CHECK (stock >= 0),
                          category_id INT REFERENCES categories(id) ON DELETE SET NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 訂單
CREATE TABLE orders (
                        id SERIAL PRIMARY KEY,
                        user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                        status VARCHAR(20) DEFAULT 'pending',
                        total_amount NUMERIC(10,2) NOT NULL CHECK (total_amount >= 0),
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 訂單明細
CREATE TABLE order_items (
                             id SERIAL PRIMARY KEY,
                             order_id INT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
                             product_id INT NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
                             quantity INT NOT NULL CHECK (quantity > 0),
                             price NUMERIC(10,2) NOT NULL CHECK (price >= 0)
);

-- 購物車
CREATE TABLE cart_items (
                            id SERIAL PRIMARY KEY,
                            user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                            product_id INT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
                            quantity INT NOT NULL CHECK (quantity > 0),
                            UNIQUE(user_id, product_id) -- 一個人對同一商品只能有一筆
);

-- 付款紀錄
CREATE TABLE payments (
                          id SERIAL PRIMARY KEY,
                          order_id INT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
                          payment_method VARCHAR(50) NOT NULL,
                          amount NUMERIC(10,2) NOT NULL CHECK (amount >= 0),
                          status VARCHAR(20) DEFAULT 'success',
                          paid_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
