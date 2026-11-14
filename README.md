# PulseChat

Backend para uma aplicação de mensagens em tempo real. A API REST trata de
autenticação, salas e histórico; o Socket.IO gere a comunicação em tempo real
entre clientes. PostgreSQL armazena utilizadores, salas e mensagens, enquanto
Redis sincroniza eventos Socket.IO entre instâncias do backend.

## Funcionalidades

- Registo e autenticação com palavra-passe protegida por bcrypt e tokens JWT.
- Salas privadas: o criador é associado como administrador.
- Histórico de mensagens paginado e acessível apenas a membros da sala.
- Comunicação em tempo real por Socket.IO, com validação de acesso às salas.
- Adaptador Redis para distribuir eventos entre instâncias.
- Endpoint de saúde para verificar PostgreSQL e Redis.
- Configuração Docker Compose com PostgreSQL, Redis, duas instâncias do backend
  e Nginx.

> A API atual não inclui um endpoint para convidar ou adicionar outros membros
> a uma sala. Apenas o criador é associado automaticamente.

## Tecnologias

- Node.js 22+
- Express 5 e Socket.IO
- PostgreSQL 17
- Redis 7
- Docker e Docker Compose (opcional)

## Configuração local

1. Clone o repositório e instale as dependências:

   ```bash
   git clone https://github.com/Ngweza/PulseChat.git
   cd PulseChat
   npm ci
   ```

2. Crie o ficheiro de ambiente e defina os valores locais:

   ```bash
   cp .env.example .env
   ```

   Defina `JWT_SECRET` com pelo menos 32 caracteres e escolha palavras-passe
   fortes para `POSTGRES_PASSWORD` e `REPLICATION_PASSWORD`. Não publique o
   ficheiro `.env`.

3. Inicie PostgreSQL e Redis com Docker Compose:

   ```bash
   docker compose up -d postgres-primary redis
   ```

4. Crie as tabelas da aplicação:

   ```bash
   docker compose exec -T postgres-primary \
     psql -U chatuser -d chatdb < database/schema.sql
   ```

5. Inicie o backend:

   ```bash
   npm run dev
   ```

   A API fica disponível em `http://localhost:3000`. Para iniciar sem o
   `nodemon`, use `npm start`.

## Variáveis de ambiente

| Variável | Obrigatória | Descrição |
| --- | --- | --- |
| `JWT_SECRET` | Sim | Segredo de assinatura dos tokens; deve ter pelo menos 32 caracteres. |
| `POSTGRES_PASSWORD` | Sim | Palavra-passe do utilizador PostgreSQL usado pela aplicação. |
| `REPLICATION_PASSWORD` | Para Compose | Palavra-passe esperada pelo serviço de réplica. |
| `PORT` | Não | Porta do backend; por omissão, `3000`. |
| `PGHOST` | Não | Host PostgreSQL; por omissão, `127.0.0.1`. |
| `PGPORT` | Não | Porta PostgreSQL; por omissão, `5434`. |
| `PGDATABASE` | Não | Base de dados; por omissão, `chatdb`. |
| `PGUSER` | Não | Utilizador PostgreSQL; por omissão, `chatuser`. |
| `REDIS_URL` | Não | URL Redis; por omissão, `redis://127.0.0.1:6379`. |
| `CORS_ORIGIN` | Não | Origem permitida pelo servidor Socket.IO; por omissão, `*`. |
| `INSTANCE_ID` | Não | Identificador da instância, incluído no evento `server_info`. |
| `TEST_JWT` | Para testes manuais | Token JWT usado pelos scripts de teste Socket.IO. |

O ficheiro [`.env.example`](.env.example) contém valores de exemplo, sem
credenciais reais.

## API REST

As rotas abaixo, exceto `GET /health`, requerem o cabeçalho:

```http
Authorization: Bearer <token>
```

### Saúde

| Método | Rota | Descrição |
| --- | --- | --- |
| `GET` | `/health` | Verifica a ligação ao PostgreSQL e ao Redis; devolve `503` se algum serviço não estiver disponível. |

### Autenticação

| Método | Rota | Descrição |
| --- | --- | --- |
| `POST` | `/api/auth/register` | Cria uma conta e devolve um token JWT. |
| `POST` | `/api/auth/login` | Autentica uma conta e devolve um token JWT. |

