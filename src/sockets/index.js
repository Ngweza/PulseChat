const handlers = require('./chat.handlers');

function configureSockets(io) {
  if (typeof handlers !== 'function') {
    throw new TypeError(
      'chat.handlers.js deve exportar uma função.'
    );
  }

  handlers(io);
}

module.exports = { configureSockets };
