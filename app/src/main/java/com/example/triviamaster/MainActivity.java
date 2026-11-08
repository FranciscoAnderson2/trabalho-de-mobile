package com.example.triviamaster;

import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Tela Principal de Configuração e Navegação do Jogo

public class MainActivity extends AppCompatActivity {

    private EditText editNome;
    private Spinner spinnerCategoria, spinnerDificuldade, spinnerQuantidade;
    private Button btnIniciar, btnSincronizar, btnHistorico;
    private ProgressBar progressBar;

    private ExecutorService executorSegundoPlano;
    private Handler handlerPrincipal;
    private AvisoReceiver avisoReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializa concorrência e o receptor de avisos
        executorSegundoPlano = Executors.newSingleThreadExecutor();
        handlerPrincipal = new Handler(Looper.getMainLooper());
        avisoReceiver = new AvisoReceiver();

        // Vincula os componentes visuais do XML
        editNome = findViewById(R.id.editNome);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        spinnerDificuldade = findViewById(R.id.spinnerDificuldade);
        spinnerQuantidade = findViewById(R.id.spinnerQuantidade);
        btnIniciar = findViewById(R.id.btnIniciar);
        btnSincronizar = findViewById(R.id.btnSincronizar);
        btnHistorico = findViewById(R.id.btnHistorico);
        progressBar = findViewById(R.id.progressBar);

