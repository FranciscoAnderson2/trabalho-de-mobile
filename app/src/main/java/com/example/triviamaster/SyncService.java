package com.example.triviamaster;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.IBinder;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//  Executa o download de perguntas em segundo plano sem travar a tela

public class SyncService extends Service {

    private ExecutorService executorSegundoPlano;

    @Override
    public void onCreate() {
        super.onCreate();
        executorSegundoPlano = Executors.newSingleThreadExecutor();
    }

    @Override
    public int onStartCommand(Intent intencao, int flags, int idInicio) {
        // Executa o download em segundo plano sem travar o aplicativo
        executorSegundoPlano.execute(new Runnable() {
            @Override
            public void run() {
                int totalBaixado = 0;
                try {
                    // Busca 10 questões da API pela internet
                    List<Pergunta> perguntas = ApiClient.buscarPerguntas(10, "", "");

                    for (Pergunta pergunta : perguntas) {
                        ContentValues dadosParaSalvar = new ContentValues();
                        dadosParaSalvar.put("pergunta", pergunta.getTexto());
                        dadosParaSalvar.put("correta", pergunta.getRespostaCorreta());

                        // Pega as opções que não são a correta
                        int contadorIncorretas = 1;
                        for (String alternativa : pergunta.getAlternativas()) {
                            if (!alternativa.equals(pergunta.getRespostaCorreta()) && contadorIncorretas <= 3) {
                                dadosParaSalvar.put("incorreta" + contadorIncorretas, alternativa);
                                contadorIncorretas++;
                            }
                        }

                        // Salva no banco através do Content Provider
                        getContentResolver().insert(TriviaProvider.URI_QUESTOES, dadosParaSalvar);
                    }
                    totalBaixado = perguntas.size();
                } catch (Exception erro) {
                    erro.printStackTrace();
                }

                // Dispara o Broadcast avisando que o Service terminou
                Intent avisoParaEnviar = new Intent(AvisoReceiver.ACAO_SINCRONIZACAO_FINALIZADA);
                avisoParaEnviar.setPackage(getPackageName());
                avisoParaEnviar.putExtra("quantidade", totalBaixado);
                sendBroadcast(avisoParaEnviar);

                // Encerra o serviço após concluir a tarefa
                stopSelf();
            }
        });

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intencao) {
        return null; // Não é um Bound Service
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorSegundoPlano != null) {
            executorSegundoPlano.shutdown();
        }
    }
}

