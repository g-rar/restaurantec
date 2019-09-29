package com.example.restaurantec;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class RestaurantAddActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Spinner spinner;
    private GoogleMap mMap = null;
    private Location locationPrevious;
    private int OK_RESULT_CODE = 1;
    private int requestAccess;
    private ScrollView svRestoDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        spinner = (Spinner) findViewById(R.id.spinner);
        String[] letra = {"Rápida","Mexicana","Casera","D","E"};
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra));
        svRestoDetail = findViewById(R.id.scrollAddRestaurant);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapAdd);
        mapFragment.getMapAsync(this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getLocation();
    }

    private void getLocation(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, requestAccess);
        }
        else{

            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            antut(location);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1500,5,locationListe);
            mMap.setMyLocationEnabled(true);
        }
    }

    private void antut(Location location){
        if(location != null) {

            LatLng lActual = new LatLng(location.getLatitude(), location.getLongitude());
            if(locationPrevious == null){
                //mMap.addMarker(new MarkerOptions().position(lActual).title("Inicio Recorrido"));
                locationPrevious = location;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lActual,18.0f));
            }

            LatLng lPrevious = new LatLng(locationPrevious.getLatitude(), locationPrevious.getLongitude());
            //mMap.addMarker( new MarkerOptions().position( lActual ).title( "The Smoothie Shop" ).icon( BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory. ) ) );
            locationPrevious = location;
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng point) {
                    //allPoints.add(point);
                    mMap.addMarker(new MarkerOptions().position(point).title("Inicio Recorrido"));
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(point));
                }
            });
            mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                @Override
                public void onCameraMove() {
                    svRestoDetail.requestDisallowInterceptTouchEvent(true);
                }
            });
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
            Toast.makeText(this,"Permiso denegado", Toast.LENGTH_LONG).show();
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
}
