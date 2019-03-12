package com.prince.assetManagement;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AbstractMapActivity implements OnMapReadyCallback {
    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String latlong = getIntent().getStringExtra("latlong");
        String[] lat_long = latlong.split(",");
        latitude = Double.parseDouble(lat_long[0]);
        longitude = Double.parseDouble(lat_long[1]);

//        latitude = 32.725401600000005;
//        longitude = 74.858496;


        if (readyToGo()) {
            setContentView(R.layout.activity_map);

            SupportMapFragment mapFrag =
                    (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            if (savedInstanceState == null) {
                mapFrag.getMapAsync(this);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
//        CameraUpdate center=
//                CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
//        CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);
//        map.addMarker(new MarkerOptions()
//                .position(new LatLng(latitude, longitude))
//                .title("Hello world"));
//
//        map.moveCamera(center);
//        map.animateCamera(zoom);
        LatLng placeLocation = new LatLng(latitude, longitude); //Make them global
        Marker placeMarker = map.addMarker(new MarkerOptions().position(placeLocation)
                .title("hello world"));
        map.moveCamera(CameraUpdateFactory.newLatLng(placeLocation));
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 1000, null);
    }
}

