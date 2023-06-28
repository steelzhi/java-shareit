DROP TABLE IF EXISTS users, item_requests, items, bookings, comments CASCADE;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS item_requests (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    requester_id BIGINT NOT NULL,
    description VARCHAR(1000) NOT NULL,
    created TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(500),
    is_available BOOLEAN NOT NULL,
    user_id BIGINT NOT NULL,
    request_id BIGINT,
    CONSTRAINT items_owner FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT items_item_request FOREIGN KEY (request_id) REFERENCES item_requests(id) ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    item_id BIGINT NOT NULL,
    booker_id BIGINT NOT NULL,
    status VARCHAR(30),
    CONSTRAINT booking_item FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT booking_booker FOREIGN KEY (booker_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    text VARCHAR(1000) NOT NULL,
    item_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    created TIMESTAMP NOT NULL,
    CONSTRAINT comment_item FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT comment_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO users (name, email) VALUES
('updateName', 'updateName@user.com'), -- 1
('updateName2', 'updateName2@user.com'), -- 2, под удаление
('updateName3', 'updateName3@user.com'); -- 3, под удаление
DELETE FROM users WHERE id = 2;
DELETE FROM users WHERE id = 3;
INSERT INTO users (name, email) VALUES
('user', 'user@user.com'), -- 4
('other', 'other@other.com'), -- 5
('practicum', 'practicum@yandex.ru'); -- 6

INSERT INTO items (name, description, is_available, user_id, request_id) VALUES
('Аккумуляторная дрель', 'Аккумуляторная дрель + аккумулятор', true, 1, null), -- 1
('Отвертка', 'Аккумуляторная отвертка', true, 4, null), -- 2
('Клей Момент', 'Тюбик суперклея марки Момент', true, 4, null); -- 3

INSERT INTO bookings (start_date, end_date, item_id, booker_id, status) VALUES
('2023-06-14 14:25:34', '2023-06-14 14:25:35', 2, 1, 'APPROVED'),-- 1, owner - 4
('2023-06-15 14:25:32', '2023-06-16 14:25:32', 2, 1, 'APPROVED'),-- 2, owner - 4
('2023-06-15 14:25:34', '2023-06-15 15:25:34', 1, 4, 'REJECTED'),-- 3, owner - 1
('2023-06-14 15:25:34', '2023-06-14 16:25:34', 2, 5, 'APPROVED'),-- 4, owner - 4
('2023-06-14 14:25:42', '2023-06-15 14:25:39', 3, 1, 'WAITING');-- 5, owner - 4