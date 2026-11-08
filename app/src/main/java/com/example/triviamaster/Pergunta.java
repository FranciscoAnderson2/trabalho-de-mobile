package com.example.triviamaster;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

// Classe simples para representar uma questão do Quiz
public class Pergunta implements Serializable {

    private String texto;
    private String respostaCorreta;
    private List<String> alternativas;

    public Pergunta(String texto, String respostaCorreta, List<String> alternativas) {
        this.texto = decodificarTexto(texto);
        this.respostaCorreta = decodificarTexto(respostaCorreta);
        this.alternativas = alternativas;

        // Decodifica cada uma das opções e depois embaralha
        for (int indice = 0; indice < this.alternativas.size(); indice++) {
            this.alternativas.set(indice, decodificarTexto(this.alternativas.get(indice)));
        }
        Collections.shuffle(this.alternativas);
    }

    // Trata caracteres que a API envia em código (ex: aspas e apóstrofos)
    public static String decodificarTexto(String textoBruto) {
        if (textoBruto == null) return "";
        return textoBruto.replace("&quot;", "\"")
                         .replace("&#039;", "'")
                         .replace("&amp;", "&")
                         .replace("&lt;", "<")
                         .replace("&gt;", ">");
    }

    public String getTexto() {
        return texto;
    }

    public String getRespostaCorreta() {
        return respostaCorreta;
    }

    public List<String> getAlternativas() {
        return alternativas;
    }
}

