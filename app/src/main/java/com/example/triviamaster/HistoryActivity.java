package com.example.triviamaster;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

// Tela de Histórico: Consulta as partidas salvas usando o Content Provider

public class HistoryActivity extends AppCompatActivity {

    private ListView listViewHistorico;
    private TextView txtSemHistorico;
    private Button btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listViewHistorico = findViewById(R.id.listViewHistorico);
        txtSemHistorico = findViewById(R.id.txtSemHistorico);
        btnVoltar = findViewById(R.id.btnVoltar);

        carregarHistoricoDoBanco();

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View botao) {
                finish();
            }
        });
    }

    // Consulta as partidas no SQLite através do Content Provider
    private void carregarHistoricoDoBanco() {
        List<String> listaFormatada = new ArrayList<>();
        Cursor cursorDoBanco = getContentResolver().query(TriviaProvider.URI_PARTIDAS, null, null, null, "_id DESC");

        if (cursorDoBanco != null) {
            while (cursorDoBanco.moveToNext()) {
                String jogador = cursorDoBanco.getString(cursorDoBanco.getColumnIndexOrThrow("jogador"));
                int pontos = cursorDoBanco.getInt(cursorDoBanco.getColumnIndexOrThrow("pontos"));
                int acertos = cursorDoBanco.getInt(cursorDoBanco.getColumnIndexOrThrow("acertos"));
                int total = cursorDoBanco.getInt(cursorDoBanco.getColumnIndexOrThrow("total"));
                String data = cursorDoBanco.getString(cursorDoBanco.getColumnIndexOrThrow("data"));

                String linha = jogador + "  •  " + pontos + " pts\n" +
                        "Acertos: " + acertos + "/" + total + "  |  Data: " + data;
                listaFormatada.add(linha);
            }
            cursorDoBanco.close();
        }

        if (listaFormatada.isEmpty()) {
            txtSemHistorico.setVisibility(View.VISIBLE);
            listViewHistorico.setVisibility(View.GONE);
        } else {
            txtSemHistorico.setVisibility(View.GONE);
            listViewHistorico.setVisibility(View.VISIBLE);
            ArrayAdapter<String> adaptadorDaLista = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaFormatada);
            listViewHistorico.setAdapter(adaptadorDaLista);
        }
    }
}

