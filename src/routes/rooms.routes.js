const express = require('express');
const authenticateToken = require('../middleware/auth');
const rooms = require('../repositories/room.repositories');

const router = express.Router();

router.use(authenticateToken);

router.post('/', async (req, res) => {
  const { name } = req.body || {};

  if (
    typeof name !== 'string' ||
    name.trim().length < 1 ||
    name.trim().length > 100
  ) {
    return res.status(400).json({
      error: 'O nome da sala deve ter entre 1 e 100 caracteres.',
    });
  }

  try {
    const userId = req.user.sub;

    const room = await rooms.createRoom(name.trim(), userId);

    return res.status(201).json({
      message: 'Sala criada com sucesso.',
      room,
    });
  } catch (error) {
    console.error('Erro ao criar sala:', error.message);

    return res.status(500).json({
      error: 'Não foi possível criar a sala.',
    });
  }
});

router.get('/', async (req, res) => {
  try {
    const userId = req.user.sub;
    const userRooms = await rooms.listRoomsForUser(userId);

    return res.json({ rooms: userRooms });
  } catch (error) {
    console.error('Erro ao listar salas:', error.message);

    return res.status(500).json({
      error: 'Não foi possível listar as salas.',
    });
  }
});

module.exports = router;
