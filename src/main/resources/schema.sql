DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS feedbacks CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE

);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(500),
    is_available BOOLEAN NOT NULL,
    user_id BIGINT NOT NULL,
    request_id BIGINT,
    CONSTRAINT items_owner FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS feedbacks (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    item_id BIGINT,
    CONSTRAINT feedbacks_item FOREIGN KEY (item_id) REFERENCES items(id)
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    item_id BIGINT NOT NULL,
    booker_id BIGINT NOT NULL,
    status VARCHAR(30),
    CONSTRAINT booking_item FOREIGN KEY (item_id) REFERENCES items(id),
    CONSTRAINT booking_booker FOREIGN KEY (booker_id) REFERENCES users(id)
);

INSERT INTO users (name, email) VALUES ('updateName', 'updateName@user.com');--1
INSERT INTO users (name, email) VALUES ('updateName2', 'updateName2@user.com');
DELETE FROM users WHERE id = 2;
INSERT INTO users (name, email) VALUES ('updateName3', 'updateName3@user.com');
DELETE FROM users WHERE id = 3;
INSERT INTO users (name, email) VALUES ('user', 'user@user.com');--4
INSERT INTO items (name, description, is_available, user_id) VALUES ('Аккумуляторная дрель', 'Аккумуляторная дрель + аккумулятор', 'true', 1); --1
INSERT INTO items (name, description, is_available, user_id) VALUES ('Отвертка', 'Аккумуляторная отвертка', 'false', 4); --2
INSERT INTO items (name, description, is_available, user_id) VALUES ('Клей Момент', 'Тюбик суперклея марки Момент', 'true', 4); --3
