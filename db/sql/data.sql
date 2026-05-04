-- data.sql - works with your current VARCHAR columns
INSERT INTO users (name, email, phone_number, topics, channels) VALUES
('Alice Johnson', 'alice.johnson@example.com', '+1-555-123-4567', 'Sports', 'SMS,E-Mail,Push'),
('Brian Smith', 'brian.smith@example.com', '+44-7700-123456', 'Finance', 'E-Mail,Push'),
('Clara Wong', 'clara.wong@example.com', '+61-412-345-678', 'Sports,Finance', 'SMS,E-Mail');

-- Insert default topics
INSERT INTO topics (name) VALUES
('Sports'),
('Technology');

-- Insert default notification channels
INSERT INTO notification_channel (name) VALUES
('SMS'),
('E-Mail'),
('Push Notification');
