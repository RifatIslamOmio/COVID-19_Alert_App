package com.example.covid_19alertapp.activities;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.covid_19alertapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationShowMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double latitude, longitude;

    // intent value keys
    private static final String LATITUDE_KEY = "maps-latitude";
    private static final String LONGITUDE_KEY = "maps-longitude";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // fetch latitude, longitude
        fetchLatLng();

        setContentView(R.layout.activity_location_show_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void fetchLatLng() {

        this.latitude = getIntent().getDoubleExtra(LATITUDE_KEY, 23.8103);
        this.longitude = getIntent().getDoubleExtra(LONGITUDE_KEY, 90.4125);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Infected Position and move the camera
        LatLng infectedPosition = new LatLng(this.latitude, this.longitude);
        mMap.addMarker(new MarkerOptions().position(infectedPosition).title("infected point"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(infectedPosition, 18.0f));
    }
}
