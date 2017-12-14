package com.developer.allef.desafiotracknme.Helper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by allef on 13/12/2017.
 */

public class Preferencias {

    private Context context; // variavel que recebera o contexto
    private SharedPreferences preferences;
    private final String NOME_ARQUIVO = "tracknme.preferencias"; // nome do arquivo que vai salvar as preferencias
    private final int MODE= 0; // quais aplicativos podem ler suas preferencias
    private SharedPreferences.Editor editor;  // variavel para editar as preferencias;

    private final String CHAVE_INTRO = "task";

    public void salvarDados( boolean vf){
        editor.putBoolean(CHAVE_INTRO,vf);
        editor.commit();
    }
    public boolean getexibir(){
       Boolean b =  preferences.getBoolean(CHAVE_INTRO,false );
        return b;
    }


    public Preferencias(Context contextParametro) {
        context = contextParametro;
        preferences = contextParametro.getSharedPreferences(NOME_ARQUIVO,MODE);
        editor = preferences.edit();
    }
}
