package com.my.test_estonia;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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

                new RoadBuilderTask(MapsActivity.this, gMap).execute();
                btnWhere.setVisibility(View.VISIBLE);
            }
        });

        btnWhere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PlacesTask(MapsActivity.this, gMap).execute();
            }
        });

    }
}


