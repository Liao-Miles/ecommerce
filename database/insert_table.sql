INSERT INTO categories (name, description) VALUES
                                               ('Electronics', 'Phones, Laptops, Accessories'),
                                               ('Clothing', 'Shirts, Pants, Jackets'),
                                               ('Books', 'Fiction, Non-fiction, Textbooks');

INSERT INTO products (name, description, price, stock, category_id) VALUES
                                                                        ('iPhone 15', 'Apple smartphone', 32900, 10, 1),
                                                                        ('Gaming Laptop', 'High performance laptop', 49900, 5, 1),
                                                                        ('T-Shirt', 'Cotton shirt', 390, 50, 2),
                                                                        ('Novel Book', 'A good read', 250, 100, 3);

INSERT INTO users (name, email, password, phone, address) VALUES
                                                              ('Alice', 'alice@example.com', 'hashed_pw1', '0912345678', 'Taipei'),
                                                              ('Bob', 'bob@example.com', 'hashed_pw2', '0922333444', 'Kaohsiung');



INSERT INTO products (name, description, price, stock, category_id) VALUES
-- 電子產品 (1)
('遊戲筆電', '高效能電競筆電', 49900, 5, 1),
('三星 Galaxy S24', '旗艦級安卓手機', 28900, 8, 1),
('Sony WH-1000XM5', '無線降噪耳機', 10900, 15, 1),
('iPad Air 平板', '蘋果平板電腦', 19900, 12, 1),
('MacBook Pro 14 吋', '專業用蘋果筆電', 62900, 6, 1),
('羅技 MX Master 3S', '無線人體工學滑鼠', 3200, 20, 1),
('華碩 ROG 顯示器', '27 吋 2K 165Hz 螢幕', 13900, 7, 1),
('GoPro Hero 12', '運動攝影機', 14500, 9, 1),

-- 服飾 (2)
('牛仔褲', '藍色丹寧牛仔褲', 1200, 40, 2),
('運動鞋', '輕便跑步鞋', 2500, 25, 2),
('外套', '保暖冬季外套', 3200, 10, 2),
('連帽上衣', '休閒棉質帽 T', 1500, 35, 2),
('西裝', '正式商務西裝', 5800, 8, 2),
('洋裝', '夏季碎花洋裝', 2200, 20, 2),
('棒球帽', '可調整棉質帽', 450, 60, 2),
('襪子組', '五雙入純棉襪', 300, 100, 2),

-- 書籍 (3)
('Java 資料結構', '程式設計教材', 850, 30, 3),
('哈利波特全集', '奇幻小說套書', 2800, 20, 3),
('自我成長指南', '勵志書籍', 500, 50, 3),
('Spring Boot 實戰', 'Java 框架入門書', 1200, 15, 3),
('乾淨的程式碼', '程式設計最佳實務', 1350, 18, 3),
('新手料理書', '簡單食譜大全', 600, 40, 3),
('世界歷史地圖集', '歷史參考書籍', 1800, 12, 3),
('兒童故事書', '彩圖床邊故事', 350, 70, 3);
