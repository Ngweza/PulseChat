
require('dotenv').config();

const { io } = require('socket.io-client');
const jwt = require('jsonwebtoken');

const roomId = 'ce18e855-c8c8-4c2e-8843-6899d31158d2';

const token = process.env.TEST_JWT;

if (!token) {
  console.error('Define a variável TEST_JWT antes de executar o teste.');
  process.exit(1);
}

const socket = io('http://localhost:3000', {
  auth: { token },
  reconnection: false,
  timeout: 5000,
});

const timer = setTimeout(() => {
  console.error('Tempo limite excedido durante o teste.');
  socket.disconnect();
  process.exit(1);
}, 10000);

socket.on('connect', () => {
  console.log('Ligação Socket.IO estabelecida.');

  socket.emit('join_room', { roomId }, (result) => {
    console.log('Resultado ao entrar na sala:', result);

    if (!result?.ok) {
      clearTimeout(timer);
      socket.disconnect();
      process.exitCode = 1;
      return;
    }

    socket.emit(
      'send_message',
      { roomId, content: 'Olá! Esta é uma mensagem de teste.' },
      (response) => {
        console.log('Resultado do envio:', response);

        clearTimeout(timer);
        socket.disconnect();

        if (!response?.ok) {
          process.exitCode = 1;
        } else {
          console.log('Teste de envio concluído com sucesso.');
        }
      }
    );
  });
});

socket.on('connect_error', (error) => {
  console.error('Erro de ligação:', error.message);
  clearTimeout(timer);
  process.exitCode = 1;
});
