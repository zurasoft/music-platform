CREATE TABLE IF NOT EXISTS resources
(
    id         BIGSERIAL PRIMARY KEY,
    audio_data BYTEA NOT NULL
);
