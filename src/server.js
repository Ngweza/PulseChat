require('dotenv').config();

const http = require('http');
const { Server } = require('socket.io');
const { createAdapter } = require('@socket.io/redis-adapter');

const { configureSockets } = require('./sockets');

const app = require('./app');
const { pool } = require('./config/database');
const { pubClient, subClient, connectRedis } = require('./config/redis');

const PORT = Number(process.env.PORT || 3000);
const server = http.createServer(app);

const io = new Server(server, {
  cors: {
    origin: process.env.CORS_ORIGIN || '*',
  },
});

async function start() {
  try {
    await pool.query('SELECT 1');
    console.log('PostgreSQL ligado.');

    await connectRedis();
    io.adapter(createAdapter(pubClient, subClient));
    configureSockets(io);
    console.log('Redis ligado; adaptador Socket.IO configurado.');

    server.listen(PORT, '0.0.0.0', () => {
      console.log(`Backend a escutar na porta ${PORT}.`);
    });
  } catch (error) {
    console.error('Não foi possível iniciar o backend:', error.message);
    process.exitCode = 1;
    await Promise.allSettled([
      pool.end(),
      pubClient.isOpen ? pubClient.quit() : Promise.resolve(),
      subClient.isOpen ? subClient.quit() : Promise.resolve(),
    ]);
  }
}

async function shutdown() {
  io.close();
  await Promise.allSettled([
    pool.end(),
    pubClient.isOpen ? pubClient.quit() : Promise.resolve(),
    subClient.isOpen ? subClient.quit() : Promise.resolve(),
  ]);
  process.exit(0);
}

process.on('SIGINT', shutdown);
process.on('SIGTERM', shutdown);

start();
