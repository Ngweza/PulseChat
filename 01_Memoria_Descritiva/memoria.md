# Memória Descritiva — PulseChat

## 1. Identificação

**Nome do projeto:** PulseChat — Serviço de Mensagens Distribuído e Tolerante a Falhas  
**Curso:** Engenharia Informática  
**Ano letivo:** 2026/2027  
**Semestre:** 5.º Semestre  
**Unidades Curriculares:** Projeto de Desenvolvimento de Software; Engenharia de Software; Segurança Informática; Sistemas Distribuídos; Inteligência Artificial  
**Docentes:** Miguel Boavida; Rui Ramos; Sérgio Nunes; Pedro Rosa; Samuel Gomes

### Elementos do grupo

- Márcio Alexandre Cardoso Quintas — 20241499
- Gianni Celso dos Santos Lopes — 20240593
- Ngweza Josemar Falcão Pereira Van-Dúnem — 20240075

### Palavras-chave

Mensagens Instantâneas; Sistemas Distribuídos; Comunicação Assíncrona; Tolerância a Falhas; Segurança Informática; Inteligência Artificial; RabbitMQ; WebSocket; PostgreSQL; Redis; Docker.

---

## 2. Resumo

O PulseChat é um serviço de mensagens instantâneas que permite a comunicação privada e em grupo através de uma arquitetura distribuída. O sistema pretende assegurar o envio, a receção e o armazenamento das mensagens, incluindo a sincronização quando os utilizadores ficam offline.

A solução recorre a múltiplos serviços, comunicação assíncrona, replicação de dados e mecanismos de segurança, procurando melhorar a disponibilidade e a recuperação perante falhas. Inclui ainda uma componente de Inteligência Artificial para apoiar a deteção de spam e comportamentos suspeitos.

---

## 3. Contexto

A comunicação digital exige sistemas capazes de processar mensagens em tempo real, armazenar o histórico e continuar a funcionar perante falhas. As arquiteturas centralizadas podem apresentar limitações de disponibilidade e escalabilidade.

Neste contexto, surge o PulseChat, que procura aplicar conceitos de Sistemas Distribuídos, Segurança Informática e Inteligência Artificial numa plataforma de mensagens fiável, com mecanismos de persistência, sincronização e proteção dos dados.

---

## 4. Processo

Nesta primeira fase, foram definidos os objetivos, os requisitos, as funcionalidades e a arquitetura inicial do PulseChat. A solução prevê uma aplicação Android desenvolvida em Kotlin, serviços backend em Node.js, comunicação em tempo real através de WebSockets e processamento assíncrono com RabbitMQ.

O PostgreSQL será utilizado para a persistência dos dados e o Redis para apoio à gestão de informação temporária. A solução prevê ainda o uso de Python e Scikit-learn na componente de Inteligência Artificial, bem como Docker e GitHub para apoiar a execução dos serviços e o controlo de versões.

---

## 5. Resultados

Nesta primeira entrega, foram estabelecidos o enquadramento do PulseChat, os objetivos, os requisitos principais e uma proposta de arquitetura distribuída. Foram também identificados mecanismos de segurança, persistência, replicação, sincronização e tolerância a falhas.

A implementação e os testes das diferentes componentes serão desenvolvidos progressivamente nas fases seguintes.

---

## 6. Reflexão

A fase inicial permitiu estruturar o PulseChat e identificar os principais desafios técnicos do projeto, sobretudo a entrega fiável de mensagens, a consistência dos dados e a recuperação perante falhas.

Nas próximas fases, pretende-se implementar os componentes definidos, testar a comunicação entre serviços e avaliar o comportamento do sistema em situações de falha, reforçando a segurança e a fiabilidade da solução.
