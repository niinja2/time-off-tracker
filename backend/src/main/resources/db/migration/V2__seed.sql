-- D16: every user's password is "password" (BCrypt); everyone hired 2020-01-01 except Tom.
INSERT INTO users (username, name, password_hash, admin, hire_date) VALUES
    ('ana',   'Ana',   '$2a$10$pJSxf6pY88we8V2CzQilu.IHqp4Xt1FDO28KeZhCIn0JinOTXa1l6', FALSE, '2020-01-01'),
    ('ivan',  'Ivan',  '$2a$10$pJSxf6pY88we8V2CzQilu.IHqp4Xt1FDO28KeZhCIn0JinOTXa1l6', FALSE, '2020-01-01'),
    ('marko', 'Marko', '$2a$10$pJSxf6pY88we8V2CzQilu.IHqp4Xt1FDO28KeZhCIn0JinOTXa1l6', FALSE, '2020-01-01'),
    ('petra', 'Petra', '$2a$10$pJSxf6pY88we8V2CzQilu.IHqp4Xt1FDO28KeZhCIn0JinOTXa1l6', FALSE, '2020-01-01'),
    ('luka',  'Luka',  '$2a$10$pJSxf6pY88we8V2CzQilu.IHqp4Xt1FDO28KeZhCIn0JinOTXa1l6', FALSE, '2020-01-01'),
    ('sara',  'Sara',  '$2a$10$pJSxf6pY88we8V2CzQilu.IHqp4Xt1FDO28KeZhCIn0JinOTXa1l6', FALSE, '2020-01-01'),
    ('tom',   'Tom',   '$2a$10$pJSxf6pY88we8V2CzQilu.IHqp4Xt1FDO28KeZhCIn0JinOTXa1l6', FALSE, '2026-06-01'),
    ('eva',   'Eva',   '$2a$10$pJSxf6pY88we8V2CzQilu.IHqp4Xt1FDO28KeZhCIn0JinOTXa1l6', TRUE,  '2020-01-01');

INSERT INTO teams (name, lead_id)
SELECT 'Platform', id FROM users WHERE username = 'ana';

INSERT INTO teams (name, lead_id)
SELECT 'Mobile', id FROM users WHERE username = 'luka';

-- Q3: a lead is also a member of the team they lead.
INSERT INTO team_members (team_id, user_id)
SELECT t.id, u.id
FROM teams t
JOIN users u ON u.username IN ('ana', 'ivan', 'marko', 'petra')
WHERE t.name = 'Platform';

INSERT INTO team_members (team_id, user_id)
SELECT t.id, u.id
FROM teams t
JOIN users u ON u.username IN ('luka', 'sara', 'tom')
WHERE t.name = 'Mobile';
