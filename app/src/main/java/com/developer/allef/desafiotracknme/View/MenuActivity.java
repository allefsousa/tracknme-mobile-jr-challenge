package com.developer.allef.desafiotracknme.View;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.allef.desafiotracknme.Helper.Preferencias;
import com.developer.allef.desafiotracknme.Model.locais;
import com.developer.allef.desafiotracknme.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@RuntimePermissions
public class MenuActivity extends AppCompatActivity {

        @BindColor(R.color.preto)
    int corPreta;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        MenuActivityPermissionsDispatcher.PermissoesWithCheck(MenuActivity.this); // verifica permissoes

        TextView mTextMessage = findViewById(R.id.message);
        mTextMessage.setTextColor(corPreta);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }


    //region verificando permissoes
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void Permissoes() {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MenuActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
    //endregion


    //region BottomNavigation
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {

                case R.id.navigation_abrirmapa:
                    startActivity(new Intent(MenuActivity.this,MapActivity.class));
                    return true;
            }
            return false;
        }
    };



    //endregion



}
