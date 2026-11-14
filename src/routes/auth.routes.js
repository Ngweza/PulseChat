const express = require('express');
const rateLimit = require('express-rate-limit');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');

const users = require('../repositories/user.repositories');

const router = express.Router();

const authLimiter = rateLimit({
  windowMs: 15 * 60 * 1000,
  limit: 10,
  standardHeaders: true,
  legacyHeaders: false,
  message: {
    error: 'Demasiadas tentativas. Tenta novamente mais tarde.',
  },
});

function validateCredentials(req, res, next) {
  const { username, password } = req.body || {};

  if (
    typeof username !== 'string' ||
    typeof password !== 'string' ||
    username.trim().length < 3 ||
    username.trim().length > 50 ||
    password.length < 8 ||
    Buffer.byteLength(password, 'utf8') > 72
  ) {
    return res.status(400).json({
      error: 'Nome de utilizador inválido ou palavra-passe fora dos limites permitidos.',
    });
  }

  req.credentials = {
    username: username.trim(),
    password,
  };

  next();
}

function createToken(user) {
  const secret = process.env.JWT_SECRET;

  if (!secret || secret.length < 32) {
    throw new Error('JWT_SECRET tem de ter pelo menos 32 caracteres.');
  }

  return jwt.sign(
    { username: user.username },
    secret,
    {
      subject: user.id,
      expiresIn: '1h',
    }
  );
}

router.post('/register', authLimiter, validateCredentials, async (req, res) => {
  try {
    const { username, password } = req.credentials;

    const existingUser = await users.findByUsername(username);

    if (existingUser) {
      return res.status(409).json({
        error: 'Esse nome de utilizador já está registado.',
      });
    }

    const passwordHash = await bcrypt.hash(password, 12);
    const user = await users.createUser(username, passwordHash);
    const token = createToken(user);

    return res.status(201).json({
      message: 'Utilizador registado com sucesso.',
      user: {
        id: user.id,
        username: user.username,
        createdAt: user.created_at,
      },
      token,
    });
  } catch (error) {
    if (error.code === '23505') {
      return res.status(409).json({
        error: 'Esse nome de utilizador já está registado.',
      });
    }

    console.error('Erro no registo:', error.message);

    return res.status(500).json({
      error: 'Não foi possível registar o utilizador.',
    });
  }
});

router.post('/login', authLimiter, validateCredentials, async (req, res) => {
  try {
    const { username, password } = req.credentials;
    const user = await users.findByUsername(username);

    if (!user) {
      return res.status(401).json({
        error: 'Nome de utilizador ou palavra-passe incorretos.',
      });
    }

    const passwordMatches = await bcrypt.compare(
      password,
      user.password_hash
    );

    if (!passwordMatches) {
      return res.status(401).json({
        error: 'Nome de utilizador ou palavra-passe incorretos.',
      });
    }

    const token = createToken(user);

    return res.json({
      message: 'Login efetuado com sucesso.',
      user: {
        id: user.id,
        username: user.username,
      },
      token,
    });
  } catch (error) {
    console.error('Erro no login:', error.message);

    return res.status(500).json({
      error: 'Não foi possível efetuar o login.',
    });
  }
});

module.exports = router;
