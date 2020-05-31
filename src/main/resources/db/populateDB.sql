DELETE FROM user_roles;
DELETE FROM users;
DELETE FROM meals;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password) VALUES
  ('User', 'user@yandex.ru', 'password'),
  ('Admin', 'admin@gmail.com', 'admin');

INSERT INTO user_roles (role, user_id) VALUES
  ('ROLE_USER', 100000),
  ('ROLE_ADMIN', 100001);

INSERT INTO meals (date_time, description, calories, owner_id) VALUES
    ('2020-05-02 07:00:00', 'завтрак1', 1000, 100000),
    ('2020-05-02 12:00:00', 'обед1', 1000, 100000),
    ('2020-05-02 18:00:00', 'ужин1', 1000, 100000),
    ('2020-05-03 07:00:00', 'завтрак2', 1000, 100000),
    ('2020-05-03 12:00:00', 'обед2', 1000, 100000),
    ('2020-05-03 18:00:00', 'ужин2', 1000, 100000),
    ('2020-05-04 07:00:00', 'завтрак3', 1000, 100000),
    ('2020-05-04 12:00:00', 'обед3', 1000, 100000),
    ('2020-05-04 18:00:00', 'ужин3', 1000, 100000);