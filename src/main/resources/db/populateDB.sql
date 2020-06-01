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
('2020-05-02 07:00:00', 'А_завтрак1', 1000, 100001);

/* НАДО БЫЛО ДЕЛАТЬ ТАК:*/
/*
* Связал поле "user_id" и "id" от табы "users"
* (!) если есть связь между табами, то
      ДРОПАЮТСЯ и ДЕЛИТЯТСЯ табы строго от последних к главной!

	CREATE TABLE meals (
		id          INTEGER PRIMARY KEY DEFAULT nextval('global_seq'),
		user_id     INTEGER   NOT NULL,
		date_time   TIMESTAMP NOT NULL,
		description TEXT      NOT NULL,
		calories    INT       NOT NULL,
		FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
	);
	CREATE UNIQUE INDEX meals_unique_user_datetime_idx
		ON meals (user_id, date_time);
*/