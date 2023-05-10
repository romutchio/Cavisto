CREATE TABLE users (
  id   SERIAL PRIMARY KEY,
  telegram_id INTEGER NOT NULL UNIQUE,
  first_name VARCHAR NULL,
  last_name VARCHAR NULL,
  username VARCHAR NULL UNIQUE,
  created_at timestamp default now()
)

CREATE TABLE advise_history (
  id   SERIAL PRIMARY KEY,
  user_id   INTEGER NOT NULL,
  advise_state jsonb not null default '{}'::jsonb,
  created_at timestamp default now(),
  FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
)

CREATE TABLE notes (
  id   SERIAL PRIMARY KEY,
  user_id   INTEGER NOT NULL,
  wine_name varchar(255) NOT NULL,
  rating decimal NULL,
  price decimal NULL,
  review TEXT NULL,
  created_at timestamp default now(),
  FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
)