const { createClient } = require('redis');

const redisUrl = process.env.REDIS_URL || 'redis://127.0.0.1:6379';

const pubClient = createClient({ url: redisUrl });
const subClient = pubClient.duplicate();

pubClient.on('error', (err) => {
  console.error('Erro no Redis (publicação):', err.message);
});

subClient.on('error', (err) => {
  console.error('Erro no Redis (subscrição):', err.message);
});

async function connectRedis() {
  if (!pubClient.isOpen) {
    await pubClient.connect();
  }

  if (!subClient.isOpen) {
    await subClient.connect();
  }
}

module.exports = { pubClient, subClient, connectRedis };