Exemplo de registo ou login:

```bash
curl -X POST http://localhost:3000/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"username":"ana","password":"uma-palavra-passe-segura"}'
```

O nome de utilizador deve ter entre 3 e 50 caracteres. A palavra-passe deve ter
pelo menos 8 caracteres e não pode exceder 72 bytes. As rotas de autenticação
estão limitadas a 10 pedidos por IP em cada janela de 15 minutos.

### Salas

| Método | Rota | Descrição |
| --- | --- | --- |
| `POST` | `/api/rooms` | Cria uma sala e associa o criador como administrador. |
| `GET` | `/api/rooms` | Lista as salas de que o utilizador autenticado é membro. |

Exemplo de criação:

```bash
curl -X POST http://localhost:3000/api/rooms \
  -H 'Authorization: Bearer <token>' \
  -H 'Content-Type: application/json' \
  -d '{"name":"Equipa"}'
```

O nome da sala deve ter entre 1 e 100 caracteres.

### Mensagens

| Método | Rota | Descrição |
| --- | --- | --- |
| `GET` | `/api/messages/:roomId` | Obtém mensagens da sala, em ordem cronológica. |

Parâmetros de consulta:

- `limit`: quantidade de mensagens, entre 1 e 100; por omissão, `50`.
- `before`: data/hora ISO 8601; devolve mensagens anteriores a esse instante.

Exemplo:

```bash
curl 'http://localhost:3000/api/messages/<roomId>?limit=50' \
  -H 'Authorization: Bearer <token>'
```

## Socket.IO

Ligue-se ao mesmo host da API e envie o JWT na autenticação do handshake:

```js
const socket = io('http://localhost:3000', {
  auth: { token: '<token>' },
});
```

Eventos disponíveis:

| Evento | Direção | Payload / comportamento |
| --- | --- | --- |
| `server_info` | Servidor → cliente | Identificador da instância (`instance`). |
| `join_room` | Cliente → servidor | `{ roomId }`; exige que o utilizador seja membro. |
| `room_joined` | Servidor → cliente | Confirma a entrada na sala. |
| `send_message` | Cliente → servidor | `{ roomId, content }`; exige entrada na sala e guarda a mensagem antes de a distribuir. |
| `new_message` | Servidor → sala | Mensagem persistida, enviada a todos os membros ligados à sala. |
| `chat_error` | Servidor → cliente | Erro quando não foi fornecida uma função de confirmação. |

`join_room` e `send_message` aceitam uma função de confirmação. As respostas
incluem `ok: true` em caso de sucesso ou `ok: false` quando a operação falha.

## Docker Compose

O Compose configura PostgreSQL primário, PostgreSQL réplica, Redis, duas
instâncias do backend e Nginx. Para iniciar os serviços principais sem a
réplica:

```bash
docker compose up --build -d \
  postgres-primary redis backend-a backend-b nginx
```

Depois de o PostgreSQL estar pronto, aplique o esquema uma vez:

```bash
docker compose exec -T postgres-primary \
  psql -U chatuser -d chatdb < database/schema.sql
```

O Nginx fica disponível em `http://localhost:8080`. As instâncias do backend
também são expostas localmente nas portas `3001` e `3002`.

**Nota sobre a réplica:** o serviço `postgres-replica` usa `pg_basebackup` com o
utilizador `replicator`, mas o Compose atual não cria esse utilizador nem
configura a autenticação de replicação no PostgreSQL primário. Para iniciar a
réplica, configure primeiro o utilizador e as regras de acesso no primário.

## Esquema da base de dados

O esquema está em [`database/schema.sql`](database/schema.sql) e cria as tabelas
`users`, `rooms`, `room_members` e `messages`, bem como os índices usados nas
consultas.

## Aplicação Android

O frontend Android está em [`android/`](android/) e usa Kotlin e Jetpack
Compose. Para compilar a variante de depuração:

```bash
cd android
./gradlew assembleDebug
```

O projeto requer Android SDK 36. No emulador Android, a aplicação liga-se ao
backend em `http://10.0.2.2:8080`; para outro ambiente, ajuste os endereços de
API e WebSocket em `android/app/src/main/java/pt/iade/ei/pulsechat/data/network/`.
