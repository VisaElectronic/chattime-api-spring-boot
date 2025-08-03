CREATE TABLE groups (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    custom_firstname VARCHAR(255),
    custom_lastname VARCHAR(255),
    key VARCHAR(255) UNIQUE NOT NULL,
    photo TEXT,
    status SMALLINT DEFAULT 0,
    is_group SMALLINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);