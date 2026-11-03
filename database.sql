DROP DATABASE IF EXISTS car_service;

CREATE DATABASE IF NOT EXISTS car_service
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE car_service;

CREATE TABLE client (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(80) NOT NULL,
    phone VARCHAR(30) NOT NULL,
    email VARCHAR(120)
);
CREATE TABLE car (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id BIGINT NOT NULL,
    vin VARCHAR(17) NOT NULL UNIQUE,
    brand VARCHAR(60) NOT NULL,
    model VARCHAR(60) NOT NULL,
    year INT NOT NULL,
    FOREIGN KEY (client_id) REFERENCES client(id)
);
CREATE TABLE service_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    car_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL,
    description VARCHAR(500) NOT NULL,
    created_at DATE NOT NULL,
    f1 DOUBLE NOT NULL,
    f2 DOUBLE NOT NULL,
    f3 DOUBLE NOT NULL,
    risk_index DOUBLE NOT NULL,
    FOREIGN KEY (car_id) REFERENCES car(id)
);
CREATE TABLE work (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    name VARCHAR(160) NOT NULL,
    price DOUBLE NOT NULL,
    hours DOUBLE NOT NULL,
    FOREIGN KEY (order_id) REFERENCES service_order(id)
);
INSERT INTO client (full_name, phone, email) VALUES
    ('Іваненко Олександр Петрович', '+380501234567', 'ivanenko@example.com'),
    ('Коваленко Марія Іванівна', '+380671112233', 'kovalenko@example.com'),
    ('Шевченко Андрій Сергійович', '+380931234000', NULL);
INSERT INTO car (client_id, vin, brand, model, year) VALUES
    (1, 'WVWZZZ1JZXW000001', 'Volkswagen', 'Golf', 2015),
    (1, 'JTDKB20U093000002', 'Toyota', 'Yaris', 2018),
    (2, 'WBAVB13506P000003', 'BMW', 'X5', 2012),
    (3, 'KMHDH41EX9U000004', 'Hyundai', 'Elantra', 2020);
INSERT INTO service_order (car_id, status, description, created_at, f1, f2, f3, risk_index) VALUES
    (1, 'СТВОРЕНО', 'Стукіт у передній підвісці', '2026-10-01', 60.0, 70.0, 50.0, 61.0),
    (2, 'ДІАГНОСТОВАНО', 'Планове технічне обслуговування', '2026-10-05', 30.0, 40.0, 20.0, 31.0),
    (3, 'РЕМОНТ', 'Знос гальмівних колодок і дисків', '2026-10-10', 80.0, 60.0, 90.0, 75.5),
    (4, 'ВИКОНАНО', 'Заміна оливи та фільтрів', '2026-10-12', 10.0, 20.0, 15.0, 14.75);
INSERT INTO work (order_id, name, price, hours) VALUES
    (1, 'Діагностика підвіски', 600.0, 1.0),
    (2, 'Заміна оливи та масляного фільтра', 800.0, 0.5),
    (3, 'Заміна гальмівних колодок', 1200.0, 1.5),
    (3, 'Заміна гальмівних дисків', 2500.0, 2.0),
    (4, 'Заміна оливи та фільтрів', 1000.0, 1.0);