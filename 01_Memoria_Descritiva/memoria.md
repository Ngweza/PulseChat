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

O PulseChat é uma plataforma distribuída de mensagens instantâneas concebida para mitigar os problemas de fragmentação da comunicação e a vulnerabilidade de infraestruturas centralizadas a pontos únicos de falha (Single Points of Failure). Desenvolvido no âmbito multidisciplinar da Licenciatura em Engenharia Informática, o sistema responde diretamente aos desafios do Trabalho 8 do briefing curricular, focando-se em demonstrar na prática mecanismos de comunicação assíncrona, persistência resiliente, ordenação causal, tolerância a faltas e mitigação ativa de falhas de componentes.   

A aplicação cliente é desenvolvida de forma nativa para Android em Kotlin através do Android Studio, assegurando uma interface moderna, reativa e eficiente na gestão de sessões de comunicação. A infraestrutura de suporte encontra-se distribuída entre nós de execução redundantes (Servidor 1 e Servidor 2), onde instâncias de Nginx operam como proxies reversos e balanceadores de carga com sondagens ativas de integridade (health checks). O tráfego de mensagens em tempo real é processado por nós de backend em Node.js suportados por WebSockets.   

Para prevenir a perda de informação em caso de sobrecarga ou indisponibilidade temporária de instâncias, o sistema recorre ao message broker RabbitMQ, que desacopla a ingestão dos eventos através de filas duráveis e persistentes. O armazenamento persistente assenta num cluster de base de dados relacional PostgreSQL configurado em topologia Primário-Réplica, operando em articulação com instâncias distribuídas de Redis para cache volátil e sincronização de estado e sessões entre servidores em memória.   

A integridade do histórico de conversação é preservada através de números de sequência por conversa e chaves de idempotência operacional, prevenindo a duplicação de mensagens durante reconexões ou procedimentos de failover. Na vertente da Segurança Informática, impõe-se a encriptação integral das comunicações através de HTTPS e WSS, complementada pela validação de integridade dos conteúdos via hashes criptográficos SHA-256. Paralelamente, a componente de Inteligência Artificial implementa um microsserviço em Python com Scikit-Learn dedicado à classificação em tempo real de mensagens maliciosas e deteção estatística de comportamentos anómalos ou fluxos massivos de spam. A tolerância a faltas da solução é validada de forma empírica através de ensaios laboratoriais controlados com contentores Docker, comprovando que a quebra abrupta de serviços, bases de dados ou nós de rede não interrompe a disponibilidade global nem corrompe os dados dos utilizadores.

---

## 3. Contexto

A dispersão do ecossistema digital dos estudantes e equipas de trabalho entre diversas plataformas gera uma experiência fragmentada e ineficiente. Adicionalmente, sistemas de comunicação convencionais baseados em arquiteturas centralizadas sofrem frequentemente de indisponibilidade total e descarte de dados quando o seu único servidor aplicacional ou nó de dados colapsa.

Como motivação, Explorar na prática os conceitos nucleares de sistemas distribuídos e engenharia de software para construir uma plataforma resiliente, sustentada no princípio orientador de que um serviço de mensagens deve saber responder e recuperar com transparência quando componentes de rede ou hardware falham.

Como objetivo, conceber e implementar um sistema distribuído de mensagens instantâneas tolerante a falhas, combinando desacoplamento por filas (RabbitMQ), persistência com replicação (PostgreSQL Primário/Réplica), cache em Redis, ordenação sequencial estrita, integridade por hash, segurança de transporte e defesa ativa contra spam com recurso a Machine Learning.

---

## 4. Processo

Nesta primeira fase, foram definidos os objetivos, os requisitos, as funcionalidades e a arquitetura inicial do PulseChat. A solução prevê uma aplicação Android desenvolvida em Kotlin, serviços backend (XAMPP), comunicação em tempo real através de WebSockets e processamento assíncrono com RabbitMQ.

O PostgreSQL será utilizado para a persistência dos dados e o Redis para apoio à gestão de informação temporária. A solução prevê ainda o uso de Python e Scikit-learn na componente de Inteligência Artificial, bem como Docker e GitHub para apoiar a execução dos serviços e o controlo de versões.

Equipa discente composta por Márcio Quintas (20241499), Gianni Lopes (20240593) e Ngweza Van-Dúnem (20240075), com distribuição colaborativa e partilhada de tarefas pelas áreas de Engenharia de Software, Sistemas Distribuídos, Segurança Informática e Inteligência Artificial.

---

## 5. Resultados

Nesta primeira entrega, foram estabelecidos o enquadramento do PulseChat, os objetivos, os requisitos principais e uma proposta de arquitetura distribuída. Foram também identificados mecanismos de segurança, persistência, replicação, sincronização e tolerância a falhas.

A implementação e os testes das diferentes componentes serão desenvolvidos progressivamente nas fases seguintes.

---

## 6. Reflexão

A fase inicial permitiu estruturar o PulseChat e identificar os principais desafios técnicos do projeto, sobretudo a entrega fiável de mensagens, a consistência dos dados e a recuperação perante falhas.

Nas próximas fases, pretende-se implementar os componentes definidos, testar a comunicação entre serviços e avaliar o comportamento do sistema em situações de falha, reforçando a segurança e a fiabilidade da solução.
