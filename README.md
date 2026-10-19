# TaskFlow — App Android

Aplicação Android (Kotlin) de gestão de projetos e tarefas em equipa. É o cliente móvel do [TaskFlow](https://taskflow.algarit.pt) — a app consome a API REST do site, por isso as tarefas ficam sempre sincronizadas entre o telemóvel e o navegador.

## Funcionalidades

- Login e registo de utilizadores
- Lista dos projetos a que pertenço
- Quadro do projeto com as colunas A Fazer / Em Curso / Concluído
- Criar, editar, mover e eliminar tarefas
- Checklist de subtarefas em cada tarefa
- Gestão de membros do projeto
- Relatório do projeto (tarefas por coluna e % de progresso)

## Tecnologia

- **Kotlin** · Android SDK 35 (minSdk 26)
- ViewBinding + ConstraintLayout
- Jetpack Navigation (Fragments)
- MVVM com ViewModel por ecrã
- Coroutines para as chamadas de rede
- Retrofit + Gson (API REST em JSON)

## Como compilar e executar

1. Instalar o [Android Studio](https://developer.android.com/studio) (com SDK 35)
2. Abrir a pasta do projeto (**File → Open**)
3. Esperar pelo Gradle sync
4. Escolher um emulador ou dispositivo físico e carregar em **Run**

A app liga-se à API em produção (`https://taskflow.algarit.pt`). Para testar contra um servidor local do TaskFlow web (`npm start` na pasta do site), basta alterar o endereço base na configuração da API (`10.0.2.2` é o PC visto do emulador).

## Estrutura

```
app/src/main/
├── java/pt/algarit/taskflow/
│   ├── data/          modelos, API (Retrofit) e repositórios
│   ├── ui/            fragments, adapters e viewmodels por ecrã
│   └── ...
└── res/
    ├── layout/        layouts em ConstraintLayout
    ├── navigation/    grafo de navegação
    └── values/        strings, colors, themes
```

## Documentação

- [Documento de planeamento](docs/Planeamento.md) — objetivos, user stories (MoSCoW), Kanban, wireframes e fluxo de navegação
- [Wireframes](docs/wireframes.html) — desenhos dos ecrãs e diagrama do fluxo

## Estado

Em desenvolvimento — trabalho individual da UC00609 (2026).
