package com.example.triviamaster;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


// Escuta o evento emitido pelo Service e avisa o usuário com um Toast

public class AvisoReceiver extends BroadcastReceiver {

    // Nome da ação que identifica este aviso no sistema
    public static final String ACAO_SINCRONIZACAO_FINALIZADA = "com.example.triviamaster.SINCRONIZACAO_FINALIZADA";

    @Override
    public void onReceive(Context contexto, Intent dadosRecebidos) {
        if (dadosRecebidos != null && ACAO_SINCRONIZACAO_FINALIZADA.equals(dadosRecebidos.getAction())) {
            int quantidade = dadosRecebidos.getIntExtra("quantidade", 0);
            if (quantidade > 0) {
                Toast.makeText(contexto, "Sincronização Concluída! " + quantidade + " questões salvas no banco.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(contexto, "Não foi possível conectar à API. Verifique sua internet.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

