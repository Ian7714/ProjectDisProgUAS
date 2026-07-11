-- =========================================================
-- Food Reservation and Ordering System
-- Database Schema (MySQL)
-- =========================================================

CREATE DATABASE IF NOT EXISTS db_food_reservation;
USE db_food_reservation;

-- ---------------------------------------------------------
-- 1. USERS  (User Management)
-- ---------------------------------------------------------
CREATE TABLE users (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password      VARCHAR(100) NOT NULL,
    full_name     VARCHAR(100) NOT NULL,
    email         VARCHAR(100) NOT NULL UNIQUE,
    role          ENUM('ADMIN','CUSTOMER') NOT NULL DEFAULT 'CUSTOMER',
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Akun admin default (password: admin123 - sudah di-hash SHA-256 di aplikasi)
INSERT INTO users (username, password, full_name, email, role)
VALUES ('admin', SHA2('admin123', 256), 'Administrator', 'admin@resto.com', 'ADMIN');

-- ---------------------------------------------------------
-- 2. RESTAURANT TABLES (Table Management)
-- ---------------------------------------------------------
CREATE TABLE restaurant_tables (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    table_number  VARCHAR(10) NOT NULL UNIQUE,
    capacity      INT NOT NULL,
    status        ENUM('AVAILABLE','RESERVED','OCCUPIED') NOT NULL DEFAULT 'AVAILABLE'
);

INSERT INTO restaurant_tables (table_number, capacity, status) VALUES
('T1', 2, 'AVAILABLE'),
('T2', 2, 'AVAILABLE'),
('T3', 4, 'AVAILABLE'),
('T4', 4, 'AVAILABLE'),
('T5', 6, 'AVAILABLE'),
('T6', 8, 'AVAILABLE');

-- ---------------------------------------------------------
-- 3. MENU ITEMS (Menu Management)
-- ---------------------------------------------------------
CREATE TABLE menu_items (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    category      ENUM('FOOD','DRINK') NOT NULL,
    price         DECIMAL(10,2) NOT NULL,
    description   VARCHAR(255),
    available     BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO menu_items (name, category, price, description) VALUES
('Nasi Goreng Spesial', 'FOOD', 35000, 'Nasi goreng dengan telur dan ayam suwir'),
('Ayam Bakar Madu', 'FOOD', 42000, 'Ayam bakar dengan bumbu madu khas'),
('Mie Ayam Jamur', 'FOOD', 28000, 'Mie ayam dengan topping jamur'),
('Es Teh Manis', 'DRINK', 8000, 'Teh manis dingin segar'),
('Jus Alpukat', 'DRINK', 15000, 'Jus alpukat murni tanpa campuran');

-- ---------------------------------------------------------
-- 4. RESERVATIONS (Reservation System)
-- ---------------------------------------------------------
CREATE TABLE reservations (
    id                INT AUTO_INCREMENT PRIMARY KEY,
    user_id           INT NOT NULL,
    table_id          INT NOT NULL,
    reservation_date  DATE NOT NULL,
    reservation_time  TIME NOT NULL,
    guest_count       INT NOT NULL,
    status            ENUM('PENDING','CONFIRMED','CANCELLED','COMPLETED') NOT NULL DEFAULT 'CONFIRMED',
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (table_id) REFERENCES restaurant_tables(id),
    -- Mencegah double booking pada meja, tanggal, dan jam yang sama
    UNIQUE KEY uq_table_datetime (table_id, reservation_date, reservation_time)
);

-- ---------------------------------------------------------
-- 5. FOOD ORDERS (Food Ordering System)
-- ---------------------------------------------------------
CREATE TABLE food_orders (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    reservation_id INT NOT NULL,
    status         ENUM('PENDING','PREPARING','READY','SERVED') NOT NULL DEFAULT 'PENDING',
    total_amount   DECIMAL(12,2) NOT NULL DEFAULT 0,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reservation_id) REFERENCES reservations(id)
);

CREATE TABLE order_items (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    order_id    INT NOT NULL,
    menu_id     INT NOT NULL,
    quantity    INT NOT NULL,
    subtotal    DECIMAL(12,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES food_orders(id),
    FOREIGN KEY (menu_id) REFERENCES menu_items(id)
);

-- ---------------------------------------------------------
-- Index tambahan untuk pencarian riwayat reservasi
-- ---------------------------------------------------------
CREATE INDEX idx_reservation_user ON reservations(user_id);
CREATE INDEX idx_reservation_date ON reservations(reservation_date);