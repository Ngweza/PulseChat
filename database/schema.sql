BEGIN;

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) NOT NULL,
    password_hash TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT users_username_length
        CHECK (char_length(username) BETWEEN 3 AND 50)
);

CREATE UNIQUE INDEX IF NOT EXISTS users_username_unique
    ON users (LOWER(username));

CREATE TABLE IF NOT EXISTS rooms (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    created_by UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT rooms_name_not_empty
        CHECK (char_length(TRIM(name)) > 0)
);

CREATE TABLE IF NOT EXISTS room_members (
    room_id UUID NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(20) NOT NULL DEFAULT 'member',
    joined_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (room_id, user_id),
    CONSTRAINT room_members_role_valid
        CHECK (role IN ('member', 'admin'))
);

CREATE TABLE IF NOT EXISTS messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    room_id UUID NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
    sender_id UUID NOT NULL REFERENCES users(id),
    content TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    edited_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    CONSTRAINT messages_content_length
        CHECK (char_length(TRIM(content)) BETWEEN 1 AND 10000)
);

CREATE INDEX IF NOT EXISTS rooms_created_by_idx
    ON rooms (created_by);

CREATE INDEX IF NOT EXISTS room_members_user_idx
    ON room_members (user_id);

CREATE INDEX IF NOT EXISTS messages_room_created_idx
    ON messages (room_id, created_at DESC, id DESC);

CREATE INDEX IF NOT EXISTS messages_sender_idx
    ON messages (sender_id);

COMMIT;
