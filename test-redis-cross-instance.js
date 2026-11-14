require('dotenv').config();

const { io } = require('socket.io-client');

const roomId = 'ce18e855-c8c8-4c2e-8843-6899d31158d2';
const token = process.env.TEST_JWT;

if (!token) {
  console.error('Define TEST_JWT.');
  process.exit(1);
}

const receiver = io('http://localhost:3002', {
  auth: { token },
  transports: ['websocket'],
  reconnection: false,
  timeout: 5000,
});

let sender;
let finished = false;

const timer = setTimeout(() => {
  finish(new Error('Tempo limite: a mensagem não chegou ao receptor.'));
}, 10000);

function finish(error) {
  if (finished) return;
  finished = true;

  clearTimeout(timer);
  receiver.disconnect();

  if (sender) sender.disconnect();

  if (error) {
    console.error('TESTE FALHOU:', error.message);
    process.exitCode = 1;
  } else {
    console.log('SUCESSO: backend-b recebeu a mensagem enviada pelo backend-a através do Redis.');
  }
}

receiver.on('connect_error', (err) => {
  finish(new Error(`Erro no receptor: ${err.message}`));
});

receiver.on('connect', () => {
  console.log('Receptor ligado ao backend-b.');

  receiver.on('new_message', (message) => {
    if (message?.content === 'Teste de comunicação entre instâncias Redis') {
      console.log('Mensagem recebida:', message.content);
      finish();
    }
  });

  receiver.emit('join_room', { roomId }, (result) => {
    if (!result?.ok) {
      finish(new Error(`Entrada na sala falhou: ${JSON.stringify(result)}`));
      return;
    }

    console.log('Receptor entrou na sala.');

    // Só criamos o emissor depois de o receptor estar pronto.
    sender = io('http://localhost:3001', {
      auth: { token },
      transports: ['websocket'],
      reconnection: false,
      timeout: 5000,
    });

    sender.on('connect_error', (err) => {
      finish(new Error(`Erro no emissor: ${err.message}`));
    });

    sender.on('connect', () => {
      console.log('Emissor ligado ao backend-a.');

      sender.emit('join_room', { roomId }, (joinResult) => {
        if (!joinResult?.ok) {
          finish(new Error(`Entrada do emissor falhou: ${JSON.stringify(joinResult)}`));
          return;
        }

        console.log('Emissor entrou na sala.');

        sender.emit(
          'send_message',
          {
            roomId,
            content: 'Teste de comunicação entre instâncias Redis',
          },
          (response) => {
            if (!response?.ok) {
              finish(new Error(`Envio falhou: ${JSON.stringify(response)}`));
              return;
            }

            console.log('Backend-a confirmou o envio.');
          }
        );
      });
    });
  });
});