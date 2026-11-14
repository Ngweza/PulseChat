const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const messagesRoutes = require('./routes/messages.routes');

const app = express();
app.set('trust proxy', 1);

app.use(helmet());
app.use(cors());
app.use(express.json());
app.use('/api/messages', messagesRoutes);

app.get('/health', async (req, res) => {
  try {
    const { checkDatabase } = require('./config/database');
    const { pubClient } = require('./config/redis');

    const database = await checkDatabase();

    if (!pubClient.isReady) {
      throw new Error('Redis não está pronto');
    }

    res.json({
      status: 'ok',
      database: 'connected',
      instance: process.env.INSTANCE_ID || process.env.HOSTNAME || 'local',
      redis: 'connected',
      time: database.time,
    });
  } catch (error) {
    res.status(503).json({
      status: 'error',
      message: 'Um ou mais serviços não estão disponíveis',
    });
  }
});
const authRoutes = require('./routes/auth.routes');
const roomsRoutes = require('./routes/rooms.routes');

app.use('/api/auth', authRoutes);
app.use('/api/rooms', roomsRoutes);

module.exports = app;
