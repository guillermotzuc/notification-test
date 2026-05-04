CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(20),
    topics VARCHAR(255),
    channels VARCHAR(255)
);

-- Create topics lookup table
CREATE TABLE IF NOT EXISTS topics (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- Create notification_channel lookup table
CREATE TABLE IF NOT EXISTS notification_channel (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- Create updated message table with foreign keys to topics and notification_channel
CREATE TABLE IF NOT EXISTS message (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    topic_id INTEGER NOT NULL,
    channel_id INTEGER NOT NULL,
    message TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_message_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_message_topic FOREIGN KEY (topic_id) REFERENCES topics(id) ON DELETE RESTRICT,
    CONSTRAINT fk_message_channel FOREIGN KEY (channel_id) REFERENCES notification_channel(id) ON DELETE RESTRICT
);

-- Create indexes for better performance
CREATE INDEX idx_message_user_id ON message(user_id);
CREATE INDEX idx_message_topic_id ON message(topic_id);
CREATE INDEX idx_message_channel_id ON message(channel_id);
CREATE INDEX idx_message_timestamp ON message(timestamp);
