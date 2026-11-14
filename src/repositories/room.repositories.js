const { pool } = require('../config/database');

async function createRoom(name, userId) {
  const client = await pool.connect();

  try {
    await client.query('BEGIN');

    const roomResult = await client.query(
      `INSERT INTO rooms (name, created_by)
       VALUES ($1, $2)
       RETURNING id, name, created_by, created_at`,
      [name, userId]
    );

    const room = roomResult.rows[0];

    await client.query(
      `INSERT INTO room_members (room_id, user_id, role)
       VALUES ($1, $2, 'admin')`,
      [room.id, userId]
    );

    await client.query('COMMIT');
    return room;
  } catch (error) {
    await client.query('ROLLBACK');
    throw error;
  } finally {
    client.release();
  }
}

async function listRoomsForUser(userId) {
  const result = await pool.query(
    `SELECT r.id, r.name, r.created_by, r.created_at, rm.role
     FROM rooms r
     INNER JOIN room_members rm ON rm.room_id = r.id
     WHERE rm.user_id = $1
     ORDER BY r.created_at DESC`,
    [userId]
  );

  return result.rows;
}

module.exports = {
  createRoom,
  listRoomsForUser,
};
