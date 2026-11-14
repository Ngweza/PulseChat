const messages = require('../repositories/message.repositories');

async function sendMessage(roomId, userId, content) {
  if (typeof content !== 'string') {
    throw new Error('INVALID_CONTENT');
  }

  const normalizedContent = content.trim();

  if (!normalizedContent || normalizedContent.length > 10000) {
    throw new Error('INVALID_CONTENT');
  }

  const isMember = await messages.isRoomMember(roomId, userId);

  if (!isMember) {
    throw new Error('ROOM_ACCESS_DENIED');
  }

  // Persistir antes de distribuir a mensagem.
  return messages.createMessage(roomId, userId, normalizedContent);
}

async function getRoomMessages(roomId, userId, limit = 50, before = null) {
  const isMember = await messages.isRoomMember(roomId, userId);

  if (!isMember) {
    throw new Error('ROOM_ACCESS_DENIED');
  }

  return messages.listMessages(roomId, limit, before);
}

module.exports = {
  sendMessage,
  getRoomMessages,
};
