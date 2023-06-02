DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS feedbacks CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL
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
    item_id BIGINT NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(30),
    CONSTRAINT booking_item FOREIGN KEY (item_id) REFERENCES items(id)
);

INSERT INTO users (name, email) VALUES ('aaa', 'bb@cc.dd');
INSERT INTO items (name, description, is_available, user_id) VALUES ('Дрель', 'Простая дрель', 'true', 1);