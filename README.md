# javabeans-ia

MVP inicial do simulador JavaBeans com IA (futuro Azure OpenAI).

## Endpoints atuais
- GET /game/health       → "Game is running!" ou similar
- GET /game/cenario      → "Primeiro cenário de teste"

## Pré-requisitos
- Java 21 (`java -version`)
- Maven 3.9+ (`mvn -v`)
- PostgreSQL 16, apenas para o perfil `postgres`

## Perfis de banco de dados

| Perfil | Banco | Arquivo | Uso |
|---|---|---|---|
| `dev` (padrão) | H2 em memória | `application-dev.yml` | desenvolvimento rápido; dados somem ao reiniciar |
| `postgres` | PostgreSQL | `application-postgres.yml` | dados persistentes |

O perfil é escolhido pela variável `SPRING_PROFILES_ACTIVE`. Sem ela, o perfil é `dev`.

## Variáveis de ambiente

| Variável | Obrigatória | Padrão | Descrição |
|---|---|---|---|
| `SPRING_PROFILES_ACTIVE` | não | `dev` | `dev` ou `postgres` |
| `DB_USER` | não | `javabeans` | usuário do PostgreSQL |
| `DB_PASSWORD` | sim, no perfil `postgres` | — | senha do PostgreSQL |
| `DEEPSEEK_API_KEY` | sim, para a IA responder | vazio | chave da API DeepSeek |
| `DEEPSEEK_ENDPOINT` | não | `https://api.deepseek.com` | URL da API DeepSeek |
| `DEEPSEEK_TIMEOUT_SEGUNDOS` | não | `15` | tempo máximo de espera pela IA |
| `DB_POOL_MAX` | não | `10` | máximo de conexões simultâneas com o banco |

Nenhuma senha ou chave fica em arquivo do repositório. Para configurações pessoais, use `application-local.yml` (ignorado pelo Git).

## Como rodar

### Perfil `dev` (H2)
```powershell
mvn spring-boot:run
```

### Perfil `postgres`
1. Crie o usuário e o banco (uma vez só), conectado como `postgres` no `psql`:
   ```sql
   CREATE USER javabeans WITH PASSWORD 'escolha-uma-senha';
   CREATE DATABASE javabeans OWNER javabeans;
   ```
   O `OWNER` é necessário: desde o PostgreSQL 15, só o dono do banco pode criar tabelas no schema `public`.
2. Rode a aplicação:
   ```powershell
   $env:DB_PASSWORD = "escolha-uma-senha"
   $env:SPRING_PROFILES_ACTIVE = "postgres"
   mvn spring-boot:run
   ```
3. O Hibernate cria a tabela `sessao` sozinho (`ddl-auto: update`). Para conferir:
   ```powershell
   psql -U javabeans -d javabeans -c "\d sessao"
   ```

### Sinais de que deu certo
No log aparecem `The following 1 profile is active: "postgres"`, `HikariPool-1 - Start completed.` e `Started JavabeansIa`.

## Tabelas do banco

O Hibernate cria as tabelas a partir das entidades (`ddl-auto: update`). As receitas são cadastradas automaticamente na primeira vez que a aplicação sobe com o banco vazio (`config/ReceitasIniciais`).

| Tabela | Entidade | Conteúdo |
|---|---|---|
| `sessao` | `Sessao` | Uma partida: nome do barista, XP total e cliente atual |
| `receita` | `ReceitaEntity` | Bebidas do jogo, pontos e ordem dos clientes |
| `receita_ingrediente` | (coleção em `ReceitaEntity`) | Ingredientes esperados de cada receita |
| `rodada` | `Rodada` | Cada preparo de café: acerto, pontos, fala e feedback da IA |
| `rodada_ingrediente` | (coleção em `Rodada`) | Ingredientes escolhidos pelo aluno na rodada |
| `avaliacao_atendimento` | `AvaliacaoAtendimento` | Cada avaliação de atendimento feita pela IA (`/responder`) |

A `HttpSession` guarda apenas o identificador da sessão (`sessionIdBanco`); o estado do jogo fica no banco e sobrevive a reinícios do servidor.

Gravações na mesma partida travam a linha da `sessao` (`SELECT ... FOR UPDATE`), para que requisições simultâneas não percam XP. Se a mesma rodada for enviada duas vezes ao mesmo tempo (duplo clique), a segunda recebe `409 Conflict`.

## Desempenho e monitoramento

- **IA:** cada chamada tem timeout (`DEEPSEEK_TIMEOUT_SEGUNDOS`) e limite de tamanho da resposta (`max_tokens`). Se a IA falhar ou demorar demais, o jogo continua: o `/preparar-cafe` grava a rodada com um comentário neutro e o `/responder` grava uma avaliação com XP 0.
- **Banco:** `open-in-view` desligado, para que nenhuma requisição segure uma conexão do banco enquanto espera a IA.
- **Logs:** gravados em `logs/javabeans-ia.log` (rotação a cada 10 MB, 7 arquivos). Cada requisição é registrada com método, rota, status e duração, e recebe um ID curto que aparece em todas as linhas de log dela. Requisições acima de 5 s geram `WARN`; erros 5xx geram `ERROR` com a pilha.
- **Actuator:**

| Endpoint | Mostra |
|---|---|
| `/actuator/health` | estado da aplicação e do banco |
| `/actuator/metrics/javabeans.ia.chamadas` | quantidade e tempo das chamadas à IA (por operação e resultado) |
| `/actuator/metrics/http.server.requests` | quantidade e tempo das requisições HTTP |

## Limitações conhecidas
- `ddl-auto: update` cria tabelas e colunas novas, mas não remove colunas nem aplica restrições novas em tabelas que já existem. Em bancos criados antes desta versão, rode uma vez:
  ```sql
  ALTER TABLE sessao ALTER COLUMN session_id SET NOT NULL;
  ALTER TABLE sessao ADD CONSTRAINT uk_sessao_session_id UNIQUE (session_id);
  ```
  Para produção, o recomendado é migrar para Flyway.
- Os endpoints do Actuator não têm autenticação. Em um ambiente público, restrinja o acesso a eles.

## Tecnologias
- Spring Boot 3.3.4
- Java 21
- Maven
- Spring Data JPA / Hibernate 6
- H2 2.2.224 (perfil `dev`)
- PostgreSQL 16 + driver JDBC 42.7.4 (perfil `postgres`)
