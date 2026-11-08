package com.example.triviamaster;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

// Tela de Resultado: Exibe desempenho e salva a partida no SQLite via Content Provider

public class ResultActivity extends AppCompatActivity {

    private TextView txtNomeJogador, txtPontuacao, txtResumoAcertos;
    private Button btnJogarNovamente, btnVerHistorico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        txtNomeJogador = findViewById(R.id.txtNomeJogador);
        txtPontuacao = findViewById(R.id.txtPontuacao);
        txtResumoAcertos = findViewById(R.id.txtResumoAcertos);
        btnJogarNovamente = findViewById(R.id.btnJogarNovamente);
        btnVerHistorico = findViewById(R.id.btnVerHistorico);

        // Recupera os dados enviados pela QuizActivity
        String nome = getIntent().getStringExtra("nome_jogador");
        int pontos = getIntent().getIntExtra("pontos", 0);
        int acertos = getIntent().getIntExtra("acertos", 0);
        int total = getIntent().getIntExtra("total", 0);

        if (nome == null || nome.isEmpty()) nome = "Jogador";

        txtNomeJogador.setText("Parabéns, " + nome + "!");
        txtPontuacao.setText(pontos + " Pontos");
        txtResumoAcertos.setText("Você acertou " + acertos + " de " + total + " questões");

        // Salva o resultado no banco SQLite através do Content Provider
        salvarResultadoNoBanco(nome, pontos, acertos, total);

        // Botão para jogar novamente (apenas fecha a tela atual e volta para o início)
        btnJogarNovamente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View botao) {
                finish();
            }
        });

        // Botão para abrir o histórico
        btnVerHistorico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View botao) {
                Intent telaHistorico = new Intent(ResultActivity.this, HistoryActivity.class);
                startActivity(telaHistorico);
            }
        });
    }

    // Salva a partida localmente no banco usando o Content Provider
    private void salvarResultadoNoBanco(String nome, int pontos, int acertos, int total) {
        String dataAtual = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());

        // Objeto para organizar os dados da partida antes de salvar
        ContentValues dadosDaPartida = new ContentValues();
        dadosDaPartida.put("jogador", nome);
        dadosDaPartida.put("pontos", pontos);
        dadosDaPartida.put("acertos", acertos);
        dadosDaPartida.put("total", total);
        dadosDaPartida.put("data", dataAtual);

        // Insere no banco através do Content Provider
        getContentResolver().insert(TriviaProvider.URI_PARTIDAS, dadosDaPartida);
        Toast.makeText(this, "Partida salva no histórico com sucesso!", Toast.LENGTH_SHORT).show();
    }
}

