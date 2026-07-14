-- Availability rules: weekly recurring working hours per user
-- day_of_week: 1 = Monday ... 7 = Sunday (ISO-8601)
CREATE TABLE availability_rules (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT      NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    day_of_week SMALLINT    NOT NULL CHECK (day_of_week BETWEEN 1 AND 7),
    start_time  TIME        NOT NULL,
    end_time    TIME        NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_availability_time_order CHECK (start_time < end_time)
);

CREATE INDEX idx_availability_rules_user_id ON availability_rules (user_id);
