package com.developer.allef.desafiotracknme.InterfaceRequest;


import com.developer.allef.desafiotracknme.Model.locais;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by allef on 10/12/2017.
 */

public interface LocaisInterface {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://private-1bbb5d-tracknme1.apiary-mock.com")
            .addConverterFactory(GsonConverterFactory.create()).build();



    @GET("/posicoes")
    Call<List<locais>> buscalocais();

    @GET("/posicoes")
    Call<List<locais>>buscaData(@Query("dateTime") String data);




}
