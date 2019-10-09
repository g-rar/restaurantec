package com.example.restaurantec;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
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

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission.CAMERA;


public class RestaurantAddActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Spinner foods;
    private GoogleMap mMap = null;
    private Location locationPrevious;
    private int OK_RESULT_CODE = 1;
    private int requestAccess;
    private ScrollView svRestoDetail;
    private final int MY_PERMISSIONS = 100;
    private final int PHOTO_CODE = 200;
    private final int SELECT_PICTURE = 300;
    private RelativeLayout mRlView;
    private ArrayList<Bitmap> images;
    private EditText txtName;
    private EditText txtPhone;
    private EditText txtHorario;
    private RadioGroup radio;
    private double[] locationRestaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        foods = findViewById(R.id.spinner);
        String[] letra = {"Rapida","Mexicana","Casera","D","E"};
        foods.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra));
        svRestoDetail = findViewById(R.id.scrollAddRestaurant);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapAdd);
        mapFragment.getMapAsync(this);
        mRlView = findViewById(R.id.relativeAdd);
        images = new ArrayList<Bitmap>();

        txtName = findViewById(R.id.edTxtName);
        txtPhone = findViewById(R.id.edTxtPhone);
        txtHorario = findViewById(R.id.edTxtH);
        radio = findViewById(R.id.rGroupPrecio);
        MainActivity.listRestaurantImage.add(images);
        myRequestStoragePermission();
    }

    public void abrir(View view){
        Intent intent = new Intent(this,ImageActivity.class);
        intent.putExtra("posList",MainActivity.listRestaurantImage.size()-1);
        startActivity(intent);
    }

    public void addPicture(View view){
        final CharSequence[] options = {"Tomar foto", "Elegir de galeria", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(RestaurantAddActivity.this);
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
                locationPrevious = location;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lActual,18.0f));
            }
            locationPrevious = location;
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng point) {
                    RestaurantAddActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(point));
                    locationRestaurant = new double[2];
                    locationRestaurant[0] = point.latitude;
                    locationRestaurant[1] = point.longitude;
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

    private void openCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, PHOTO_CODE);
    }

    private boolean myRequestStoragePermission(){

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if((checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED)) {
            return true;
        }

        if((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) || (shouldShowRequestPermissionRationale(CAMERA))){
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
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
                Toast.makeText(RestaurantAddActivity.this, "Permisos aceptados", Toast.LENGTH_SHORT).show();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(RestaurantAddActivity.this);
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

    public void addRestaurant(View view) {
        String name = txtName.getText().toString();
        String phone = txtPhone.getText().toString();
        String horario = txtHorario.getText().toString();
        String food = foods.getSelectedItem().toString();
        int select = radio.getCheckedRadioButtonId();
        boolean isNotComp = name.isEmpty() || phone.isEmpty() || horario.isEmpty() || (select == -1) || (locationRestaurant == null);

        if(!isNotComp){
            String precio = "";
            switch (select){
                case R.id.rbutton1:
                    precio = "0";
                    break;
                case R.id.rbutton2:
                    precio = "1";
                    break;
                case R.id.rbutton3:
                    precio = "2";
                    break;
            }
            String[] info = {name,phone,horario,food,precio,"2.5"};
            MainActivity.listRestaurantInfo.add(info);
            MainActivity.listRestaurantDir.add(locationRestaurant);
            String[] list = {name,"2.5",food};
            MainActivity.listRestaurantComent.add(new ArrayList<String[]>());
            ListFragment.listRestaurant.add(list);
            ListFragment.adapterList.notifyDataSetChanged();
            MainActivity.reset();
            finish();
        }
        else
            Toast.makeText(this,"Complete todo lo solicitado",Toast.LENGTH_LONG).show();
    }
}
