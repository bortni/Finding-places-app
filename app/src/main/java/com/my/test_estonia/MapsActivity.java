package com.my.test_estonia;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

public class MapsActivity extends Activity {
    private GoogleMap gMap;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        gMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        Button btnGo = (Button) findViewById(R.id.btnGo);
        final Button btnWhere = (Button) findViewById(R.id.btnWhere);

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected(MapsActivity.this)) {
                    new RoadBuilderTask(MapsActivity.this, gMap).execute();
                    btnWhere.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(MapsActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnWhere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkConnected(MapsActivity.this)) {
                    new PlacesTask(MapsActivity.this, gMap).execute();
                } else {
                    Toast.makeText(MapsActivity.this, getString(R.string.error_internet_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Method for NetworkInfo
     * Taking Activity context - @param context
     *
     * @param context
     * @return true - network is avialable
     */
    private boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) return false;
        else return true;
    }
}


