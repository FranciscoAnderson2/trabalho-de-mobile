package com.example.triviamaster;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

//  Tela de Perguntas do Quiz, Cronômetro e Verificação de Resposta

public class QuizActivity extends AppCompatActivity {

    private TextView txtProgresso, txtTempo, txtPergunta;
    private ProgressBar progressTempo;
    private Button btnOpcao1, btnOpcao2, btnOpcao3, btnOpcao4;
    private Button[] botoesOpcoes;

    private String nomeJogador;
    private List<Pergunta> listaPerguntas;
    private int indiceAtual = 0;
    private int totalAcertos = 0;

    private CountDownTimer cronometro;
    private static final long TEMPO_POR_QUESTAO_MS = 20000; // 20 segundos por questão

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Recebe os dados passados pela MainActivity
        nomeJogador = getIntent().getStringExtra("nome_jogador");
        listaPerguntas = (ArrayList<Pergunta>) getIntent().getSerializableExtra("perguntas");

        if (listaPerguntas == null || listaPerguntas.isEmpty()) {
            Toast.makeText(this, "Erro ao carregar perguntas.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        txtProgresso = findViewById(R.id.txtProgresso);
        txtTempo = findViewById(R.id.txtTempo);
        txtPergunta = findViewById(R.id.txtPergunta);
        progressTempo = findViewById(R.id.progressTempo);

        btnOpcao1 = findViewById(R.id.btnOpcao1);
        btnOpcao2 = findViewById(R.id.btnOpcao2);
        btnOpcao3 = findViewById(R.id.btnOpcao3);
        btnOpcao4 = findViewById(R.id.btnOpcao4);
        botoesOpcoes = new Button[]{btnOpcao1, btnOpcao2, btnOpcao3, btnOpcao4};

        configurarCliquesDosBotoes();
        exibirPerguntaAtual();
    }

    private void configurarCliquesDosBotoes() {
        btnOpcao1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View botao) {
                processarResposta(btnOpcao1.getText().toString(), btnOpcao1);
            }
        });

        btnOpcao2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View botao) {
                processarResposta(btnOpcao2.getText().toString(), btnOpcao2);
            }
        });

        btnOpcao3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View botao) {
                processarResposta(btnOpcao3.getText().toString(), btnOpcao3);
            }
        });

        btnOpcao4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View botao) {
                processarResposta(btnOpcao4.getText().toString(), btnOpcao4);
            }
        });
    }

    private void exibirPerguntaAtual() {
        if (indiceAtual >= listaPerguntas.size()) {
            finalizarPartida();
            return;
        }

        Pergunta perguntaAtual = listaPerguntas.get(indiceAtual);

        // Atualiza os textos na interface
        txtProgresso.setText("Questão " + (indiceAtual + 1) + " de " + listaPerguntas.size());
        txtPergunta.setText(perguntaAtual.getTexto());

        List<String> alternativas = perguntaAtual.getAlternativas();
        for (int indice = 0; indice < botoesOpcoes.length; indice++) {
            if (indice < alternativas.size()) {
                botoesOpcoes[indice].setVisibility(View.VISIBLE);
                botoesOpcoes[indice].setText(alternativas.get(indice));
                botoesOpcoes[indice].setEnabled(true);
                botoesOpcoes[indice].setBackgroundColor(Color.parseColor("#4F46E5")); // Cor padrão roxa
                botoesOpcoes[indice].setTextColor(Color.WHITE);
            } else {
                botoesOpcoes[indice].setVisibility(View.GONE);
            }
        }

        iniciarCronometro();
    }

    private void iniciarCronometro() {
        if (cronometro != null) cronometro.cancel();

        progressTempo.setMax(20);
        progressTempo.setProgress(20);

        // Temporizador de 20 segundos por questão
        cronometro = new CountDownTimer(TEMPO_POR_QUESTAO_MS, 1000) {
            @Override
            public void onTick(long milissegundosRestantes) {
                int segundosRestantes = (int) (milissegundosRestantes / 1000);
                txtTempo.setText("Tempo: " + segundosRestantes + "s");
                progressTempo.setProgress(segundosRestantes);
            }

            @Override
            public void onFinish() {
                txtTempo.setText("Tempo esgotado!");
                progressTempo.setProgress(0);
                // Quando o tempo acaba, trata como resposta errada e mostra a correta
                processarResposta("", null);
            }
        }.start();
    }

    private void processarResposta(String respostaEscolhida, Button botaoClicado) {
        if (cronometro != null) cronometro.cancel();

        // Desabilita os botões para não clicar duas vezes
        for (Button botao : botoesOpcoes) {
            botao.setEnabled(false);
        }

        Pergunta pergunta = listaPerguntas.get(indiceAtual);
        String alternativaCorreta = pergunta.getRespostaCorreta();

        if (respostaEscolhida.equals(alternativaCorreta)) {
            // Acertou: Pinta o botão de verde!
            totalAcertos++;
            if (botaoClicado != null) {
                botaoClicado.setBackgroundColor(Color.parseColor("#10B981"));
            }
        } else {
            // Errou: Pinta o botão clicado de vermelho e acha o certo para pintar de verde
            if (botaoClicado != null) {
                botaoClicado.setBackgroundColor(Color.parseColor("#EF4444"));
            }
            for (Button botao : botoesOpcoes) {
                if (botao.getText().toString().equals(alternativaCorreta)) {
                    botao.setBackgroundColor(Color.parseColor("#10B981"));
                }
            }
        }

        // Aguarda 1,5 segundos para o jogador ver o resultado e avança
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                indiceAtual++;
                exibirPerguntaAtual();
            }
        }, 1500);
    }

    private void finalizarPartida() {
        if (cronometro != null) cronometro.cancel();

        // Cada acerto vale 100 pontos
        int pontosFinais = totalAcertos * 100;

        Intent telaResultado = new Intent(QuizActivity.this, ResultActivity.class);
        telaResultado.putExtra("nome_jogador", nomeJogador);
        telaResultado.putExtra("pontos", pontosFinais);
        telaResultado.putExtra("acertos", totalAcertos);
        telaResultado.putExtra("total", listaPerguntas.size());
        startActivity(telaResultado);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cronometro != null) cronometro.cancel();
    }
}

