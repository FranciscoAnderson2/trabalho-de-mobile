package com.example.triviamaster;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

// Testes unitários para validar a lógica básica do Quiz
public class TriviaTest {

    @Test
    public void testDecodificacaoTextoHtml() {
        String textoComHtml = "&quot;Hello &amp; World&#039;s&quot;";
        String decodificado = Pergunta.decodificarTexto(textoComHtml);
        assertNotNull(decodificado);
        assertTrue(decodificado.contains("Hello"));
    }

    @Test
    public void testCalculoPontuacao() {
        int acertos = 4;
        int pontosPorAcerto = 100;
        int pontuacaoTotal = acertos * pontosPorAcerto;
        assertEquals(400, pontuacaoTotal);
    }

    @Test
    public void testCriacaoPergunta() {
        List<String> alternativas = new ArrayList<>();
        alternativas.add("Brasília");
        alternativas.add("Rio de Janeiro");
        alternativas.add("São Paulo");
        alternativas.add("Salvador");

        Pergunta pergunta = new Pergunta("Qual é a capital?", "Brasília", alternativas);
        assertEquals("Brasília", pergunta.getRespostaCorreta());
        assertEquals(4, pergunta.getAlternativas().size());
    }
}

