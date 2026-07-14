-- Bookings: a booked slot against an event type
CREATE TABLE bookings (
    id            BIGSERIAL PRIMARY KEY,
    event_type_id BIGINT       NOT NULL REFERENCES event_types (id) ON DELETE CASCADE,
    booker_name   VARCHAR(255) NOT NULL,
    booker_email  VARCHAR(255) NOT NULL,
    start_at      TIMESTAMPTZ  NOT NULL,
    end_at        TIMESTAMPTZ  NOT NULL,
    status        VARCHAR(32)  NOT NULL DEFAULT 'CONFIRMED',
    notes         TEXT,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT chk_bookings_time_order CHECK (start_at < end_at)
);

CREATE INDEX idx_bookings_event_type_id ON bookings (event_type_id);
CREATE INDEX idx_bookings_start_at ON bookings (start_at);
