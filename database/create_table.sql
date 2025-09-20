-- 使用者（顧客）
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(100), -- 改成可為 NULL
                       email VARCHAR(150) UNIQUE NOT NULL,
                       password VARCHAR(200) NOT NULL,
                       phone VARCHAR(20),
                       address TEXT,
                       role VARCHAR(50) DEFAULT 'USER', -- 新增角色欄位
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

-- 購物車 (支援登入 + 未登入)
CREATE TABLE cart_items (
                            id SERIAL PRIMARY KEY,
                            user_id INT REFERENCES users(id) ON DELETE CASCADE, -- 登入用戶
                            session_id VARCHAR(100),                            -- 未登入訪客
                            product_id INT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
                            quantity INT NOT NULL CHECK (quantity > 0),
                            CONSTRAINT unique_cart UNIQUE (user_id, session_id, product_id),
                            CONSTRAINT cart_user_or_session CHECK (
                                (user_id IS NOT NULL) OR (session_id IS NOT NULL)
                                )
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


ALTER TABLE users
    ADD COLUMN role VARCHAR(50) DEFAULT 'USER';


-- 加一個 session_id 欄位 (給未登入訪客用)
ALTER TABLE cart_items
    ADD COLUMN session_id VARCHAR(100);

-- 修改 unique 條件：一個 user 或一個 session 對同商品只能有一筆
-- 先刪掉舊的 unique(user_id, product_id)
ALTER TABLE cart_items
    DROP CONSTRAINT cart_items_user_id_product_id_key;

-- 新增新的 unique constraint
ALTER TABLE cart_items
    ADD CONSTRAINT unique_cart UNIQUE (user_id, session_id, product_id);

-- 確保 user_id 和 session_id 至少有一個不為 NULL
ALTER TABLE cart_items
    ADD CONSTRAINT cart_user_or_session CHECK (
        (user_id IS NOT NULL) OR (session_id IS NOT NULL)
        );

ALTER TABLE cart_items ALTER COLUMN user_id DROP NOT NULL;


ALTER TABLE products
    ADD COLUMN image_url VARCHAR(255);


ALTER TABLE orders
    ADD COLUMN shipping_name VARCHAR(100) ,          -- 收件人姓名
    ADD COLUMN shipping_phone VARCHAR(20) ,          -- 收件人電話
    ADD COLUMN shipping_address TEXT ,               -- 收件地址
    -- ADD COLUMN shipping_zip VARCHAR(20),                     -- 郵遞區號
    ADD COLUMN shipping_note TEXT,                           -- 備註
    ADD COLUMN shipping_method VARCHAR(50) DEFAULT 'home',   -- 配送方式 (home, store_pickup, ...)
    ADD COLUMN shipping_status VARCHAR(20) DEFAULT 'pending',-- 配送狀態 (pending, shipped, delivered, returned)
    ADD COLUMN tracking_number VARCHAR(100),                 -- 物流單號
    ADD COLUMN courier VARCHAR(100);                         -- 物流公司


ALTER TABLE orders
    DROP COLUMN tracking_number,
    DROP COLUMN courier,
    DROP COLUMN shipping_zip;
