package com.developer.allef.desafiotracknme.Model;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

import java.util.List;


/**
 * Created by allef on 14/12/2017.
 *
 * Classe extendendo a SugarRecord para que o Sugar Orm
 * mapeie a classe como uma entidade do banco de dados
 */

public class locais extends SugarRecord {


    /**
     * atributos do Objeto que sera buscado
     */
    @SerializedName("dateTime")
    String data;
    @SerializedName("latitude")
    double latitude;
    @SerializedName("longitude")
    private double longitude;


    public locais() {
    }

    public locais(String data, double latitude, double longitude) {
        this.data = data;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    /**
     * sobreescrevendo  o metodo Equals para fazer a verificação de objetos
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (!(o instanceof locais)){
            return false;
        }

        locais locais = (locais) o;

        if (Double.compare(locais.latitude, latitude) != 0){
            return false;
        }
        if (Double.compare(locais.longitude, longitude) != 0){
            return false;
        }
        return data != null ? data.equals(locais.data) : locais.data == null;
    }

    /**
     * Metodos staticos que fazem referencias as Operações
     * Utilizando Sugar Object Relational Mapper
     */
    // DELETE
    public static void limpaBD(){
        locais.deleteAll(locais.class);
    }
    // SELECT
    public static List<locais> buscaBD(){

        return  locais.listAll(locais.class);
    }


}



