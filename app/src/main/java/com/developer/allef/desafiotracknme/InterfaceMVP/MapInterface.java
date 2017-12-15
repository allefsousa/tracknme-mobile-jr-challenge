package com.developer.allef.desafiotracknme.InterfaceMVP;

import android.app.ProgressDialog;
import android.support.design.widget.FloatingActionButton;

import com.akexorcist.googledirection.model.Leg;
import com.developer.allef.desafiotracknme.Model.locais;
import com.google.android.gms.maps.GoogleMap;

import java.util.List;

/**
 * Created by allef on 14/12/2017.
 */

public interface MapInterface {

    interface View {

    }


    interface Presenter {
        void RequestAllLocais(List<locais> aaa, GoogleMap googleMap);

        void DesenhaRotas(List<locais> BancoAdapter, GoogleMap map);

        double calculaPercurso(Leg leg);

        void BuscaDadosData(final String data, GoogleMap googleMap, List<locais> BancoAdapter);

        void fechaFiltro(android.view.View v, FloatingActionButton floatbu);

        void LimpaAll(List<locais> locaisList);


        void abrirFiltro(android.view.View theAwesomeView, FloatingActionButton fab);
    }


}
