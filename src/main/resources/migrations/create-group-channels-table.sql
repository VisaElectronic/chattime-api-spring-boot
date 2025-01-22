CREATE TABLE groups_channels (
    id SERIAL PRIMARY KEY,
    channel_id BIGINT,
    group_id BIGINT,
    status SMALLINT DEFAULT 0,
    CONSTRAINT fk_channel_id FOREIGN KEY (channel_id) REFERENCES channels(id),
    CONSTRAINT fk_group_id FOREIGN KEY (group_id) REFERENCES groups(id)
);