CREATE TABLE messages (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    content TEXT,
    channel_id BIGINT NOT NULL,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_channel_id FOREIGN KEY (channel_id) REFERENCES channels(id),
    CONSTRAINT fk_created_by FOREIGN KEY (created_by) REFERENCES users(id)
);