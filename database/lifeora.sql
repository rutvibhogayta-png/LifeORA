CREATE DATABASE IF NOT EXISTS lifeora;
USE lifeora;

CREATE TABLE IF NOT EXISTS Users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS Items (
    item_id INT PRIMARY KEY AUTO_INCREMENT,
    seller_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    `condition` VARCHAR(30) NOT NULL,
    location VARCHAR(100),
    image VARCHAR(255),
    status VARCHAR(30) DEFAULT 'Available',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (seller_id) REFERENCES Users(user_id)
);

CREATE TABLE IF NOT EXISTS Wishlist (
    wishlist_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    item_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, item_id),
    FOREIGN KEY (user_id) REFERENCES Users(user_id),
    FOREIGN KEY (item_id) REFERENCES Items(item_id)
);

INSERT INTO Items
(seller_id,title,description,category,price,`condition`,location,status)
SELECT 1,'Engineering Mathematics Book','Good condition, useful for first-year students.','Books',350,'Good','College','Available'
WHERE EXISTS (SELECT 1 FROM Users WHERE user_id=1)
AND NOT EXISTS (SELECT 1 FROM Items WHERE title='Engineering Mathematics Book');

INSERT INTO Items
(seller_id,title,description,category,price,`condition`,location,status)
SELECT 1,'Scientific Calculator','Working perfectly and lightly used.','Electronics',600,'Good','College','Available'
WHERE EXISTS (SELECT 1 FROM Users WHERE user_id=1)
AND NOT EXISTS (SELECT 1 FROM Items WHERE title='Scientific Calculator');
