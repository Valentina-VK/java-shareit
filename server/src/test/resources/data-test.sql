INSERT INTO users (id, name, email)
VALUES (11, 'user1', 'user11@yandex.ru'),
       (12, 'user2', 'user12@yandex.ru'),
       (13, 'user3', 'user13@yandex.ru');

INSERT INTO item_requests (id, requestor_id, description, created)
VALUES (21, 11, 'description1', TIMESTAMP '2025-04-25 10:10:10'),
       (22, 12, 'description2', TIMESTAMP '2025-04-24 20:20:20');

INSERT INTO items (id, owner_id, name, description, available, request_id)
VALUES (31, 11, 'item1', 'description1', 'true', null),
       (32, 11, 'item2', 'description2', 'true', null),
       (33, 11, 'item3', 'description3', 'false', null);

INSERT INTO bookings (id, booker_id, item_id, start_date, end_date, status)
VALUES (41, 12, 31, TIMESTAMP '2025-03-25 15:15:15', TIMESTAMP '2025-03-29 17:10:10', 'APPROVED'),
       (42, 12, 31, CAST(CURRENT_DATE AS TIMESTAMP) + INTERVAL '1' DAY, CAST(CURRENT_DATE AS TIMESTAMP)
                           + INTERVAL '2' DAY, 'APPROVED'),
       (43, 12, 32, CAST(CURRENT_DATE AS TIMESTAMP) + INTERVAL '1' DAY, CAST(CURRENT_DATE AS TIMESTAMP)
                           + INTERVAL '2' DAY, 'APPROVED'),
       (44, 13, 33, TIMESTAMP '2025-06-25 15:15:15', TIMESTAMP '2026-03-14 17:10:10', 'WAITING'),
       (45, 12, 33, TIMESTAMP '2025-02-25 15:15:15', TIMESTAMP '2025-07-14 17:10:10', 'REJECTED'),
       (46, 12, 31, TIMESTAMP '2025-03-25 15:15:15', TIMESTAMP '2026-04-25 17:10:10', 'CANCELED');

INSERT INTO comments (id, item_id, user_id, text, created)
    VALUES (111, 31, 12, 'test comment', TIMESTAMP '2025-03-30 15:15:15');