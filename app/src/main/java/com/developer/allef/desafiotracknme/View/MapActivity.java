package com.developer.allef.desafiotracknme.View;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.Language;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.constant.Unit;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Info;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.developer.allef.desafiotracknme.Helper.Preferencias;
import com.developer.allef.desafiotracknme.InterfaceMVP.MapInterface;
import com.developer.allef.desafiotracknme.InterfaceRequest.LocaisInterface;
import com.developer.allef.desafiotracknme.Model.locais;
import com.developer.allef.desafiotracknme.Presenter.MapPresenter;
import com.developer.allef.desafiotracknme.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.jaouan.revealator.Revealator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, MapInterface.View {

    //region VariaveisGlobais
    GoogleMap map;
    List<locais> BancoAdapter;
    List<locais> auxiliar;
    locais ll;
    private LatLng l;
    private LatLng d;
    List<LatLng> latLngs;
    Info distancia, duracao;
    Leg leg;
    double dis = 0;
    String[] colors = {"#d50000", "#7f31c7c5", "#7fff8a00"};
    DatePicker datePicker;
    ProgressDialog progressDoalog;
    Switch pesquisaSwitch;
    Button theWonderfulButton;
    TextView tituloFiltro;
    Preferencias preferencias;
    boolean verifica;
    MapPresenter mapPresenter;
    FloatingActionButton fab;
    private View theAwesomeView;
    @BindString(R.string.SERVERKEY)
    String serverkey;
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
        ButterKnife.bind(this); // adiconando referencia do annotation Processor a classe


        //region IntanciaDeVariaveis
        mapPresenter = new MapPresenter(this);
        BancoAdapter = new ArrayList<>();
        ll = new locais();
        BancoAdapter = locais.listAll(locais.class); // recuperando dados do Banco e atribuindo os ao ArrayList
        auxiliar = new ArrayList<>();
        latLngs = new ArrayList<>();
        preferencias = new Preferencias(MapActivity.this);
        verifica = preferencias.getexibir();
        theAwesomeView = findViewById(R.id.the_awesome_view);
        theWonderfulButton = findViewById(R.id.the_wonderful_button);
        datePicker = findViewById(R.id.datePicker10);
        pesquisaSwitch = findViewById(R.id.switchtask);
        tituloFiltro = findViewById(R.id.filtroTitulo);
        //endregion

        //region Task2
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Revealator.reveal(theAwesomeView)
                        .from(fab)
                        .withCurvedTranslation()
                        .withChildsAnimation()
                        .start();
                fab.hide();
                auxiliar = BancoAdapter;
            }
        });

        pesquisaSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {

                    preferencias.salvarDados(true);
                    datePicker.setVisibility(View.VISIBLE);
                    theWonderfulButton.setVisibility(View.VISIBLE);


                } else if (!b) {

                    preferencias.salvarDados(false);
                    datePicker.setVisibility(View.INVISIBLE);
                    theWonderfulButton.setVisibility(View.INVISIBLE);


                }


            }
        });

        theWonderfulButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = TratandoData();
                BuscaDadosData(data);
                ProgressDialogRequest();

                if (verifica && pesquisaSwitch.isChecked()) { // encontrou uma lista de pesquisas

                } else { // encontou uma lista comum apaga
                    locais.deleteAll(locais.class);
                    BancoAdapter.clear();
                }
                preferencias.salvarDados(true);


            }
        });

        if (verifica) {
            pesquisaSwitch.setChecked(true);
        } else {
            pesquisaSwitch.setChecked(false);


        }
        //endregion


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

        /**
         * Buscando Todos os locais quando o mapa é iniciado;
         */
        if (!pesquisaSwitch.isChecked()) {
            RequestAllLocais();
        }
        /**
         * chamando o metodo para exibir as rotas no mapa
         */
        DesenhaRotas();


    }

    /**
     * Fazendo a requisição dos dados no servidor
     */
    private void RequestAllLocais() {
        BancoAdapter.clear();
        locais.limpaBD();
        map.clear();

        // Instancia da interface  que possui as Rotas e a URLBase do Serviço
        LocaisInterface locaisInterface = LocaisInterface.retrofit.create(LocaisInterface.class);

        //Executando a chamada Assincrona do metodo para buscar todos os Locais
        Call<List<locais>> lo = locaisInterface.buscalocais();
        lo.enqueue(new Callback<List<locais>>() {

            /**
             * Obtendo a resposta da chamada
             * @param call
             * @param response
             */
            @Override
            public void onResponse(Call<List<locais>> call, Response<List<locais>> response) {

                /**
                 *Caso a requisição tenha sido executada com sucesso
                 * e feita a verificação dos dados para impedir que adicione dados repetidos
                 * ao banco de dados local
                 */
                if (response.isSuccessful()) {

                    List<locais> lisd = locais.buscaBD(); // Buscando os dados no bd e os atribuindo a uma lista de dados ()
                                                          // A consulta nessa etapa seria(Select * from locais)
                    for (locais a : response.body()) {
                        boolean flag = false;

                        /**
                         *  caso a lista seja vazia salvo todos os dados
                         */
                        if (lisd.size() == 0) {
                            a.save();
                        }
                        for (locais bb : lisd) {

                            /**
                             * faço a comparacao do Objeto recebido com todo o
                             *  banco local
                             */
                            if (a.equals(bb)) {
                                flag = true;
                            }

                        }

                        /**
                         * se flag nao mudou de valor nao existe valor igual
                         */
                        if (!flag) {
                            a.save();
                        }

                    }

                    BancoAdapter = locais.buscaBD();// faço o select no banco local.
                    fechaFiltro();

                }

            }


            @Override
            public void onFailure(Call<List<locais>> call, Throwable t) {
                Toast.makeText(MapActivity.this, "Falha ao Buscar os dados", Toast.LENGTH_SHORT).show();

            }
        });
    }


    /**
     * Metodo responsavel por desenhar as rotas obtidas do banco local
     * para tracar as rotas em Android nativo foi utlizado uma api que faz a busca das cordenadas
     * junto ao google maps e facilita as requisições
     * A documantação da api pode ser consulta aqui :http://www.akexorcist.com/2015/12/google-direction-library-for-android-en.html
     */
    public void DesenhaRotas() {
        LatLng latLngPrimeira = null;
        int j = 1;

        if (!BancoAdapter.isEmpty()) {


            for (int i = 0; i < BancoAdapter.size(); i++, j++) {
                if (j != BancoAdapter.size()) { // FIM DA LISTA de dados

                    latLngPrimeira = new LatLng(BancoAdapter.get(0).getLatitude(), BancoAdapter.get(0).getLongitude());// recuperando a primeira posição da lista
                    LatLng mOrigem = new LatLng(BancoAdapter.get(i).getLatitude(), BancoAdapter.get(i).getLongitude()); // recuperando novamente a primeira para exibila
                    LatLng mDestino = new LatLng(BancoAdapter.get(j).getLatitude(), BancoAdapter.get(j).getLongitude());//neste caso sempre sera exibido uma posicao a frente pra desenhar a rota
                    latLngs.add(mOrigem); // todas as localizações dao adicionadas em uma lista

                    /**
                     * O primeiro parametro é a chave da Google para fazer a requisição da rota
                     * em seguida é passada o ponto de origem, o ponto de destino a uniodade de medida
                     * o idioma o metodo que vai percorrer o percurso (Carro,andando,onibus,metro)
                     * caso queira e possua rotas alternativas essas podem ser exebidas
                     * e por fim a chamada de callback
                     *
                     */
                    GoogleDirection.withServerKey(serverkey)
                            .from(mOrigem)
                            .to(mDestino)
                            .unit(Unit.METRIC)
                            .language(Language.PORTUGUESE_BRAZIL)
                            .transportMode(TransportMode.DRIVING)
                            .alternativeRoute(false)
                            .execute(new DirectionCallback() {
                                @Override
                                public void onDirectionSuccess(Direction direction, String rawBody) {
                                    

                                    if (direction.isOK()) {// caso a requisição tenha sido completa
                                        double quilometragem = 0;

                                        for (Route a : direction.getRouteList()) {
                                            a.getLegList().get(0).getDirectionPoint();
                                        }

                                        /**
                                         * pegando a lista de direções iterando e as exibindo no mapa.
                                         * recuperando a distancia e o tempo do percurso
                                         */
                                        for (int i = 0; i < direction.getRouteList().size(); i++) {
                                            Route route = direction.getRouteList().get(i);
                                            String color = colors[i * colors.length]; // cor da rota no mapa
                                            ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                                            leg = route.getLegList().get(0);
                                             quilometragem = calculaPercurso(leg);
                                            duracao = leg.getDuration();// tempo do percurso
                                            map.addPolyline(DirectionConverter.createPolyline(MapActivity.this, directionPositionList, 5, Color.parseColor(color)));
                                        }
                                        Log.d("KM", "onDirectionSuccess: "+quilometragem);


                                    }
                                }


                                @Override
                                public void onDirectionFailure(Throwable t) {
                                    Snackbar.make(findViewById(android.R.id.content), "Impossivel Exibir Rotas, Verifique sua Conexão com a Internet. " + t.getMessage(),
                                            Snackbar.LENGTH_LONG).show();
                                    Log.d("Falha", "onDirectionFailure: " + t.getMessage());

                                }
                            });
                }
            }


            if (latLngPrimeira != null) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngPrimeira, 8.0f));
              //  map.addMarker(new MarkerOptions().position(lng).title("olha noias aqui").snippet("Ponto de PARTIDA"));
            }



        }
    }

    private double calculaPercurso(Leg leg) {
        distancia = leg.getDistance();
        String div = distancia.getText();
        String[] kmroute = div.split("[ ]"); // quebrando a Stringno espaço que exibia ex:[200 km]
        double d = Double.parseDouble(kmroute[0].replaceAll(",", "."));
        dis = dis + d;
        return dis;

    }


    //region Tarefa 2
    private void ProgressDialogRequest() {
        progressDoalog = new ProgressDialog(MapActivity.this);
        progressDoalog.setIndeterminate(true);
        progressDoalog.setMessage("Aguarde .....");
        progressDoalog.setTitle("Buscando Dados Atualizados");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
    }

    /**
     * Como a data obtida da API
     * @return
     */
    @NonNull
    private String TratandoData() {
        int mesCorreto = datePicker.getMonth();
        mesCorreto += 1;
        String data = datePicker.getYear() + "-" + mesCorreto + "-" + datePicker.getDayOfMonth();
        Log.d("Allef", "onCreate: " + data);
        return data;
    }

    private void BuscaDadosData(final String data) {
        map.clear();

        LocaisInterface anInterface = LocaisInterface.retrofit.create(LocaisInterface.class);

        Call<List<locais>> request = anInterface.buscaData(data);
        request.enqueue(new Callback<List<locais>>() {
            @Override
            public void onResponse(Call<List<locais>> call, Response<List<locais>> response) {

                if (response.isSuccessful()) {

                    List<locais> dadosLocais = locais.listAll(locais.class);
                    for (locais a : response.body()) {

                        boolean flag = false;
                        locais l = null;
                        Log.d("data", "onResponse: " + a.getData());
                        String quebraData = a.getData();
                        String[] dat = quebraData.split("T");
                        String dataCorreta = dat[0];


                        if (dadosLocais.size() == 0) {
                            if (dataCorreta.equals(data)) {
                                a.save();
                            }
                        }

                        for (locais bb : dadosLocais) {

                            if (a.equals(bb)) {
                                flag = true;
                            }
                            l = bb;


                        }
                        if (l != null) {

                            if (!flag && dataCorreta.equals(data)) {
                                a.save();
                            }
                        }

                    }

                    BancoAdapter = locais.listAll(locais.class);
                    Log.d("tamanho meu bem", "onResponse: " + BancoAdapter.size());
                    progressDoalog.cancel();
                    fechaFiltro();
                }


            }


            @Override
            public void onFailure(Call<List<locais>> call, Throwable t) {
                Log.d("Allef", "result" + t.getMessage());
            }
        });

    }


    private void fechaFiltro() {
        Revealator.unreveal(theAwesomeView)
                .to(fab)
                .withCurvedTranslation().start();
        fab.show();
        DesenhaRotas();

    }
    //endregion

    /**
     * tratando o click do Botão fisico do Celular
     */
    @Override
    public void onBackPressed() {
        Log.d("BancoAdapter", "onSupportNavigateUp: " + BancoAdapter.size());
        if (theAwesomeView.isShown() && !pesquisaSwitch.isChecked()) {
            pesquisaSwitch.setChecked(false);
            RequestAllLocais();
        } else if (theAwesomeView.isShown() && pesquisaSwitch.isChecked()) {

            if (BancoAdapter.size() == 0) {
                Toast.makeText(this, "Desabilite o Filtro para Voltar", Toast.LENGTH_LONG).show();
            } else {
                fechaFiltro();
            }
        }
        if (!theAwesomeView.isShown()) {
            finish();
            super.onBackPressed();
        }
        if (!verifica && !pesquisaSwitch.isChecked()) { // encontrou uma lista de pesquisas // botao de voltar para o mapa view
            /* se for */
            locais.deleteAll(locais.class);
            BancoAdapter.clear(); // TODO: 14/12/2017  continua apagando os dados via requisição por partes verificar o pq de nao deixar  exibir outros pontos no mapa
        }


    }


    /**
     * tratando o click do botao de voltar da Toolbar
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        Log.d("BancoAdapter", "onSupportNavigateUp: " + BancoAdapter.size());
        if (theAwesomeView.isShown() && !pesquisaSwitch.isChecked()) {
            pesquisaSwitch.setChecked(false);
            RequestAllLocais();
        } else if (theAwesomeView.isShown() && pesquisaSwitch.isChecked()) {

            if (BancoAdapter.size() == 0) {
                Toast.makeText(this, "Desabilite o Filtro para Voltar", Toast.LENGTH_LONG).show();
            } else {
                fechaFiltro();
            }
        }
        if (!theAwesomeView.isShown()) {
            finish();
            return super.onSupportNavigateUp();
        }
        if (!verifica && !pesquisaSwitch.isChecked()) { // encontrou uma lista de pesquisas // botao de voltar para o mapa view
            /* se for */
            locais.deleteAll(locais.class);
            BancoAdapter.clear(); // TODO: 14/12/2017  continua apagando os dados via requisição por partes verificar o pq de nao deixar  exibir outros pontos no mapa
        }

        return false;
    }
}
