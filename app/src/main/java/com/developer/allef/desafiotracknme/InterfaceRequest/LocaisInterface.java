package com.developer.allef.desafiotracknme.InterfaceRequest;


import com.developer.allef.desafiotracknme.Model.locais;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by allef on 14/12/2017.
 * Interface com as rotas a serem buscadas
 * e a URL Base para requisições
 *
 */

public interface LocaisInterface {
    // URL
    String uriBase = "http://private-1bbb5d-tracknme1.apiary-mock.com";

    // Instancia e configuração da Biblioteca Retrofit
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(uriBase)
            .addConverterFactory(GsonConverterFactory.create()).build();


    // Definição das rotas e dos parametos a serem buscados na requisição
    @GET("/posicoes")
    Call<List<locais>> buscalocais();

    @GET("/posicoes")
    Call<List<locais>> buscaData(@Query("dateTime") String data);


}
