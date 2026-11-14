const { Pool } = require('pg');

const pool = new Pool({
  host: process.env.PGHOST || '127.0.0.1',
  port: Number(process.env.PGPORT || 5434),
  database: process.env.PGDATABASE || 'chatdb',
  user: process.env.PGUSER || 'chatuser',
  password: process.env.POSTGRES_PASSWORD,
  connectionTimeoutMillis: 5000,
});

pool.on('error', (err) => {
  console.error('Erro inesperado no PostgreSQL:', err.message);
});

async function checkDatabase() {
  const result = await pool.query('SELECT NOW() AS time');
  return result.rows[0];
}

module.exports = { pool, checkDatabase };
