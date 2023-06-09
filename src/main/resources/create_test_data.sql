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

INSERT INTO item_requests (requester_id, description, created) VALUES
(1, 'Хотел бы воспользоваться щёткой для обуви', '2023-06-14 17:11:48.718206'),
(4, 'Хотел бы воспользоваться УШМ', '2023-06-15 17:11:48.718206'),
(4, 'Нужен суперклей', '2023-06-15 18:11:48.718206'),
(5, 'Ищу дрель-шуруповерт', '2023-06-15 18:11:48.718206');

INSERT INTO items (name, description, is_available, user_id, request_id) VALUES
('Аккумуляторная дрель', 'Аккумуляторная дрель + аккумулятор', true, 1, null), -- 1
('Отвертка', 'Аккумуляторная отвертка', true, 4, null), -- 2
('Клей Момент', 'Тюбик суперклея марки Момент', true, 4, null), -- 3
('Кухонный стол', 'Стол для празднования', true, 6, null), -- 4
('Щётка для обуви', 'Стандартная щётка для обуви', true, 4, 1); -- 5

INSERT INTO bookings (start_date, end_date, item_id, booker_id, status) VALUES
('2023-06-14 14:25:34', '2023-06-14 14:25:35', 2, 1, 'APPROVED'),-- 1, owner - 4
('2023-06-15 14:25:32', '2023-06-16 14:25:32', 2, 1, 'APPROVED'),-- 2, owner - 4
('2023-06-15 14:25:34', '2023-06-15 15:25:34', 1, 4, 'REJECTED'),-- 3, owner - 1
('2023-06-14 15:25:34', '2023-06-14 16:25:34', 2, 5, 'APPROVED'),-- 4, owner - 4
('2023-06-14 14:25:42', '2023-06-15 14:25:39', 3, 1, 'REJECTED'),-- 5, owner - 4
('2023-06-14 14:25:42', '2023-06-14 14:25:43', 2, 1, 'APPROVED'),-- 6, owner - 4
('2023-06-24 14:25:40', '2023-06-25 14:25:40', 1, 5, 'APPROVED'),-- 7, owner - 1
('2023-06-14 14:25:42', '2023-06-14 15:25:40', 4, 1, 'APPROVED');-- 8, owner - 6

INSERT INTO comments (text, item_id, author_id, created) VALUES
('Add comment from user1', 2, 1, '2023-06-14 14:25:48.627901'),
('Add comment from user5', 2, 5, '2023-06-15 14:25:48.627901');;