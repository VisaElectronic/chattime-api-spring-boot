CREATE TABLE groups_channels (
    id SERIAL PRIMARY KEY,
    channel_id BIGINT,
    group_id BIGINT,
    role INT,
    last_message_id BIGINT,
    unread INT,
    display_order INT,
    status SMALLINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_channel_id FOREIGN KEY (channel_id) REFERENCES channels(id),
    CONSTRAINT fk_group_id FOREIGN KEY (group_id) REFERENCES groups(id),
    CONSTRAINT fk_last_message_id FOREIGN KEY (last_message_id) REFERENCES messages(id)
);