        configurarSpinners();
        configurarBotoes();
    }

    private void configurarSpinners() {
        // Categorias simples
        String[] categorias = {"Qualquer Categoria", "Conhecimentos Gerais", "Ciência & Natureza", "Computação", "História"};
        ArrayAdapter<String> adaptadorCategorias = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categorias);
        spinnerCategoria.setAdapter(adaptadorCategorias);

        // Dificuldade
        String[] dificuldades = {"Qualquer", "Fácil", "Médio", "Difícil"};
        ArrayAdapter<String> adaptadorDificuldades = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, dificuldades);
        spinnerDificuldade.setAdapter(adaptadorDificuldades);

        // Quantidade de questões
        String[] quantidades = {"5", "10", "15"};
        ArrayAdapter<String> adaptadorQuantidades = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, quantidades);
        spinnerQuantidade.setAdapter(adaptadorQuantidades);
    }

    private void configurarBotoes() {
        // 1. Botão Iniciar Partida
        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View botao) {
                String nome = editNome.getText().toString().trim();
                if (nome.isEmpty()) {
                    editNome.setError("Por favor, digite seu nome!");
                    return;
                }
                carregarPerguntasEIniciar(nome);
            }
        });

        // 2. Botão Sincronizar (Chama o Service em segundo plano)
        btnSincronizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View botao) {
                Toast.makeText(MainActivity.this, "Baixando perguntas em segundo plano...", Toast.LENGTH_SHORT).show();
                Intent servicoSincronizacao = new Intent(MainActivity.this, SyncService.class);
                startService(servicoSincronizacao);
            }
        });

        // 3. Botão Histórico (Abre a tela de histórico)
        btnHistorico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View botao) {
                Intent telaHistorico = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(telaHistorico);
            }
        });
    }

    // Busca perguntas na API (ou no banco se estiver sem internet)
    private void carregarPerguntasEIniciar(final String nomeJogador) {
        progressBar.setVisibility(View.VISIBLE);
        btnIniciar.setEnabled(false);

        // Converte as opções do spinner para parâmetros da API
        final int quantidade = Integer.parseInt(spinnerQuantidade.getSelectedItem().toString());

        String categoriaSelecionada = spinnerCategoria.getSelectedItem().toString();
        String idCategoria = "";
        if (categoriaSelecionada.equals("Conhecimentos Gerais")) idCategoria = "9";
        else if (categoriaSelecionada.equals("Ciência & Natureza")) idCategoria = "17";
        else if (categoriaSelecionada.equals("Computação")) idCategoria = "18";
        else if (categoriaSelecionada.equals("História")) idCategoria = "23";

        String dificuldadeSelecionada = spinnerDificuldade.getSelectedItem().toString();
        String parametroDificuldade = "";
        if (dificuldadeSelecionada.equals("Fácil")) parametroDificuldade = "easy";
        else if (dificuldadeSelecionada.equals("Médio")) parametroDificuldade = "medium";
        else if (dificuldadeSelecionada.equals("Difícil")) parametroDificuldade = "hard";

        final String idCategoriaParaBuscar = idCategoria;
        final String parametroDificuldadeParaBuscar = parametroDificuldade;

        executorSegundoPlano.execute(new Runnable() {
            @Override
            public void run() {
                List<Pergunta> perguntasCarregadas;
                boolean modoOffline = false;

                try {
                    // Tenta buscar perguntas online na API
                    perguntasCarregadas = ApiClient.buscarPerguntas(quantidade, idCategoriaParaBuscar, parametroDificuldadeParaBuscar);
                } catch (Exception erro) {
                    // Se falhar (ex: sem internet), busca do banco de dados local via Content Provider!
                    perguntasCarregadas = carregarPerguntasDoBancoLocal(quantidade);
                    modoOffline = true;
                }

                final List<Pergunta> perguntasParaEnviar = perguntasCarregadas;
                final boolean estaNoModoOffline = modoOffline;

                handlerPrincipal.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        btnIniciar.setEnabled(true);

                        if (perguntasParaEnviar == null || perguntasParaEnviar.isEmpty()) {
                            Toast.makeText(MainActivity.this, "Nenhuma pergunta disponível. Conecte à internet ou sincronize perguntas.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (estaNoModoOffline) {
                            Toast.makeText(MainActivity.this, "Sem conexão com a internet. Usando perguntas offline salvas no banco!", Toast.LENGTH_LONG).show();
                        }

                        // Inicia a QuizActivity passando a lista de perguntas e o nome do jogador
                        Intent telaQuiz = new Intent(MainActivity.this, QuizActivity.class);
                        telaQuiz.putExtra("nome_jogador", nomeJogador);
                        telaQuiz.putExtra("perguntas", (Serializable) perguntasParaEnviar);
                        startActivity(telaQuiz);
                    }
                });
            }
        });
    }

    // Busca questões do SQLite através do Content Provider
    private List<Pergunta> carregarPerguntasDoBancoLocal(int limite) {
        List<Pergunta> lista = new ArrayList<>();
        Cursor cursorDoBanco = getContentResolver().query(TriviaProvider.URI_QUESTOES, null, null, null, null);

        if (cursorDoBanco != null) {
            while (cursorDoBanco.moveToNext() && lista.size() < limite) {
                String enunciado = cursorDoBanco.getString(cursorDoBanco.getColumnIndexOrThrow("pergunta"));
                String respostaCorreta = cursorDoBanco.getString(cursorDoBanco.getColumnIndexOrThrow("correta"));
                String opcaoIncorreta1 = cursorDoBanco.getString(cursorDoBanco.getColumnIndexOrThrow("incorreta1"));
                String opcaoIncorreta2 = cursorDoBanco.getString(cursorDoBanco.getColumnIndexOrThrow("incorreta2"));
                String opcaoIncorreta3 = cursorDoBanco.getString(cursorDoBanco.getColumnIndexOrThrow("incorreta3"));

                List<String> alternativas = new ArrayList<>();
                alternativas.add(respostaCorreta);
                if (opcaoIncorreta1 != null && !opcaoIncorreta1.isEmpty()) alternativas.add(opcaoIncorreta1);
                if (opcaoIncorreta2 != null && !opcaoIncorreta2.isEmpty()) alternativas.add(opcaoIncorreta2);
                if (opcaoIncorreta3 != null && !opcaoIncorreta3.isEmpty()) alternativas.add(opcaoIncorreta3);

                lista.add(new Pergunta(enunciado, respostaCorreta, alternativas));
            }
            cursorDoBanco.close();
        }
        return lista;
    }

    // Ciclo de vida: registra e desregistra o BroadcastReceiver
    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filtroAviso = new IntentFilter(AvisoReceiver.ACAO_SINCRONIZACAO_FINALIZADA);
        androidx.core.content.ContextCompat.registerReceiver(this, avisoReceiver, filtroAviso, androidx.core.content.ContextCompat.RECEIVER_NOT_EXPORTED);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(avisoReceiver);
        } catch (Exception erro) {}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorSegundoPlano != null) {
            executorSegundoPlano.shutdown();
        }
    }
}

