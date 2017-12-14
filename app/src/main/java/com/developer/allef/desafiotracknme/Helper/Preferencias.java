package com.developer.allef.desafiotracknme.Helper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by allef on 14/12/2017.
 * classe de preferencia respsonsavel por
 * persistir os dados localmente e facilitar o armazenamento de informações pequenas
 * SharedPrefereces trabalha com armazenamento Chave e valor
 */

public class Preferencias {

    private Context context; // variavel que recebera o contexto
    private SharedPreferences preferences;
    private final String NOME_ARQUIVO = "tracknme.preferencias"; // nome do arquivo que vai salvar as preferencias
    private final int MODE= 0; // quais aplicativos podem ler suas preferencias
    private SharedPreferences.Editor editor;  // variavel para editar as preferencias;

    private final String CHAVE_INTRO = "task"; // chave

    // metodo responsavel por salvar um valor Booleano
    // a chave ja é passada para nao haver erro
    public void salvarDados( boolean vf){
        editor.putBoolean(CHAVE_INTRO,vf);
        editor.commit();
    }

    // Metodo faz a busca do dado tambem pasando a chave ja definida
    public boolean getexibir(){
       Boolean b =  preferences.getBoolean(CHAVE_INTRO,false );
        return b;
    }

    /**
     * O construtor da classe de preferencia  que recebe um Context como parametro.
     * @param contextParametro
     */
    public Preferencias(Context contextParametro) {
        context = contextParametro;
        preferences = contextParametro.getSharedPreferences(NOME_ARQUIVO,MODE);
        editor = preferences.edit();
    }
}
