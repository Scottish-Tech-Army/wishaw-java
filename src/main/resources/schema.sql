-- Centre
CREATE TABLE IF NOT EXISTS centre (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(20) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Users
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    display_name VARCHAR(100),
    dob DATE,
    role VARCHAR(20) NOT NULL,
    centre_id BIGINT REFERENCES centre(id),
    profile_image_url VARCHAR(1000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Badge (core badges)
CREATE TABLE IF NOT EXISTS badge (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Level (data-driven level thresholds)
CREATE TABLE IF NOT EXISTS level (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    min_points INT NOT NULL,
    max_points INT NOT NULL,
    display_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Module (12-16 week game modules e.g. Minecraft, Rocket League, Fortnite)
CREATE TABLE IF NOT EXISTS module (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    approved BOOLEAN DEFAULT FALSE,
    centre_id BIGINT REFERENCES centre(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Sub-badge / Challenge (~15 per module)
CREATE TABLE IF NOT EXISTS sub_badge (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    points INT NOT NULL DEFAULT 0,
    badge_id BIGINT REFERENCES badge(id),
    module_id BIGINT REFERENCES module(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Game Group (centre grouping)
CREATE TABLE IF NOT EXISTS game_group (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    centre_id BIGINT REFERENCES centre(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Group membership (many-to-many users <-> game_group)
CREATE TABLE IF NOT EXISTS group_member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    game_group_id BIGINT REFERENCES game_group(id),
    user_id BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (game_group_id, user_id)
);

-- User progress per badge (XP accumulation)
CREATE TABLE IF NOT EXISTS user_progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    badge_id BIGINT REFERENCES badge(id),
    total_points INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, badge_id)
);

-- Sub-badge completion tracking
CREATE TABLE IF NOT EXISTS sub_badge_completion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    sub_badge_id BIGINT REFERENCES sub_badge(id),
    completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, sub_badge_id)
);

-- Legacy points (initial/historical points per user per badge, no sub-badge breakdown)
CREATE TABLE IF NOT EXISTS legacy_points (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    badge_id BIGINT REFERENCES badge(id),
    points INT NOT NULL DEFAULT 0,
    reason VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, badge_id)
);
