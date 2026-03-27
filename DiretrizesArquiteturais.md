# DiretrizesArquiteturais.md

## Diretrizes Arquiteturais do Projeto JavaBeans-IA

Todas as análises, sugestões de refatoração, alterações contratuais de API, propostas de integração com IA generativa e geração de código para o projeto JavaBeans-IA devem obedecer irrestritamente às restrições estruturais definidas neste documento.

Este arquivo estabelece a política arquitetural oficial do sistema e possui precedência sobre sugestões heurísticas, propostas de automação ad hoc ou recomendações genéricas produzidas por assistentes, modelos de linguagem ou ferramentas de apoio.

---

## 1. Princípio da Fonte de Verdade Autoritativa

O backend Spring Boot constitui a única fonte de verdade autoritativa para todo o domínio determinístico da aplicação.

Isso inclui, de forma explícita e indelegável:

- controle de estado de sessão;
- progressão do fluxo do jogo;
- definição do pedido atual do cliente;
- validação de receitas;
- comparação entre ingredientes esperados e ingredientes selecionados;
- cálculo de pontuação;
- definição de sucesso ou erro da rodada;
- composição do resultado oficial exibido ao usuário.

Nenhum componente do frontend, modelo de linguagem ou mecanismo probabilístico pode substituir, reinterpretar ou sobrepor a autoridade semântica do backend quanto ao estado real do sistema.

---

## 2. Separação Estrita de Responsabilidades (Separation of Concerns)

A arquitetura do projeto deve preservar separação estrita entre três camadas de responsabilidade:

### 2.1. Frontend (React)
Responsável exclusivamente por:

- coleta de entrada do usuário;
- renderização da interface;
- transição visual entre estados da aplicação;
- envio de requisições ao backend;
- exibição de resultados devolvidos pelo backend.

O frontend não pode assumir papel de autoridade sobre regras do jogo, cálculo de pontuação ou validação semântica de receita.

### 2.2. Backend Determinístico (Spring Boot)
Responsável exclusivamente por:

- execução das regras formais do jogo;
- validação de hard skills;
- gestão do estado oficial da rodada;
- consolidação dos dados recebidos do cliente;
- orquestração entre serviços determinísticos e serviços auxiliares de linguagem.

### 2.3. Módulo de IA Generativa
Responsável exclusivamente por:

- análise textual da fala do aluno;
- avaliação de comunicação, cordialidade, empatia e clareza;
- geração de comentários narrativos sobre soft skills.

A IA generativa não possui autoridade sobre estado, pontuação, acerto de receita ou qualquer aspecto algorítmico determinístico.

---

## 3. Proibição do Antipadrão “Oráculo Estocástico”

É terminantemente vedada a adoção do antipadrão arquitetural no qual a Inteligência Artificial generativa é utilizada como mecanismo de decisão para regras formais do sistema.

Configura violação arquitetural qualquer proposta que atribua à IA a responsabilidade de:

- determinar se a bebida está correta;
- inferir se os ingredientes escolhidos correspondem à receita;
- calcular pontos;
- arbitrar sucesso ou fracasso da rodada;
- interpretar o estado interno da aplicação sem mediação do backend;
- comentar propriedades físicas não observáveis do sistema, como sabor, textura real, temperatura ou qualidade factual do café preparado.

A IA não detém acesso privilegiado ao estado autoritativo do sistema. Logo, qualquer deliberação probabilística sobre hard skills constitui inferência não confiável e será considerada arquiteturalmente inválida.

---

## 4. Escopo Restrito da IA Generativa

A IA generativa atua estritamente como módulo auxiliar de Processamento de Linguagem Natural.

Seu escopo admissível restringe-se à avaliação de competências comportamentais do usuário, tais como:

- cordialidade;
- clareza discursiva;
- adequação comunicacional;
- empatia;
- postura profissional;
- coerência narrativa da interação com o cliente.

A IA somente pode operar sobre variáveis textuais explicitamente fornecidas, como:

- `respostaAluno`
- `nomeBarista`

A IA não deve receber a incumbência de validar hard skills nem de inferir regras de jogo a partir de linguagem natural.

Em formulação sintética:

> A IA é componente auxiliar de interpretação linguística, nunca autoridade de domínio.

---

## 5. Classificação Formal dos Domínios do Sistema

Para evitar ambiguidade conceitual, o sistema deve ser tratado como composto por dois domínios distintos:

### 5.1. Domínio Determinístico (Hard Skill)
Inclui:

- receita esperada;
- ingredientes selecionados;
- comparação entre vetores;
- acerto ou erro;
- pontuação;
- progressão de rodada.

Esse domínio pertence integralmente ao backend.

### 5.2. Domínio Interpretativo (Soft Skill)
Inclui:

- fala do usuário;
- tom da interação;
- qualidade comunicacional;
- adequação do atendimento.

Esse domínio pode ser processado pela IA, desde que o backend permaneça como orquestrador e mantenha o controle do resultado consolidado.

Nenhuma solução arquitetural válida pode fundir esses dois domínios em uma única autoridade probabilística.

---

## 6. Integridade dos Contratos de API

Toda alteração em endpoints, DTOs, contratos JSON ou chamadas `fetch` deve preservar a bifurcação semântica entre dados determinísticos e dados textuais.

O payload ideal deve transportar simultaneamente:

- vetores determinísticos do preparo;
- variáveis textuais da interação.

Exemplo de contrato admissível:

```json
{
  "ingredientesEscolhidos": ["CAFE", "LEITE", "ESPUMA"],
  "respostaAluno": "Já vou preparar seu cappuccino agora.",
  "nomeBarista": "André"
}