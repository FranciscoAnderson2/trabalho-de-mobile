package com.example.triviamaster;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

// Conexão HTTP REST e Leitura do JSON

public class ApiClient {

    // Método que faz o GET na API pública Open Trivia DB
    public static List<Pergunta> buscarPerguntas(int quantidade, String idCategoria, String dificuldade) throws Exception {
        // Monta a URL dinâmica com os parâmetros configurados pelo jogador
        String enderecoUrl = "https://opentdb.com/api.php?type=multiple&amount=" + quantidade;

        if (idCategoria != null && !idCategoria.isEmpty()) {
            enderecoUrl += "&category=" + idCategoria;
        }
        if (dificuldade != null && !dificuldade.isEmpty() && !dificuldade.equals("any")) {
            enderecoUrl += "&difficulty=" + dificuldade.toLowerCase();
        }

        URL url = new URL(enderecoUrl);
        HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
        conexao.setRequestMethod("GET");
        conexao.setConnectTimeout(6000);
        conexao.setReadTimeout(6000);

        int codigoResposta = conexao.getResponseCode();
        if (codigoResposta != HttpURLConnection.HTTP_OK) {
            throw new Exception("Erro HTTP: " + codigoResposta);
        }

        // Lê a resposta enviada pelo servidor
        BufferedReader leitor = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
        StringBuilder respostaJson = new StringBuilder();
        String linha;
        while ((linha = leitor.readLine()) != null) {
            respostaJson.append(linha);
        }
        leitor.close();
        conexao.disconnect();

        // Faz a leitura dos dados em formato JSON
        JSONObject jsonResposta = new JSONObject(respostaJson.toString());
        int codigoStatusApi = jsonResposta.optInt("response_code", -1);
        if (codigoStatusApi != 0) {
            throw new Exception("A API retornou código: " + codigoStatusApi);
        }

        JSONArray resultados = jsonResposta.getJSONArray("results");
        List<Pergunta> listaDePerguntas = new ArrayList<>();

        for (int indice = 0; indice < resultados.length(); indice++) {
            JSONObject questaoJson = resultados.getJSONObject(indice);
            String textoPergunta = questaoJson.getString("question");
            String respostaCorreta = questaoJson.getString("correct_answer");

            JSONArray alternativasIncorretas = questaoJson.getJSONArray("incorrect_answers");
            List<String> opcoes = new ArrayList<>();
            opcoes.add(respostaCorreta);
            for (int indiceIncorreta = 0; indiceIncorreta < alternativasIncorretas.length(); indiceIncorreta++) {
                opcoes.add(alternativasIncorretas.getString(indiceIncorreta));
            }

            listaDePerguntas.add(new Pergunta(textoPergunta, respostaCorreta, opcoes));
        }

        return listaDePerguntas;
    }
}

