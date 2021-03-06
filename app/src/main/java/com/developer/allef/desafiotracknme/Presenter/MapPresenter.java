package com.developer.allef.desafiotracknme.Presenter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
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
import com.developer.allef.desafiotracknme.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.jaouan.revealator.Revealator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by allef on 14/12/2017.
 */

public class MapPresenter implements MapInterface.Presenter {

    //region variaveis Globais
    private Info distancia, duracao;
    private Leg leg;
    private double dis = 0;
    private String[] colors = {"#d50000", "#7f31c7c5", "#7fff8a00"};
    private MapInterface.View view;
    private List<locais> cc;
    private Activity activity;
    private Preferencias preferencias;
    private FloatingActionButton fabFiltro;
    private View viewFiltro;
    boolean conexao;
    //endregion


    public MapPresenter(Activity v) {
        this.activity = v;
        fabFiltro = activity.findViewById(R.id.fab);
        viewFiltro = activity.findViewById(R.id.the_awesome_view);
        preferencias = new Preferencias(v);

    }

    /**
     * TASK 1
     * metodo responsavel por fazer a busca doaslocais na Api
     *
     * @param aaa
     * @param googleMap
     */
    public void RequestAllLocais(final List<locais> aaa, final GoogleMap googleMap) {
        cc = new ArrayList<>();
        aaa.clear();
        locais.limpaBD();
        conexao = verificaconexao();
        if(conexao){



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
                if (!response.body().isEmpty()) {

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

                            List<locais> l = locais.buscaBD(); // fazendo a busca no banco de dados
                            DesenhaRotas(l, googleMap); // desenhando as rotas logo em seguida
                        }


                    }
                }


            }


            @Override
            public void onFailure(Call<List<locais>> call, Throwable t) {
                Toast.makeText(activity, "Falha ao Buscar os dados", Toast.LENGTH_SHORT).show();

            }
        });
        }else {
            List<locais> l = locais.buscaBD(); // fazendo a busca no banco de dados
            DesenhaRotas(l, googleMap); // desenhando as rotas logo em seguida
        }



    }

    /**
     * metodo responsavel por exibir as rotas no mapa.
     *
     * @param BancoAdapter
     * @param map
     */
    @Override
    public void DesenhaRotas(List<locais> BancoAdapter, final GoogleMap map) {
        LatLng latLngPrimeira = null;
        int j = 1;

        if (!BancoAdapter.isEmpty()) {


            for (int i = 0; i < BancoAdapter.size(); i++, j++) {
                if (j != BancoAdapter.size()) { // FIM DA LISTA de dados

                    latLngPrimeira = new LatLng(BancoAdapter.get(0).getLatitude(), BancoAdapter.get(0).getLongitude());// recuperando a primeira posição da lista
                    LatLng mOrigem = new LatLng(BancoAdapter.get(i).getLatitude(), BancoAdapter.get(i).getLongitude()); // recuperando novamente a primeira para exibila
                    LatLng mDestino = new LatLng(BancoAdapter.get(j).getLatitude(), BancoAdapter.get(j).getLongitude());//neste caso sempre sera exibido uma posicao a frente pra desenhar a rota
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngPrimeira, 8.0f));


                    /**
                     * O primeiro parametro é a chave da Google para fazer a requisição da rota
                     * em seguida é passada o ponto de origem, o ponto de destino a uniodade de medida
                     * o idioma o metodo que vai percorrer o percurso (Carro,andando,onibus,metro)
                     * caso queira e possua rotas alternativas essas podem ser exebidas
                     * e por fim a chamada de callback
                     *
                     */
                    GoogleDirection.withServerKey("AIzaSyAVp5CMECXAzGyrVP0A8p-7gZI_EVDNbfA")
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
                                            map.addPolyline(DirectionConverter.createPolyline(activity, directionPositionList, 5, Color.parseColor(color)));
                                        }


                                    }
                                }


                                @Override
                                public void onDirectionFailure(Throwable t) {

                                    Toast.makeText(activity, "Falha a Exibir rotas", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }


        }

    }

    /**
     * metodo responsavel por calcular a quilometragem percorrida pela rota
     *
     * @param leg
     * @return
     */
    @Override
    public double calculaPercurso(Leg leg) {
        distancia = leg.getDistance();
        String div = distancia.getText();
        String[] kmroute = div.split("[ ]"); // quebrando a Stringno espaço que exibia ex:[200 km]
        double d = Double.parseDouble(kmroute[0].replaceAll(",", "."));
        dis = dis + d;
        return dis;
    }


    /**
     * TASK 2
     * metodo responsavel por realizar uma busca Por data na Api
     *
     * @param data
     * @param googleMap
     * @param BancoAdapter
     */
    @Override
    public void BuscaDadosData(final String data, final GoogleMap googleMap, final List<locais> BancoAdapter) {

        boolean verifica = preferencias.getexibir();

        if (!verifica) { // Caso seja falso a busca esta sendo feita por data
            // agora se for verdadeiro a busca esta sendo feita normal.
            googleMap.clear();
            LimpaAll(BancoAdapter);
            preferencias.salvarDados(false);
        }
        conexao = verificaconexao();
        if(conexao){

        preferencias.salvarDados(true);

        // fazendo a busca dos dados na api
        LocaisInterface anInterface = LocaisInterface.retrofit.create(LocaisInterface.class);

        Call<List<locais>> request = anInterface.buscaData(data);
        request.enqueue(new Callback<List<locais>>() {
            @Override
            public void onResponse(Call<List<locais>> call, Response<List<locais>> response) {
                /**
                 * fazendo o tratamento dos dados pois nao foi possivel
                 * receber somente a data passada da api.
                 * foi feito um tratamento para que so a data pesquisada seja exibida
                 * e exibir as rotas daquela data . buscando no servidor , salvando local, e as listando
                 */
                if (response.isSuccessful()) {

                    List<locais> dadosLocais = locais.buscaBD(); // fazendo o select no banco de dados
                    if (!response.body().isEmpty()) {

                        for (locais a : response.body()) {

                            boolean flag = false;
                            locais l = null;
                            String quebraData = a.getData();
                            String[] dat = quebraData.split("T");
                            String dataCorreta = dat[0];


                            if (dadosLocais.size() == 0) {
                                if (dataCorreta.equals(data)) {
                                    a.save();
                                } else {
                                    Toast.makeText(activity, "Não existem dados para ser exibidos na data pesquisada !!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            for (locais bb : dadosLocais) {

                                if (a.equals(bb)) {
                                    flag = true;
                                }
                                l = bb;


                            }
                            // foi utilizado um flag para salvar a vaga
                            if (l != null) {

                                if (!flag && dataCorreta.equals(data)) {
                                    a.save();
                                }
                            }

                        }


                        List<locais> l = locais.buscaBD(); // fazendo a busca no Bd
                        DesenhaRotas(l, googleMap); // desenhando as rotas
                        fechaFiltro(viewFiltro, fabFiltro); // fechando o filtro

                    }

                }
            }


            @Override
            public void onFailure(Call<List<locais>> call, Throwable t) {
                Toast.makeText(activity, "Impossivel Exibir locais!!", Toast.LENGTH_SHORT).show();
            }
        });
        }else {
            List<locais> l = locais.buscaBD(); // fazendo a busca no Bd
            DesenhaRotas(l, googleMap); // desenhando as rotas
            fechaFiltro(viewFiltro, fabFiltro); // fechando o filtro
        }

    }

    /**
     * metodo responsavel por fechar o filtro que sobre poe a view
     *
     * @param v
     * @param floatbu
     */
    @Override
    public void fechaFiltro(View v, FloatingActionButton floatbu) {
        Revealator.unreveal(v)
                .to(floatbu)
                .withCurvedTranslation().start();
        floatbu.show();

    }

    /**
     * metodo responsavel por limpar o adapter do banco que no
     * caso é uma lista de locais
     * e por fazer um DELETE no banco utilizando Sugar Orm
     *
     * @param locaisList
     */
    @Override
    public void LimpaAll(List<locais> locaisList) {
        locais.deleteAll(locais.class); // Apagando os dados do banco
        locaisList.clear(); // limpando o adapterdo banco

    }

    /**
     * metodo responsavel por exibir o filtro sobre a view
     *
     * @param theAwesomeView
     * @param fab
     */
    @Override
    public void abrirFiltro(View theAwesomeView, FloatingActionButton fab) {
        Revealator.reveal(theAwesomeView)
                .from(fab)
                .withCurvedTranslation()
                .withChildsAnimation()
                .start();
        fab.hide();

    }


    public boolean verificaconexao(){
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            conectado = true;
        } else {
            conectado = false;
        }
        return conectado;
    }


}
