-- Event types: bookable meeting definitions (e.g. "30-min intro call")
CREATE TABLE event_types (
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT       NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    name             VARCHAR(255) NOT NULL,
    slug             VARCHAR(255) NOT NULL,
    description      TEXT,
    duration_minutes INTEGER      NOT NULL,
    active           BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_event_types_user_slug UNIQUE (user_id, slug)
);

CREATE INDEX idx_event_types_user_id ON event_types (user_id);
