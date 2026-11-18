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
- Francisco Zolana - 20240801

### Palavras-chave

Mensagens Instantâneas; Sistemas Distribuídos; Comunicação Assíncrona; Tolerância a Falhas; Segurança Informática; Inteligência Artificial; RabbitMQ; WebSocket; PostgreSQL; Redis; Docker.

---

## 2. Resumo

O PulseChat é um projeto de desenvolvimento de uma plataforma de comunicação digital, concebida para permitir a troca de mensagens em tempo real entre utilizadores, através de conversas individuais e grupos. O principal objetivo consiste em desenvolver uma aplicação funcional, escalável e resiliente, capaz de assegurar a continuidade da comunicação mesmo perante falhas num dos servidores.
A solução foi desenvolvida com uma arquitetura cliente-servidor, na qual a aplicação Android funciona como interface de utilização e comunica com uma infraestrutura de backend responsável pelo processamento dos pedidos, gestão dos utilizadores, armazenamento das mensagens e funcionamento das conversas.
No desenvolvimento da infraestrutura foram utilizados Node.js e Express.js para a implementação dos servidores, Socket.IO para permitir a comunicação em tempo real e PostgreSQL para o armazenamento persistente dos dados. Foi também integrado Redis, utilizado na partilha de eventos entre instâncias do servidor, permitindo melhorar a comunicação numa arquitetura com múltiplos backends. Para a distribuição dos pedidos foi utilizado o Nginx, configurado como proxy inverso e balanceador de carga entre dois servidores de aplicação, designados backend-a e backend-b.
Uma das características relevantes do projeto é a implementação de mecanismos de tolerância a falhas. A existência de duas instâncias de backend permite que, quando uma delas fica indisponível, a outra possa continuar a assegurar o serviço. Durante os testes realizados, foi possível verificar a recuperação da ligação da aplicação Android através do backend-b quando o backend-a foi desligado. Os grupos criados permaneceram igualmente guardados na base de dados, demonstrando a separação entre a disponibilidade dos servidores de aplicação e a persistência dos dados.
O projeto integra ainda o PulseBot, um agente inteligente desenvolvido em Python, Flask e PyTorch. Este componente permite reconhecer determinadas intenções dos utilizadores através de um classificador treinado, sendo ativado através da menção @PulseBot nas conversas.
Ao longo do desenvolvimento foram realizados testes de comunicação, persistência de dados, funcionamento dos servidores e recuperação perante falhas. O trabalho permitiu aplicar conhecimentos de desenvolvimento de aplicações, bases de dados, redes, comunicação em tempo real, virtualização e sistemas distribuídos.

---

## 3. Contexto

Atualmente, as plataformas de comunicação digital são essenciais para a interação entre pessoas e equipas. No entanto, uma aplicação dependente de um único servidor pode ficar indisponível quando ocorre uma falha nesse servidor.
O PulseChat procura responder a este problema através de uma arquitetura distribuída, com dois servidores de aplicação, mecanismos de comunicação em tempo real e armazenamento centralizado dos dados.

ii. Motivação
A motivação para o desenvolvimento deste projeto surgiu da necessidade de criar uma plataforma própria de comunicação, que permitisse explorar tecnologias modernas de desenvolvimento de software e compreender o funcionamento de sistemas distribuídos.
Pretendeu-se também desenvolver uma solução que não dependesse exclusivamente de um único servidor, permitindo estudar mecanismos de redundância, balanceamento de carga e recuperação de ligações.

iii. Objetivos
Os principais objetivos definidos foram:

Desenvolver uma aplicação Android para comunicação entre utilizadores.
Implementar conversas individuais e em grupo.
Permitir o envio e a receção de mensagens em tempo real.
Implementar autenticação e gestão de utilizadores.
Utilizar uma base de dados PostgreSQL para garantir a persistência da informação.
Integrar Redis para partilha de eventos entre os servidores.
Configurar dois servidores de aplicação para aumentar a disponibilidade.
Implementar o Nginx como proxy inverso e balanceador de carga.
Desenvolver e integrar o agente inteligente PulseBot.
Testar o comportamento da aplicação perante falhas de servidores.


---

## 4. Processo

i. Metodologia utilizada

O desenvolvimento foi realizado de forma incremental, dividindo o projeto em diferentes componentes e funcionalidades. Inicialmente, foi preparada a estrutura da aplicação e da infraestrutura. Posteriormente, foram desenvolvidos os mecanismos de comunicação, autenticação, armazenamento e integração entre os serviços.

Foram realizados testes progressivos para verificar o funcionamento individual dos componentes e o comportamento da arquitetura no seu conjunto. Também foram efetuados testes de falha, nomeadamente através da interrupção de uma das instâncias do backend, de forma a avaliar a capacidade de recuperação da aplicação.

ii. Ferramentas utilizadas

As principais ferramentas utilizadas foram:

Android Studio – desenvolvimento e testes da aplicação Android.
Visual Studio Code – edição e desenvolvimento do backend.
Docker e Docker Compose – criação, execução e gestão dos containers.
Git – gestão e controlo do código-fonte.
Terminal – execução de comandos, configuração e diagnóstico dos serviços.
Figma – elaboração do diagrama da arquitetura do sistema.

iii. Tecnologias utilizadas

