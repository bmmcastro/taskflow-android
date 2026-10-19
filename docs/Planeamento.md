# TaskFlow — Trabalho Individual · Planeamento

**UC00609 — Desenvolver aplicações móveis (plataforma Android)**
**Autor:** Bruno Miguel Marques de Castro · **Formador:** Rui Vilares · **Data:** 14/10/2026

## 1. A app

**Objetivo.** Vou desenvolver uma app Android em Kotlin para gerir projetos e tarefas em equipa, no estilo de quadros tipo Trello. É a versão móvel do **TaskFlow**, um projeto web que desenvolvi com colegas na UC00605 e que está em produção em `https://taskflow.algarit.pt`. A app consome a API REST do próprio site, por isso uma tarefa movida no telemóvel aparece logo no site e vice-versa.

**Público-alvo.** Pequenas equipas que precisam de organizar trabalho: trabalhos de escola, projetos de curso, pequenas empresas. Qualquer pessoa que queira ver e atualizar as suas tarefas a partir do telemóvel.

**Porque escolhi este tema.** As sugestões do enunciado usam APIs públicas de exemplo. Prefiro usar a API real do TaskFlow por três razões: já a conheço bem (fui eu que desenvolvi o recurso Tarefas no projeto web), tem casos reais que uma API de demonstração não tem (autenticação, validações, erros de negócio), e o resultado final é útil de verdade para quem já usa o site.

**API.** REST, em JSON, hospedada em `https://taskflow.algarit.pt`. Endpoints principais:

| Endpoint | Métodos | Para quê |
|---|---|---|
| `/utilizadores/registo` | POST | Criar conta |
| `/utilizadores/login` | POST | Autenticar |
| `/projetos` | GET, POST | Listar e criar projetos |
| `/projetos/{id}/membros` | GET, POST | Membros do projeto |
| `/tarefas` | GET, POST | Tarefas de um projeto |
| `/tarefas/{id}` | PATCH, DELETE | Atualizar e eliminar tarefa |
| `/tarefas/relatorio` | GET | Estatísticas do projeto |
| `/subtarefas` | GET, POST, PATCH, DELETE | Checklist de cada tarefa |

Para testes em casa uso `http://10.0.2.2:3000` (o emulador aponta para o servidor Node local, com `npm start`), para não depender da internet. O pedido de autenticação é simples (sem token): depois do login a app guarda o id do utilizador e envia-o nos pedidos.

## 2. User stories e priorização (MoSCoW)

| ID | User story | MoSCoW | Critérios de aceitação |
|---|---|---|---|
| US01 | Como utilizador, quero registar uma conta para poder usar a app. | Must | Email único, campos validados; após o registo vou para o login. |
| US02 | Como utilizador, quero iniciar sessão para aceder aos meus projetos. | Must | Credenciais erradas mostram erro; corretas abrem a lista de projetos. |
| US03 | Como utilizador, quero ver a lista dos projetos a que pertenço, com indicação de carregamento. | Must | Dados vêm da API com ProgressBar; sem rede aparece mensagem clara. |
| US04 | Como gestor, quero criar um projeto para organizar o trabalho da equipa. | Must | Nome obrigatório; o criador fica automaticamente como membro. |
| US05 | Como membro, quero ver o quadro do projeto com as colunas A Fazer, Em Curso e Concluído. | Must | As 3 colunas com os cartões; cada cartão mostra título e nº de subtarefas. |
| US06 | Como membro, quero criar uma tarefa com título, descrição e coluna. | Must | Título obrigatório (EditText com validação); a coluna escolhe-se num Spinner. |
| US07 | Como membro, quero mover uma tarefa de coluna para mudar o seu estado. | Must | Botões no detalhe da tarefa; a mudança fica gravada na API. |
| US08 | Como membro, quero editar e eliminar tarefas. | Must | Editar título/descrição; eliminar pede confirmação. |
| US09 | Como utilizador, quero terminar sessão. | Should | Botão na barra superior; volto ao login. |
| US10 | Como gestor, quero ver os membros e adicionar um membro por email. | Should | Lista com nome e email; o email tem de existir; sem duplicados. |
| US11 | Como membro, quero uma checklist de subtarefas dentro da tarefa. | Should | Adicionar e marcar itens; contador aparece no cartão. |
| US12 | Como membro, quero mensagens claras quando algo falha (sem rede, timeout). | Should | Toast ou Snackbar com a mensagem; a app nunca bloqueia sem aviso. |
| US13 | Como gestor, quero um relatório do projeto com tarefas por coluna e % de progresso. | Could | Contagens corretas, vindas do endpoint de relatório. |

