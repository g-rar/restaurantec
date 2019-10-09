package com.example.restaurantec;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.restaurantec.ui.main.ListFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class RestaurantActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap = null;
    private Location locationPrevious;
    private int OK_RESULT_CODE = 1;
    private int requestAccess;
    private ScrollView svRest;
    public AdapterComentList adapterList;
    private ListView listComentRestaurant;
    private ArrayList<String[]> listComent;
    private final int MY_PERMISSIONS = 100;
    private final int PHOTO_CODE = 200;
    private final int SELECT_PICTURE = 300;
    private RelativeLayout mRlView;
    private ArrayList<Bitmap> images;
    private String nameS;
    private LatLng locationRest;
    private int pos;
    private boolean isExist;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_activity);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        final RatingBar ratingBar = findViewById(R.id.ratingRestaurant);
        isExist = false;
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                if(!isExist) {
                    Toast.makeText(RestaurantActivity.this, "" + v, Toast.LENGTH_LONG).show();
                    String[] calificacion = {MainActivity.user[1], "" + v};
                    MainActivity.listRestaurantCaliFilter.get(pos).add(calificacion);
                    int total = Integer.parseInt(MainActivity.listRestaurantInfoFilter.get(pos)[6]) + 1;
                    float cal = Float.parseFloat(MainActivity.listRestaurantInfoFilter.get(pos)[7]) + v;
                    float calTotal = cal / total;
                    MainActivity.listRestaurantInfoFilter.get(pos)[5] = calTotal + "";
                    RatingBar rating = findViewById(R.id.ratingRestaurantP);
                    rating.setRating(Float.parseFloat(MainActivity.listRestaurantInfoFilter.get(pos)[5]));
                    for (int i = 0; i < MainActivity.listRestaurantInfo.size(); i++) {
                        if (MainActivity.listRestaurantInfo.get(i)[0].equalsIgnoreCase(MainActivity.listRestaurantInfoFilter.get(pos)[0])) {
                            MainActivity.listRestaurantInfo.get(i)[5] = MainActivity.listRestaurantInfoFilter.get(i)[5];
                            MainActivity.listRestaurantInfo.get(i)[6] = MainActivity.listRestaurantInfoFilter.get(i)[6];
                            MainActivity.listRestaurantInfo.get(i)[7] = MainActivity.listRestaurantInfoFilter.get(i)[7];
                            break;
                        }
                    }
                    ListFragment.listRestaurant.get(pos)[1] = calTotal + "";
                    ListFragment.adapterList.notifyDataSetChanged();
                    for (int i = 0; i < MainActivity.listRestaurantCali.size(); i++) {
                        if (MainActivity.listRestaurantInfo.get(i)[0].equalsIgnoreCase(MainActivity.listRestaurantInfoFilter.get(pos)[0])) {
                            MainActivity.listRestaurantCali.get(i).add(calificacion);
                            break;
                        }
                    }
                    ratingBar.setIsIndicator(true);
                }
            }
        });
        svRest = findViewById(R.id.scrollRestaurant);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapRestaurant);
        mapFragment.getMapAsync(this);
        if(getIntent() != null) {
            pos = getIntent().getIntExtra("listPos",0);
            TextView name = findViewById(R.id.txtName);
            name.setText(MainActivity.listRestaurantInfoFilter.get(pos)[0]);
            nameS = MainActivity.listRestaurantInfoFilter.get(pos)[0];

            TextView food = findViewById(R.id.txtTypeFood);
            food.setText(MainActivity.listRestaurantInfoFilter.get(pos)[3]);

            TextView phone = findViewById(R.id.txtPhone);
            phone.setText(MainActivity.listRestaurantInfoFilter.get(pos)[1]);

            TextView horario = findViewById(R.id.txtHor);
            horario.setText(MainActivity.listRestaurantInfoFilter.get(pos)[2]);

            RatingBar rating = findViewById(R.id.ratingRestaurantP);
            rating.setRating(Float.parseFloat(MainActivity.listRestaurantInfoFilter.get(pos)[5]));

            ImageView imgPrecio = findViewById(R.id.imgPrecio);

            locationRest = new LatLng(MainActivity.listRestaurantDirFilter.get(pos)[0], MainActivity.listRestaurantDirFilter.get(pos)[1]);

            switch(MainActivity.listRestaurantInfoFilter.get(pos)[4])
            {
                case "0":
                    imgPrecio.setImageResource(R.drawable.dollar1);
                    break;
                case "1":
                    imgPrecio.setImageResource(R.drawable.dollar2);
                    break;
                case "2":
                    imgPrecio.setImageResource(R.drawable.dollar3);
                    break;
            }
            images = MainActivity.listRestaurantImageFilter.get(pos);
        }
        listComentRestaurant = findViewById(R.id.listComentRest);

        listComent = new ArrayList<String[]>();
        adapterList = new AdapterComentList(this, listComent);
        listComentRestaurant.setAdapter(adapterList);

        for (int i = 0; i < MainActivity.listRestaurantComentFilter.get(pos).size() ; i++) {
            listComent.add(MainActivity.listRestaurantComentFilter.get(pos).get(i));

        }

        for (int i = 0; i < MainActivity.listRestaurantCaliFilter.get(pos).size(); i++) {
            if(MainActivity.listRestaurantCaliFilter.get(pos).get(i)[0].equalsIgnoreCase(MainActivity.user[1])){
                isExist = true;
                ratingBar.setRating(Float.parseFloat(MainActivity.listRestaurantCaliFilter.get(pos).get(i)[1]));
                ratingBar.setIsIndicator(true);
                break;
            }
        }

        adapterList.notifyDataSetChanged();

        mRlView = findViewById(R.id.relativeRestaurant);
        myRequestStoragePermission();
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
                mMap.addMarker(new MarkerOptions().position(locationRest)).setTitle(nameS);
                locationPrevious = location;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lActual,18.0f));
            }
            locationPrevious = location;
            mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                @Override
                public void onCameraMove() {
                    svRest.requestDisallowInterceptTouchEvent(true);
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

    public void abrir(View view){
        Intent intent = new Intent(this,ImageActivity.class);
        intent.putExtra("posList",pos);
        startActivity(intent);
    }

    public void addPicture(View view){
        final CharSequence[] options = {"Tomar foto", "Elegir de galeria", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(RestaurantActivity.this);
        builder.setTitle("Elige una opción");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(options[i] == "Tomar foto"){
                    openCamera();
                }
                else if(options[i] == "Elegir de galeria"){
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent,"Selecciona la app:" ), SELECT_PICTURE);
                }
                else if (options[i] == "Cancelar"){
                    dialogInterface.dismiss();
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
        for(int i = 0; i < MainActivity.listRestaurantImage.size(); i++){
            boolean isElement = MainActivity.listRestaurantInfo.get(i)[0] == MainActivity.listRestaurantInfoFilter.get(pos)[0]
                    && MainActivity.listRestaurantInfo.get(i)[1] == MainActivity.listRestaurantInfoFilter.get(pos)[1]
                    && MainActivity.listRestaurantInfo.get(i)[2] == MainActivity.listRestaurantInfoFilter.get(pos)[2]
                    && MainActivity.listRestaurantInfo.get(i)[3] == MainActivity.listRestaurantInfoFilter.get(pos)[3]
                    && MainActivity.listRestaurantInfo.get(i)[4] == MainActivity.listRestaurantInfoFilter.get(pos)[4]
                    && MainActivity.listRestaurantInfo.get(i)[5] == MainActivity.listRestaurantInfoFilter.get(pos)[5];
            if(isElement){
                MainActivity.listRestaurantImage.remove(i);
                MainActivity.listRestaurantImage.add(i,images);
                break;
            }

        }
        MainActivity.listRestaurantImageFilter.remove(pos);
        MainActivity.listRestaurantImageFilter.add(pos,images);
    }

    private void openCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, PHOTO_CODE);
    }

    private boolean myRequestStoragePermission(){

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if((checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)) {
            return true;
        }

        if((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) || (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA))){
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, MY_PERMISSIONS);
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, MY_PERMISSIONS);
        }

        return false;
    }


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
        if(requestCode == MY_PERMISSIONS){
            if(grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(RestaurantActivity.this, "Permisos aceptados", Toast.LENGTH_SHORT).show();
            }
        }else{
            showExplanation();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case PHOTO_CODE:
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    images.add(bitmap);
                    break;
                case SELECT_PICTURE:
                    Uri path = data.getData();
                    ImageView imageView = new ImageView(this);
                    imageView.setImageURI(path);

                    Bitmap bitmap1 = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                    images.add(bitmap1);
                    break;

            }
        }
    }

    private void showExplanation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RestaurantActivity.this);
        builder.setTitle("Permisos denegados");
        builder.setMessage("Para usar las funciones de la app necesitas aceptar los permisos");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addComent(View view) {
        EditText coment = findViewById(R.id.edTxtComent);
        String comentS = coment.getText().toString();
        if(!comentS.isEmpty()){
            String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
            String[] lista = {MainActivity.user[1],currentDate,comentS};
            listComent.add(lista);
            adapterList.notifyDataSetChanged();
            for(int i = 0; i < MainActivity.listRestaurantImage.size(); i++){
                boolean isElement = MainActivity.listRestaurantInfo.get(i)[0] == MainActivity.listRestaurantInfoFilter.get(pos)[0]
                        && MainActivity.listRestaurantInfo.get(i)[1] == MainActivity.listRestaurantInfoFilter.get(pos)[1]
                        && MainActivity.listRestaurantInfo.get(i)[2] == MainActivity.listRestaurantInfoFilter.get(pos)[2]
                        && MainActivity.listRestaurantInfo.get(i)[3] == MainActivity.listRestaurantInfoFilter.get(pos)[3]
                        && MainActivity.listRestaurantInfo.get(i)[4] == MainActivity.listRestaurantInfoFilter.get(pos)[4]
                        && MainActivity.listRestaurantInfo.get(i)[5] == MainActivity.listRestaurantInfoFilter.get(pos)[5];
                if(isElement){
                    MainActivity.listRestaurantComent.get(i).add(lista);
                    break;
                }
            }
            MainActivity.listRestaurantComentFilter.get(pos).add(lista);
        }
        else
            Toast.makeText(this,"Escriba el comentario", Toast.LENGTH_LONG).show();
    }
}
