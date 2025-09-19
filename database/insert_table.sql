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
