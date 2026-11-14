const readline = require('readline');
const { io } = require('socket.io-client');
const https = require('https');
const http = require('http');

const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout
});

rl.question('Palavra-passe: ', async (password) => {
  try {
    const response = await fetch('http://localhost:8080/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        username: 'redis_teste_2026_2',
        password
      })
    });

    const data = await response.json();

    if (!data.token) {
      console.log('Login falhou:', data.error || data.message);
      rl.close();
      return;
    }

    require('fs').writeFileSync('.test-token', data.token, { mode: 0o600 });
    console.log('Login efetuado. Token guardado localmente.');
  } catch (error) {
    console.log('Erro:', error.message);
  }

  rl.close();
});