
const jwt = require('jsonwebtoken');
const chatService = require('../Services/chat.services');
const messages = require('../repositories/message.repositories');

function registerChatHandlers(io) {
  // Autentica cada ligação Socket.IO com o token JWT.
  io.use((socket, next) => {
    const token = socket.handshake.auth?.token;

    if (!token) {
      return next(new Error('Token de autenticação em falta.'));
    }

    try {
      socket.user = jwt.verify(token, process.env.JWT_SECRET);
      next();
    } catch (error) {
      next(new Error('Token inválido ou expirado.'));
    }
  });

  io.on('connection', (socket) => {
    const instance =
    process.env.INSTANCE_ID ||
    process.env.HOSTNAME ||
    'local';
    
    console.log(
    `Utilizador ligado por Socket.IO: ${socket.user.sub} | Servidor: ${instance}`
  );
   socket.emit('server_info', {
    instance: instance
  });

    // Entrar numa sala, apenas se o utilizador for membro.
    socket.on('join_room', async (payload, callback) => {
      try {
        const roomId = payload?.roomId;

        if (typeof roomId !== 'string') {
          throw new Error('INVALID_ROOM');
        }

        const isMember = await messages.isRoomMember(
          roomId,
          socket.user.sub
        );

        if (!isMember) {
          throw new Error('ROOM_ACCESS_DENIED');
        }

        await socket.join(roomId);

        const result = { ok: true, roomId };

        if (typeof callback === 'function') callback(result);

        socket.emit('room_joined', result);
      } catch (error) {
        const result = {
          ok: false,
          error:
            error.message === 'ROOM_ACCESS_DENIED'
              ? 'Não tens acesso a esta sala.'
              : 'Não foi possível entrar na sala.',
        };

        if (typeof callback === 'function') callback(result);
        else socket.emit('chat_error', result);
      }
    });

    // Guardar a mensagem na base de dados e distribuí-la pela sala.
    socket.on('send_message', async (payload, callback) => {
      try {
        const roomId = payload?.roomId;
        const content = payload?.content;

        if (typeof roomId !== 'string' || !socket.rooms.has(roomId)) {
          throw new Error('ROOM_ACCESS_DENIED');
        }

        const message = await chatService.sendMessage(
          roomId,
          socket.user.sub,
          content
        );

        io.to(roomId).emit('new_message', message);

        if (typeof callback === 'function') {
          callback({ ok: true, message });
        }
      } catch (error) {
        let message = 'Não foi possível enviar a mensagem.';

        if (error.message === 'INVALID_CONTENT') {
          message = 'A mensagem não pode estar vazia nem exceder 10000 caracteres.';
        } else if (error.message === 'ROOM_ACCESS_DENIED') {
          message = 'Tens de entrar numa sala a que tens acesso.';
        }

        const result = { ok: false, error: message };

        if (typeof callback === 'function') callback(result);
        else socket.emit('chat_error', result);
      }
    });

    socket.on('disconnect', (reason) => {
      console.log(`Ligação terminada (${socket.user.sub}): ${reason}`);
    });
  });
}

module.exports = registerChatHandlers;
