package com.example.restauranttec;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapActivity {
/*
    private int requestAccess;
    private GoogleMap mMap = null;
    private Location locationPrevious;
    private float distance = 0;
    private Chronometer cronometro;
    private ArrayList<Double> coordenadas;
    private TextView txtDist;
    private int OK_RESULT_CODE = 1;
    private boolean nuevaCarrera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        cronometro = findViewById(R.id.crono);
        txtDist = findViewById(R.id.txtMDistancia);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Bundle parametros = this.getIntent().getExtras();
        if(parametros !=null){
            Button btn = (Button) findViewById(R.id.btnSalir);
            btn.setText("Salir");
            nuevaCarrera = false;
            txtDist.setText(parametros.getString("Distancia"));
            cronometro.setText(parametros.getString("Duracion"));
            double[] coord = parametros.getDoubleArray("Coordenadas");
            if (coord.length > 20) {
                LatLng lPrevious = new LatLng(coord[0], coord[1]);
                mMap.addMarker(new MarkerOptions().position(lPrevious).title("Inicio Recorrido"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lPrevious, 18.0f));
                for (int i = 2; i < (coord.length - 1); i += 2) {
                    LatLng lActual = new LatLng(coord[i], coord[i + 1]);
                    mMap.addPolyline(new PolylineOptions().add(lPrevious, lActual).width(10).color(Color.RED));
                    lPrevious = new LatLng(coord[i], coord[i + 1]);
                }
            }
        }
        else {
            nuevaCarrera = true;
            cronometro.setBase(SystemClock.elapsedRealtime());
            cronometro.start();
            coordenadas = new ArrayList<Double>();
        }
        if(nuevaCarrera)
            getLocation();
    }

    public void stop(View view){
        sendValues();
        finish();
    }

    private void sendValues(){
        if(nuevaCarrera) {
            Intent data = new Intent();
            double coordenadasSend[] = new double[coordenadas.size()];
            for (int i = 0; i < coordenadas.size(); i++) {
                coordenadasSend[i] = coordenadas.get(i);
            }

            data.putExtra("Coordenadas", coordenadasSend);
            data.putExtra("Duracion", cronometro.getText());
            data.putExtra("Distancia", txtDist.getText());
            setResult(OK_RESULT_CODE, data);
        }
    }
    private void getLocation(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, requestAccess);
        }
        else{

            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            antut(location);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1500,5,locationListe);
        }
    }

    private void antut(Location location){
        if(location != null) {

            LatLng lActual = new LatLng(location.getLatitude(), location.getLongitude());
            if(locationPrevious == null){
                mMap.addMarker(new MarkerOptions().position(lActual).title("Inicio Recorrido"));
                locationPrevious = location;
            }
            coordenadas.add(location.getLatitude());
            coordenadas.add(location.getLongitude());
            LatLng lPrevious = new LatLng(locationPrevious.getLatitude(), locationPrevious.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lActual,18.0f));
            double dist = Math.round(location.distanceTo(locationPrevious) * 100d) / 100d;
            distance += dist;
            txtDist.setText(Math.round(distance)+" mts");

            mMap.addPolyline(new PolylineOptions().add(lPrevious,lActual).width(10).color(Color.RED));
            locationPrevious = location;

        }
    }

    LocationListener locationListe = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            antut(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestAccess == requestCode) {
            if(grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                getLocation();
            }
        } else {
            Toast.makeText(this,"Permiso denegado",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    public void onBackPressed() {
        sendValues();
        super.onBackPressed();
    }*/
}
