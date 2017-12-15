package com.developer.allef.desafiotracknme.View;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.allef.desafiotracknme.Helper.Preferencias;
import com.developer.allef.desafiotracknme.InterfaceMVP.MapInterface;
import com.developer.allef.desafiotracknme.Model.locais;
import com.developer.allef.desafiotracknme.Presenter.MapPresenter;
import com.developer.allef.desafiotracknme.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, MapInterface.View {

    //region VariaveisGlobais
    GoogleMap map;
    List<locais> BancoAdapter;
    List<locais> auxiliar;
    locais ll;
    List<LatLng> latLngs;
    DatePicker datePicker;
    Switch pesquisaSwitch;
    Button abriFiltro;
    TextView tituloFiltro;
    Preferencias preferencias;
    boolean verifica;
    MapPresenter mapPresenter;
    FloatingActionButton fab;
    private View theAwesomeView;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map1);
        fragment.getMapAsync(this);

        //region InstanciaDeVariaveis
        mapPresenter = new MapPresenter(MapActivity.this);
        BancoAdapter = new ArrayList<>();
        ll = new locais();
        BancoAdapter = locais.listAll(locais.class); // recuperando dados do Banco e atribuindo os ao ArrayList
        auxiliar = new ArrayList<>();
        latLngs = new ArrayList<>();
        preferencias = new Preferencias(MapActivity.this);
        verifica = preferencias.getexibir();
        theAwesomeView = findViewById(R.id.the_awesome_view);
        abriFiltro = findViewById(R.id.the_wonderful_button);
        datePicker = findViewById(R.id.datePicker10);
        pesquisaSwitch = findViewById(R.id.switchtask);
        tituloFiltro = findViewById(R.id.filtroTitulo);
        //endregion


        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapPresenter.abrirFiltro(theAwesomeView, fab);
            }
        });

        //region Estado Switch
        /**
         * metodo responsavel por exibir ou nao os dados da view de filtro
         */
        pesquisaSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    datePicker.setVisibility(View.VISIBLE);
                    abriFiltro.setVisibility(View.VISIBLE);
                } else if (!b) {
                    preferencias.salvarDados(false);
                    datePicker.setVisibility(View.INVISIBLE);
                    abriFiltro.setVisibility(View.INVISIBLE);


                }


            }
        });
        //endregion

        /**
         * Metodo responsavel por abrir o filtro para pesquisar as datas
         */
        abriFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String dat = formatandoData(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                map.clear();
                mapPresenter.BuscaDadosData(dat, map, BancoAdapter);

                if (!verifica && !pesquisaSwitch.isChecked()) { // encontrou uma lista de pesquisas
                    mapPresenter.LimpaAll(BancoAdapter);
                }


            }
        });

        if (verifica) {
            pesquisaSwitch.setChecked(true);
        } else {
            pesquisaSwitch.setChecked(false);
        }


    }

    private String formatandoData(int year, int mesCorreto, int dayOfMonth) {
        int mes = mesCorreto;
        mes += 1;
        String data = year + "-" + mes + "-" + dayOfMonth;
        return data;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        /**
         * fazendo a atribuição do mapa a variavel Global
         * e atribuindo oque sera exibido no mapa
         */
        map = googleMap;
        map.getUiSettings().setAllGesturesEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        if (!verifica) {
            map.clear();
            mapPresenter.RequestAllLocais(BancoAdapter, map);
        } else {
            mapPresenter.DesenhaRotas(BancoAdapter, map);
        }


    }


    /**
     * tratando o click do Botão fisico do Celular
     */
    @Override
    public void onBackPressed() {
        if (theAwesomeView.isShown() && !pesquisaSwitch.isChecked()) {

            pesquisaSwitch.setChecked(false);
            mapPresenter.RequestAllLocais(BancoAdapter, map);
            map.clear();
            BancoAdapter = locais.buscaBD();
            mapPresenter.fechaFiltro(theAwesomeView, fab);


        } else if (theAwesomeView.isShown() && pesquisaSwitch.isChecked()) {

            if (BancoAdapter.size() == 0) {
                Toast.makeText(this, "Desabilite o Filtro para Voltar", Toast.LENGTH_LONG).show();
            } else {
                mapPresenter.fechaFiltro(theAwesomeView, fab);
            }
        }
        if (!theAwesomeView.isShown()) {
            finish();
            super.onBackPressed();
        }
        if (!verifica && !pesquisaSwitch.isChecked()) { // encontrou uma lista de pesquisas // botao de voltar para o mapa view
            locais.deleteAll(locais.class);
            BancoAdapter.clear();
        }


    }


    /**
     * tratando o click do botao de voltar da Toolbar
     *
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {

        if (theAwesomeView.isShown() && !pesquisaSwitch.isChecked()) {
            pesquisaSwitch.setChecked(false);
            //  RequestAllLocais();
            mapPresenter.RequestAllLocais(BancoAdapter, map);
            map.clear();
            BancoAdapter = locais.buscaBD();
            mapPresenter.fechaFiltro(theAwesomeView, fab);
        } else if (theAwesomeView.isShown() && pesquisaSwitch.isChecked()) {

            if (BancoAdapter.size() == 0) {
                Toast.makeText(this, "Desabilite o Filtro para Voltar", Toast.LENGTH_LONG).show();
            } else {
                mapPresenter.fechaFiltro(theAwesomeView, fab);
            }
        }
        if (!theAwesomeView.isShown()) {
            finish();
            return super.onSupportNavigateUp();
        }
        if (!verifica && !pesquisaSwitch.isChecked()) { // encontrou uma lista de pesquisas // botao de voltar para o mapa view

            locais.deleteAll(locais.class);
            BancoAdapter.clear();


        }
        return false;
    }

}
