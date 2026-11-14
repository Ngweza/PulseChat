
const express = require('express');
const authenticateToken = require('../middleware/auth');
const chatService = require('../Services/chat.services');

const router = express.Router();

router.use(authenticateToken);

router.get('/:roomId', async (req, res) => {
  try {
    const { roomId } = req.params;
    const limit = Number(req.query.limit ?? 50);
    const before = req.query.before ?? null;

    if (!Number.isInteger(limit) || limit < 1 || limit > 100) {
      return res.status(400).json({
        error: 'O limite deve ser um número entre 1 e 100.',
      });
    }

    if (before !== null && Number.isNaN(Date.parse(before))) {
      return res.status(400).json({
        error: 'A data do parâmetro before é inválida.',
      });
    }

    const messages = await chatService.getRoomMessages(
      roomId,
      req.user.sub,
      limit,
      before
    );

    return res.json({ messages });
  } catch (error) {
    if (error.message === 'ROOM_ACCESS_DENIED') {
      return res.status(403).json({
        error: 'Não tens acesso a esta sala.',
      });
    }

    console.error('Erro ao consultar mensagens:', error.message);

    return res.status(500).json({
      error: 'Não foi possível consultar as mensagens.',
    });
  }
});

module.exports = router;
