package com.example.triviamaster;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// Banco de Dados SQLite Local

public class BancoHelper extends SQLiteOpenHelper {

    public static final String NOME_BANCO = "triviamaster.db";
    public static final int VERSAO_BANCO = 1;

    // Tabelas
    public static final String TABELA_PARTIDAS = "partidas";
    public static final String TABELA_QUESTOES = "questoes";

    public BancoHelper(Context contexto) {
        super(contexto, NOME_BANCO, null, VERSAO_BANCO);
    }

    @Override
    public void onCreate(SQLiteDatabase banco) {
        // Tabela para salvar o histórico de partidas dos jogadores
        banco.execSQL("CREATE TABLE " + TABELA_PARTIDAS + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "jogador TEXT NOT NULL, " +
                "pontos INTEGER NOT NULL, " +
                "acertos INTEGER NOT NULL, " +
                "total INTEGER NOT NULL, " +
                "data TEXT NOT NULL);");

        // Tabela para guardar perguntas offline para quando não houver internet
        banco.execSQL("CREATE TABLE " + TABELA_QUESTOES + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "pergunta TEXT NOT NULL, " +
                "correta TEXT NOT NULL, " +
                "incorreta1 TEXT NOT NULL, " +
                "incorreta2 TEXT NOT NULL, " +
                "incorreta3 TEXT NOT NULL);");

        // Inserção de perguntas padrão iniciais para garantir modo offline
        inserirPerguntaOffline(banco, "Qual é a capital do Brasil?", "Brasília", "Rio de Janeiro", "São Paulo", "Salvador");
        inserirPerguntaOffline(banco, "Quantos planetas existem no Sistema Solar?", "8", "9", "7", "10");
        inserirPerguntaOffline(banco, "Quem pintou a Mona Lisa?", "Leonardo da Vinci", "Pablo Picasso", "Vincent van Gogh", "Michelangelo");
        inserirPerguntaOffline(banco, "Qual linguagem é padrão nativa do Android junto com Kotlin?", "Java", "Python", "PHP", "Ruby");
        inserirPerguntaOffline(banco, "Qual é o maior oceano do planeta Terra?", "Pacífico", "Atlântico", "Índico", "Ártico");
    }

    private void inserirPerguntaOffline(SQLiteDatabase banco, String enunciado, String respostaCorreta,
                                       String opcaoErrada1, String opcaoErrada2, String opcaoErrada3) {
        ContentValues dadosDaQuestao = new ContentValues();
        dadosDaQuestao.put("pergunta", enunciado);
        dadosDaQuestao.put("correta", respostaCorreta);
        dadosDaQuestao.put("incorreta1", opcaoErrada1);
        dadosDaQuestao.put("incorreta2", opcaoErrada2);
        dadosDaQuestao.put("incorreta3", opcaoErrada3);
        banco.insert(TABELA_QUESTOES, null, dadosDaQuestao);
    }

    @Override
    public void onUpgrade(SQLiteDatabase banco, int versaoAntiga, int versaoNova) {
        banco.execSQL("DROP TABLE IF EXISTS " + TABELA_PARTIDAS);
        banco.execSQL("DROP TABLE IF EXISTS " + TABELA_QUESTOES);
        onCreate(banco);
    }
}

