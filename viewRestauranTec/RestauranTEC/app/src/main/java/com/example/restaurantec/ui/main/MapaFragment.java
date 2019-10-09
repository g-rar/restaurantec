package com.example.restaurantec.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.restaurantec.MainActivity;
import com.example.restaurantec.R;
import com.example.restaurantec.RestaurantActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapaFragment extends Fragment  implements OnMapReadyCallback {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    public static GoogleMap mMap = null;
    private Location locationPrevious;
    private int OK_RESULT_CODE = 1;
    private int requestAccess;
    public static MainActivity main;
    private SupportMapFragment mapFragment;
    public static Marker pointMap;

    public MapaFragment() {
        // Required empty public constructor
    }

    public static MapaFragment newInstance(String param1, String param2) {
        MapaFragment fragment = new MapaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_mapa, container, false);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return root;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getLocation();
        MainActivity.reset();
    }

    private void getLocation(){
        if (ActivityCompat.checkSelfPermission(main, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(main, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(main,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, requestAccess);
            getLocation();
        }
        else{
            LocationManager locationManager = (LocationManager) main.getSystemService(Context.LOCATION_SERVICE);
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
                locationPrevious = location;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lActual,18.0f));
            }

            locationPrevious = location;
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng point) {
                    if(MainActivity.touchMap) {
                        if (pointMap != null)
                            pointMap.remove();
                        pointMap = mMap.addMarker(new MarkerOptions().position(point).icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    }
                }
            });

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    for (int i = 0; i <  MainActivity.listRestaurantInfoFilter.size(); i++) {
                        if(MainActivity.listRestaurantInfoFilter.get(i)[0].equalsIgnoreCase(marker.getTitle())){
                            Intent intent = new Intent(main, RestaurantActivity.class);
                            intent.putExtra("listPos",i);
                            startActivity(intent);
                        }
                    }
                    return false;
                }
            });
        }
    }


    public static void addPointers(){
        mMap.clear();
        for (int i = 0; i < MainActivity.listRestaurantDirFilter.size(); i++) {
            LatLng point = new LatLng(MainActivity.listRestaurantDirFilter.get(i)[0], MainActivity.listRestaurantDirFilter.get(i)[1]);
            mMap.addMarker(new MarkerOptions().position(point)).setTitle(MainActivity.listRestaurantInfoFilter.get(i)[0]);
        }
        if(MainActivity.touchMap && pointMap != null){
            LatLng point = pointMap.getPosition();
            pointMap = mMap.addMarker(new MarkerOptions().position(point).icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
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
            Toast.makeText(main,"Permiso denegado", Toast.LENGTH_LONG).show();
        }
    }

}