**Won't have (nesta versão):** modo offline (os dados vivem no servidor), notificações push, recuperação de palavra-passe por email, e arrastar e largar cartões (a v1 move tarefas com botões; o drag fica para uma versão futura).

## 3. Quadro Kanban

Regras: fluxo Backlog → A Fazer → Em Curso (máx. 2) → Em Revisão → Concluído; uma tarefa só passa a Concluído depois de testada no emulador. Cada tarefa indica a user story e o critério da grelha de avaliação a que responde.

| Backlog | A Fazer | Em Curso | Em Revisão | Concluído |
|---|---|---|---|---|
| T08 Criar projeto (dialog) · US04 | T01 Criar projeto no Android Studio (Kotlin, ViewBinding, ConstraintLayout, minSdk 26) | T03 Modelos: data classes, enum EstadoTarefa, sealed UiState | | |
| T10 Nova/editar tarefa (Spinner) · US06 | T02 Tema e recursos (strings, colors, themes, dimens) | T04 Retrofit + Gson e tratamento de erros (sem rede, timeout, resposta inválida) | | |
| T09 Board com colunas · US05 · | T05 Activity única + Navigation (Fragments + grafo de navegação) | | | |
| T11 Detalhe da tarefa (checklist, mover) · US07–08 | T06 Login (EditText validado + ViewModel) · US02 | | | |
| T12 Membros · US10 | T07 Projetos: RecyclerView + ListAdapter + ProgressBar · US03 | | | |
| T13 Relatório · US13 | | | | |
| T14 Testes unitários (JUnit) · C7 | | | | |
| T15 Testes no emulador + dispositivo físico; registo de erros | | | | |
| T16 Relatório técnico, RGPD, licenças, manual · C8–C9 | | | | |
| T17 README + publicação no GitHub | | | | |

Ordem prevista: semana 1 (17–23 out) T01–T04, T06, T07 · semana 2 (24–30 out) T05, T08–T11 · semana 3 (31 out–6 nov) T12–T14 · semana 4 (7–19 nov) T15–T17 e correções.

## 4. Wireframes e fluxo de navegação

A app tem 7 ecrãs, implementados como **Fragments** dentro de uma única Activity com **Jetpack Navigation**: Login, Registo, Projetos, Board, Detalhe da Tarefa, Membros e Relatório. Criar projeto é um dialog sobre a lista de projetos.

Fluxo: Login → Projetos (home) → Board → Detalhe da Tarefa; do Board abre-se Membros e o Relatório; o botão voltar do sistema faz sempre o esperado, e a passagem do id da tarefa para o detalhe é feita com argumentos do grafo de navegação.

Os desenhos dos ecrãs e o diagrama do fluxo estão no anexo `wireframes.html` (entregue com este documento).

## 5. Requisitos técnicos previstos

| Exigência do enunciado | Como vou implementar |
|---|---|
| ConstraintLayout | Todos os layouts |
| ViewBinding em todos os ecrãs | `binding` em cada Fragment |
| RecyclerView + ListAdapter + cliques | Lista de projetos, cartões do board, subtarefas, membros |
| Navigation (Jetpack) + Fragments | 1 Activity + NavHost + grafo com argumentos |
| MVVM | Um ViewModel por ecrã, com a lógica fora dos Fragments |
| Coroutines + ProgressBar | Chamadas à API com `suspend` e indicador de carregamento |
| UI variada | EditText com validação, Spinner (coluna), Switch (filtro), ImageView, Toast e Snackbar |
| data class / enum / sealed | Projeto, Tarefa e Subtarefa; EstadoTarefa; UiState (Loading/Sucesso/Erro) |
| Null safety | Tipos anuláveis da API tratados com `?.` e `?:`, sem `!!` |
| Testes | JUnit nas validações e modelos; registo dos erros encontrados |

## 6. Entregáveis

1. Código-fonte (GitHub) com README a explicar como compilar e executar
2. Este documento de planeamento
3. Relatório técnico: arquitetura, endpoints usados, decisões técnicas, testes, normas (RGPD, permissões, licenças)
4. Manual de utilizador

Prazo: Moodle, até às 23h55 de 20 de novembro de 2026.
