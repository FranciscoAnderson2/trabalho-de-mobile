package com.example.triviamaster;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

// Atua como ponte oficial de acesso aos dados do SQLite via URIs

public class TriviaProvider extends ContentProvider {

    public static final String AUTORIDADE = "com.example.triviamaster.provider";
    public static final Uri URI_PARTIDAS = Uri.parse("content://" + AUTORIDADE + "/partidas");
    public static final Uri URI_QUESTOES = Uri.parse("content://" + AUTORIDADE + "/questoes");

    private static final int CODIGO_PARTIDAS = 1;
    private static final int CODIGO_QUESTOES = 2;

    // Utilitário do Android para identificar qual URI foi chamada
    private static final UriMatcher comparadorDeUri = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        comparadorDeUri.addURI(AUTORIDADE, "partidas", CODIGO_PARTIDAS);
        comparadorDeUri.addURI(AUTORIDADE, "questoes", CODIGO_QUESTOES);
    }

    private BancoHelper bancoHelper;

    @Override
    public boolean onCreate() {
        bancoHelper = new BancoHelper(getContext());
        return true;
    }

    // Consulta dados no banco através da URI correspondente
    @Override
    public Cursor query(Uri uri, String[] colunasDesejadas, String filtro, String[] argumentosFiltro, String ordenacao) {
        SQLiteDatabase banco = bancoHelper.getReadableDatabase();
        int tipoRequisicao = comparadorDeUri.match(uri);

        if (tipoRequisicao == CODIGO_PARTIDAS) {
            return banco.query(BancoHelper.TABELA_PARTIDAS, colunasDesejadas, filtro, argumentosFiltro, null, null, ordenacao);
        } else if (tipoRequisicao == CODIGO_QUESTOES) {
            return banco.query(BancoHelper.TABELA_QUESTOES, colunasDesejadas, filtro, argumentosFiltro, null, null, ordenacao);
        }
        return null;
    }

    // Insere dados no banco através da URI correspondente
    @Override
    public Uri insert(Uri uri, ContentValues valoresParaInserir) {
        SQLiteDatabase banco = bancoHelper.getWritableDatabase();
        int tipoRequisicao = comparadorDeUri.match(uri);
        long idInserido = -1;

        if (tipoRequisicao == CODIGO_PARTIDAS) {
            idInserido = banco.insert(BancoHelper.TABELA_PARTIDAS, null, valoresParaInserir);
            if (idInserido > 0) return ContentUris.withAppendedId(URI_PARTIDAS, idInserido);
        } else if (tipoRequisicao == CODIGO_QUESTOES) {
            idInserido = banco.insert(BancoHelper.TABELA_QUESTOES, null, valoresParaInserir);
            if (idInserido > 0) return ContentUris.withAppendedId(URI_QUESTOES, idInserido);
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String filtro, String[] argumentosFiltro) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues valores, String filtro, String[] argumentosFiltro) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }
}

