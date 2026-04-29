CREATE TYPE booking_status AS ENUM ('BOOKED', 'CANCELLED');

CREATE TABLE bookings (
    id          BIGSERIAL      PRIMARY KEY,
    service_id  BIGINT         NOT NULL REFERENCES services (id),
    customer_id BIGINT         NOT NULL REFERENCES users (id),
    start_time  TIMESTAMP      NOT NULL,
    end_time    TIMESTAMP      NOT NULL,
    status      booking_status NOT NULL DEFAULT 'BOOKED',
    created_at  TIMESTAMP      NOT NULL DEFAULT now(),
    CONSTRAINT no_overlap EXCLUDE USING gist (
        service_id WITH =,
        tsrange(start_time, end_time) WITH &&
    ) WHERE (status = 'BOOKED')
);

CREATE EXTENSION IF NOT EXISTS btree_gist;