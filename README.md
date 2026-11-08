# Trivia Master Android

Aplicativo de perguntas e respostas (quiz) desenvolvido para a disciplina de **Desenvolvimento de Software para Dispositivos Móveis**.

O projeto tem como finalidade exercitar os conceitos fundamentais do desenvolvimento nativo para Android utilizando Java, cobrindo o consumo de APIs REST, persistência local em banco de dados e o uso dos componentes da arquitetura Android.

---

## Sobre o Aplicativo

O **Trivia Master** consome a API pública do [Open Trivia Database (OpenTDB)](https://opentdb.com/) para gerar quizzes interativos.

### Principais recursos:
- **Configuração da Partida**: Escolha de dificuldade (Fácil, Médio ou Difícil) e formato de pergunta (Múltipla Escolha ou Verdadeiro/Falso).
- **Quiz**: Partida com 10 perguntas dinâmicas, pontuação calculada por acerto e retorno visual das respostas.
- **Histórico**: Armazenamento dos resultados de partidas finalizadas em banco de dados SQLite local, com data, pontuação e opções de consulta e limpeza.
- **Execução em segundo plano**: Serviços e receptores para operações assíncronas e notificações.

---

## Componentes Android Implementados

O aplicativo implementa os 4 componentes fundamentais do Android:

1. **Activity**
   - `MainActivity`: Tela inicial para seleção de opções de jogo e navegação.
   - `QuizActivity`: Gerenciamento do fluxo de perguntas, respostas e contagem de pontos.
   - `ResultActivity`: Exibição do resultado da partida e navegação para novo jogo ou histórico.
   - `HistoryActivity`: Exibição do histórico de partidas salvas.

2. **Content Provider**
   - `TriviaProvider`: Expõe e padroniza as operações de consulta, inserção e exclusão no banco de dados local via URI (`content://com.example.triviamaster.provider/partidas`).

3. **Service**
   - `SyncService`: Executa tarefas em segundo plano (como sincronização e rotinas de manutenção de dados) sem bloquear a interface de usuário.

4. **Broadcast Receiver**
   - `AvisoReceiver`: Registrado no `AndroidManifest.xml` para receber e tratar eventos de broadcast do sistema (como `BOOT_COMPLETED`) e notificações do aplicativo.

---

## Estrutura Técnica

- **Linguagem**: Java
- **Target SDK**: Android API 34 (compatibilidade a partir da API 26 / Android 8.0)
- **Persistência Local**: SQLite nativo (`SQLiteOpenHelper`)
- **Rede**: `HttpURLConnection` nativo e manipulação de JSON com `org.json`
- **UI**: Android Views nativas com Material Components

---

## Como Executar o Projeto

1. Clone o repositório:
   ```bash
   git clone https://github.com/FranciscoAnderson2/trabalho-de-mobile.git
   ```
2. Abra o projeto no **Android Studio**.
3. Aguarde o Gradle sincronizar as dependências do projeto.
4. Conecte um aparelho Android via depuração USB ou inicie um emulador (AVD).
5. Clique em **Run 'app'** (`Shift + F10`).
