CREATE TABLE messages (
    id SERIAL PRIMARY KEY,
    content TEXT,
    group_id BIGINT NOT NULL,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_group_id FOREIGN KEY (group_id) REFERENCES groups(id),
    CONSTRAINT fk_created_by FOREIGN KEY (created_by) REFERENCES users(id)
);