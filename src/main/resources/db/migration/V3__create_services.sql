CREATE TABLE services (
    id               BIGSERIAL      PRIMARY KEY,
    name             VARCHAR(255)   NOT NULL,
    duration_minutes INT            NOT NULL CHECK (duration_minutes > 0),
    price            NUMERIC(10, 2) NOT NULL CHECK (price >= 0),
    business_id      BIGINT         NOT NULL REFERENCES businesses (id),
    created_at       TIMESTAMP      NOT NULL DEFAULT now()
);