O projeto utiliza as seguintes tecnologias:
Kotlin: desenvolvimento da aplicação Android.
Node.js: execução dos servidores de aplicação.
Express.js: criação e gestão das rotas e serviços HTTP.
Socket.IO: comunicação em tempo real entre clientes e servidores.
PostgreSQL: armazenamento de utilizadores, grupos, membros e mensagens.
Redis: partilha de eventos entre instâncias do backend.
Nginx: encaminhamento e distribuição dos pedidos entre servidores.
Docker: isolamento e execução dos diferentes serviços.
Python e Flask: implementação do serviço PulseBot.
PyTorch: criação e treino do classificador de intenções do PulseBot.
JWT: mecanismo de autenticação através de tokens.

iv. Estrutura da equipa

A equipa organizou o trabalho em diferentes áreas de desenvolvimento, de acordo com as responsabilidades atribuídas a cada elemento:
Desenvolvimento da aplicação Android e respetiva interface.
Implementação do backend e dos serviços de comunicação.
Configuração e gestão da base de dados.
Preparação da infraestrutura Docker, Redis e Nginx.
Desenvolvimento e integração do PulseBot.
Realização de testes e documentação técnica.

---

## 5. Resultados

i. Descrição da solução desenvolvida
Foi desenvolvida uma plataforma de comunicação composta por uma aplicação Android e uma infraestrutura de backend distribuída. A aplicação comunica com os servidores através do Nginx, que encaminha os pedidos para as instâncias disponíveis.

A infraestrutura inclui dois servidores Node.js, uma base de dados PostgreSQL principal, uma réplica PostgreSQL, um serviço Redis e o serviço independente PulseBot. Os componentes são executados em containers Docker, permitindo uma gestão organizada e isolada dos serviços.

ii. Funcionalidades principais

Entre as principais funcionalidades implementadas destacam-se:

Registo e autenticação de utilizadores.
Comunicação em tempo real através de Socket.IO.
Criação e consulta de grupos de conversa.
Gestão de membros dos grupos.
Envio e receção de mensagens.
Armazenamento persistente das conversas.
Comunicação entre múltiplas instâncias do backend através do Redis.
Distribuição dos pedidos através do Nginx.
Identificação da instância de backend à qual a aplicação está ligada.
Integração do PulseBot através da menção @PulseBot.

Foi também testado o mecanismo de failover. Durante esse teste, o backend-a foi desligado e a aplicação conseguiu estabelecer ligação através do backend-b. Os grupos existentes permaneceram disponíveis na base de dados e foram apresentados na aplicação.

iii. Contributos relevantes

Um dos principais contributos do projeto foi a implementação e validação de uma arquitetura com múltiplas instâncias de backend, permitindo estudar a disponibilidade e a tolerância a falhas.

A utilização do Redis permitiu explorar a partilha de eventos entre servidores, enquanto o PostgreSQL assegurou a persistência dos dados independentemente da instância de backend utilizada.

Outro contributo importante foi o desenvolvimento do PulseBot, que introduziu um componente de inteligência artificial próprio na plataforma, utilizando um classificador de intenções desenvolvido com PyTorch.

Os testes realizados permitiram ainda identificar limitações na rapidez de recuperação das ligações e oportunidades de melhoria na configuração do balanceamento e na reconexão automática do cliente Android.


---

## 6. Reflexão

i. Lições aprendidas
O desenvolvimento do PulseChat permitiu adquirir conhecimentos práticos sobre a construção de aplicações distribuídas e a integração de diferentes tecnologias num único sistema.

Foi possível compreender melhor a importância da comunicação em tempo real, da persistência de dados, da utilização de containers e da existência de mecanismos de redundância. Os testes de falha demonstraram que a disponibilidade de uma aplicação não depende apenas de existirem vários servidores, mas também da correta configuração da infraestrutura e do comportamento do cliente durante a reconexão.

ii. Limitações

Apesar dos resultados alcançados, existem aspetos que necessitam de melhorias. A recuperação da ligação entre a aplicação Android e o servidor alternativo ainda pode ser otimizada, nomeadamente através da redução dos tempos de reconexão e da recuperação automática da participação nos grupos.
A réplica PostgreSQL e os mecanismos de promoção automática perante uma falha do servidor principal também representam áreas que exigem validação e desenvolvimento adicional. O PulseBot, por sua vez, ainda possui funcionalidades que necessitam de maior aprofundamento, sobretudo no processamento de contexto e na execução efetiva de determinadas intenções.


iii. Trabalho futuro

Como trabalho futuro, pretende-se:

Melhorar a rapidez de deteção de falhas e reconexão automática.
Implementar a recuperação automática da participação nos grupos após uma reconexão.
Melhorar a gestão de sessões e a sincronização de mensagens.
Implementar e testar mecanismos de failover da base de dados PostgreSQL.
Reforçar a segurança da comunicação e dos dados.
Desenvolver novas funcionalidades para o PulseBot, incluindo maior capacidade de compreensão do contexto das conversas.
Melhorar a monitorização dos servidores e a identificação de erros.
Realizar testes de carga e de resistência para avaliar o desempenho da infraestrutura.

Em conclusão, o PulseChat representa uma solução que permitiu aplicar conhecimentos de várias áreas da engenharia informática, desde o desenvolvimento mobile até à administração de sistemas e à integração de inteligência artificial. O projeto constitui uma base funcional para a evolução de uma plataforma de comunicação distribuída, com potencial para receber novas funcionalidades e melhorias de desempenho e disponibilidade.

