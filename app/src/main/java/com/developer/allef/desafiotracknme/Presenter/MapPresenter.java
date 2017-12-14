package com.developer.allef.desafiotracknme.Presenter;

import android.view.View;

import com.developer.allef.desafiotracknme.InterfaceMVP.MapInterface;
import com.google.android.gms.maps.GoogleMap;

/**
 * Created by allef on 14/12/2017.
 */

public class MapPresenter implements MapInterface.Presenter {

    private MapInterface.View view;

    public MapPresenter(MapInterface.View v) {
        this.view = v;
    }
}
