CREATE TABLE businesses (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    owner_id   BIGINT       NOT NULL REFERENCES users (id),
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);