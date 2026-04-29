CREATE TYPE user_role AS ENUM ('ADMIN', 'CUSTOMER');

CREATE TABLE users (
    id         BIGSERIAL PRIMARY KEY,
    email      VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    role       user_role    NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);