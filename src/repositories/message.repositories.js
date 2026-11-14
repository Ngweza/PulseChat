const { pool } = require('../config/database');

async function isRoomMember(roomId, userId) {
  const result = await pool.query(
    `SELECT 1
     FROM room_members
     WHERE room_id = $1 AND user_id = $2
     LIMIT 1`,
    [roomId, userId]
  );

  return result.rowCount > 0;
}

async function createMessage(roomId, senderId, content) {
  const result = await pool.query(
    `INSERT INTO messages (room_id, sender_id, content)
     VALUES ($1, $2, $3)
     RETURNING id, room_id, sender_id, content, created_at`,
    [roomId, senderId, content]
  );

  return result.rows[0];
}

async function listMessages(roomId, limit = 50, before = null) {
  const result = await pool.query(
    `SELECT id, room_id, sender_id, content, created_at
     FROM messages
     WHERE room_id = $1
       AND ($2::timestamptz IS NULL OR created_at < $2)
     ORDER BY created_at DESC, id DESC
     LIMIT $3`,
    [roomId, before, limit]
  );

  return result.rows.reverse();
}

module.exports = {
  isRoomMember,
  createMessage,
  listMessages,
};
