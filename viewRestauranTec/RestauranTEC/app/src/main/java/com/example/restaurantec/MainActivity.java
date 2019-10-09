package com.example.restaurantec;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.restaurantec.ui.main.ListFragment;
import com.example.restaurantec.ui.main.MapaFragment;
import com.example.restaurantec.ui.main.SectionsPagerAdapter;

import android.util.Log;
import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Spinner sp_type;
    private Spinner sp_typeFood;
    private Spinner sp_maxStars;
    private Spinner sp_minStars;
    private TextView txtMaxStars;
    private TextView txtMinStars;
    private TextView txtName;
    private TextView txtDist;
    private RadioGroup radioGroupPrecio;
    private ImageView imgDollar1;
    private ImageView imgDollar2;
    private ImageView imgDollar3;
    public static ArrayList<ArrayList<Bitmap>> listRestaurantImage;
    public static ArrayList<ArrayList<String[]>> listRestaurantComent;
    public static ArrayList<String[]> listRestaurantInfo;
    public static ArrayList<double[]> listRestaurantDir;

    public static ArrayList<ArrayList<Bitmap>> listRestaurantImageFilter;
    public static ArrayList<ArrayList<String[]>> listRestaurantComentFilter;
    public static ArrayList<String[]> listRestaurantInfoFilter;
    public static ArrayList<double[]> listRestaurantDirFilter;
    public static String[] user;
    public static boolean touchMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        View headerLayout = navigationView.getHeaderView(0);
        MapaFragment.main = this;
        ListFragment.main = this;
        CircleImageView circleImageView = headerLayout.findViewById(R.id.profile_pic);

        TextView name = headerLayout.findViewById(R.id.txt_name);
        TextView email = headerLayout.findViewById(R.id.txt_email);

        if(getIntent() != null) {
            String isFacebook = getIntent().getStringExtra("facebook");
            user = new String[2];
            user[0] = getIntent().getStringExtra("email");
            email.setText(user[0]);
            if(isFacebook.equalsIgnoreCase("True")) {
                String image_url = getIntent().getStringExtra("image");
                Glide.with(MainActivity.this).load(image_url).into(circleImageView);
            }
            else {
                Glide.with(MainActivity.this).load("https://image.flaticon.com/icons/svg/149/149071").into(circleImageView);
            }
            user[1] = getIntent().getStringExtra("name");
            name.setText(user[1]);
        }

        txtName = findViewById(R.id.edTxtName);

        txtDist = findViewById(R.id.edTxtDist);

        txtMaxStars = findViewById(R.id.txt_maxStars);

        txtMinStars = findViewById(R.id.txt_minStars);

        radioGroupPrecio = findViewById(R.id.rGroupPrecio);

        imgDollar1 = findViewById(R.id.imgDolar1);

        imgDollar2 = findViewById(R.id.imgDolar2);

        imgDollar3 = findViewById(R.id.imgDolar3);

        sp_maxStars = findViewById(R.id.spMaxStars);
        String[] stars = {"0","0.5","1","1.5","2","2.5","3","3.5","4","4.5","5"};
        sp_maxStars.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stars));

        sp_minStars = findViewById(R.id.spMinStars);
        sp_minStars.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stars));

        sp_typeFood = (Spinner) findViewById(R.id.spTipoComida);
        String[] foods = {"Rapida","Mexicana","Casera","Italiana","Sandia"};
        sp_typeFood.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, foods));

        sp_type = (Spinner) findViewById(R.id.spTipoBusqueda);
        String[] letra = {"Todos","Nombre","Precio","Tipo Comida","Distancia mts", "Desde un punto", "Estrellas"};
        sp_type.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra));
        sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectItem(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        listRestaurantImage = new ArrayList<ArrayList<Bitmap>>();
        listRestaurantInfo = new ArrayList<String[]>();
        listRestaurantDir = new ArrayList<double[]>();
        listRestaurantComent = new ArrayList<ArrayList<String[]>>();

        listRestaurantImageFilter = new ArrayList<ArrayList<Bitmap>>();
        listRestaurantComentFilter = new ArrayList<ArrayList<String[]>>();
        listRestaurantInfoFilter = new ArrayList<String[]>();
        listRestaurantDirFilter = new ArrayList<double[]>();

        touchMap = false;

        requestData("https://serene-anchorage-77141.herokuapp.com/usuarios/2.json");
        requestData("https://serene-anchorage-77141.herokuapp.com/restaurantes.json");
        requestData("https://serene-anchorage-77141.herokuapp.com/restaurantes/PintoHouse.json");
        requestData("https://serene-anchorage-77141.herokuapp.com/comentarios.json");
    }

    private void selectItem(int type){
        txtName.setVisibility(View.INVISIBLE);
        sp_typeFood.setVisibility(View.INVISIBLE);
        txtDist.setVisibility(View.INVISIBLE);
        radioGroupPrecio.setVisibility(View.INVISIBLE);
        imgDollar1.setVisibility(View.INVISIBLE);
        imgDollar2.setVisibility(View.INVISIBLE);
        imgDollar3.setVisibility(View.INVISIBLE);
        txtMinStars.setVisibility(View.INVISIBLE);
        txtMaxStars.setVisibility(View.INVISIBLE);
        sp_maxStars.setVisibility(View.INVISIBLE);
        sp_minStars.setVisibility(View.INVISIBLE);
        txtDist.setText("");
        touchMap = false;
        if(MapaFragment.pointMap != null) {
            MapaFragment.pointMap.remove();
            MapaFragment.pointMap = null;
        }
        switch (type){
            case 1:
                txtName.setVisibility(View.VISIBLE);
                break;
            case 2:
                radioGroupPrecio.setVisibility(View.VISIBLE);
                imgDollar1.setVisibility(View.VISIBLE);
                imgDollar2.setVisibility(View.VISIBLE);
                imgDollar3.setVisibility(View.VISIBLE);
                break;
            case 3:
                sp_typeFood.setVisibility(View.VISIBLE);
                break;
            case 4:
                txtDist.setVisibility(View.VISIBLE);
                break;
            case 5:
                txtDist.setVisibility(View.VISIBLE);
                touchMap = true;
                break;
            case 6:
                txtMinStars.setVisibility(View.VISIBLE);
                txtMaxStars.setVisibility(View.VISIBLE);
                sp_maxStars.setVisibility(View.VISIBLE);
                sp_minStars.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finishAffinity();
            System.exit(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add_retaurant) {
            Intent intent = new Intent(this, RestaurantAddActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_exit) {
                Intent data = new Intent();
                data.setData(Uri.parse("exit"));
                setResult(RESULT_OK, data);
                finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static void reset(){
        if(MapaFragment.pointMap != null) {
            MapaFragment.pointMap.remove();
            MapaFragment.pointMap = null;
        }
        listRestaurantImageFilter = (ArrayList<ArrayList<Bitmap>>) listRestaurantImage.clone();
        listRestaurantComentFilter = (ArrayList<ArrayList<String[]>>) listRestaurantComent.clone();
        listRestaurantInfoFilter = (ArrayList<String[]>) listRestaurantInfo.clone();
        listRestaurantDirFilter = (ArrayList<double[]>) listRestaurantDir.clone();
        ListFragment.listRestaurant.clear();
        for(int i = 0; i < listRestaurantInfoFilter.size(); i++){
            String[] list = {listRestaurantInfoFilter.get(i)[0],listRestaurantInfoFilter.get(i)[5],listRestaurantInfoFilter.get(i)[3]};
            ListFragment.listRestaurant.add(list);
        }
        ListFragment.adapterList.notifyDataSetChanged();
        MapaFragment.addPointers();
    }

    public void search(View view) {
        int select = sp_type.getSelectedItemPosition();
        switch (select){
            case 0:
                reset();
                break;
            case 1:
                String name = txtName.getText().toString();
                if(!name.isEmpty()){
                    listRestaurantImageFilter.clear();
                    listRestaurantComentFilter.clear();
                    listRestaurantInfoFilter.clear();
                    listRestaurantDirFilter.clear();
                    ListFragment.listRestaurant.clear();
                    for(int i = 0; i < listRestaurantInfo.size(); i++){
                        if(listRestaurantInfo.get(i)[0].equalsIgnoreCase(name)){
                            listRestaurantInfoFilter.add(listRestaurantInfo.get(i));
                            listRestaurantImageFilter.add(listRestaurantImage.get(i));
                            listRestaurantComentFilter.add(listRestaurantComent.get(i));
                            listRestaurantDirFilter.add(listRestaurantDir.get(i));
                            String[] list = {listRestaurantInfo.get(i)[0],listRestaurantInfo.get(i)[5],listRestaurantInfo.get(i)[3]};
                            ListFragment.listRestaurant.add(list);
                        }
                    }
                }
                else
                    Toast.makeText(this,"Digite el nombre",Toast.LENGTH_LONG).show();
                break;
            case 2:
                int selectRadio = radioGroupPrecio.getCheckedRadioButtonId();
                if(selectRadio != -1){
                    String precio = "";
                    switch (selectRadio) {
                        case R.id.radioButton1:
                            precio = "0";
                            break;
                        case R.id.radioButton2:
                            precio = "1";
                            break;
                        case R.id.radioButton3:
                            precio = "2";
                            break;
                    }

                    listRestaurantImageFilter.clear();
                    listRestaurantComentFilter.clear();
                    listRestaurantInfoFilter.clear();
                    listRestaurantDirFilter.clear();
                    ListFragment.listRestaurant.clear();
                    for(int i = 0; i < listRestaurantInfo.size(); i++){
                        if(listRestaurantInfo.get(i)[4].equalsIgnoreCase(precio)){
                            listRestaurantInfoFilter.add(listRestaurantInfo.get(i));
                            listRestaurantImageFilter.add(listRestaurantImage.get(i));
                            listRestaurantComentFilter.add(listRestaurantComent.get(i));
                            listRestaurantDirFilter.add(listRestaurantDir.get(i));
                            String[] list = {listRestaurantInfo.get(i)[0],listRestaurantInfo.get(i)[5],listRestaurantInfo.get(i)[3]};
                            ListFragment.listRestaurant.add(list);
                        }
                    }
                }
                else
                    Toast.makeText(this,"Elija el precio",Toast.LENGTH_LONG).show();
                break;
            case 3:
                String food = sp_typeFood.getSelectedItem().toString();
                listRestaurantImageFilter.clear();
                listRestaurantComentFilter.clear();
                listRestaurantInfoFilter.clear();
                listRestaurantDirFilter.clear();
                ListFragment.listRestaurant.clear();
                for(int i = 0; i < listRestaurantInfo.size(); i++){
                    if(listRestaurantInfo.get(i)[3].equalsIgnoreCase(food)){
                        listRestaurantInfoFilter.add(listRestaurantInfo.get(i));
                        listRestaurantImageFilter.add(listRestaurantImage.get(i));
                        listRestaurantComentFilter.add(listRestaurantComent.get(i));
                        listRestaurantDirFilter.add(listRestaurantDir.get(i));
                        String[] list = {listRestaurantInfo.get(i)[0],listRestaurantInfo.get(i)[5],listRestaurantInfo.get(i)[3]};
                        ListFragment.listRestaurant.add(list);
                    }
                }
                break;
            case 4:
                String dist = txtDist.getText().toString();
                if(!dist.isEmpty()){
                    float distF = Float.valueOf(dist);
                    listRestaurantImageFilter.clear();
                    listRestaurantComentFilter.clear();
                    listRestaurantInfoFilter.clear();
                    listRestaurantDirFilter.clear();

                    Location myLocation = MapaFragment.mMap.getMyLocation();
                    ListFragment.listRestaurant.clear();
                    for(int i = 0; i < listRestaurantInfo.size(); i++){
                        Location restaurant = new Location("inicio");
                        restaurant.setLatitude(listRestaurantDir.get(i)[0]);
                        restaurant.setLongitude(listRestaurantDir.get(i)[1]);
                        if(myLocation.distanceTo(restaurant) <= distF){
                            listRestaurantInfoFilter.add(listRestaurantInfo.get(i));
                            listRestaurantImageFilter.add(listRestaurantImage.get(i));
                            listRestaurantComentFilter.add(listRestaurantComent.get(i));
                            listRestaurantDirFilter.add(listRestaurantDir.get(i));
                            String[] list = {listRestaurantInfo.get(i)[0],listRestaurantInfo.get(i)[5],listRestaurantInfo.get(i)[3]};
                            ListFragment.listRestaurant.add(list);
                        }
                    }
                }
                else
                    Toast.makeText(this,"Digite la distancia",Toast.LENGTH_LONG).show();
                break;
            case 5:
                String dist2 = txtDist.getText().toString();
                if(!dist2.isEmpty() && MapaFragment.pointMap != null){
                    float distF = Float.valueOf(dist2);
                    listRestaurantImageFilter.clear();
                    listRestaurantComentFilter.clear();
                    listRestaurantInfoFilter.clear();
                    listRestaurantDirFilter.clear();

                    Location myLocation = new Location("punto");
                    myLocation.setLatitude(MapaFragment.pointMap.getPosition().latitude);
                    myLocation.setLongitude(MapaFragment.pointMap.getPosition().longitude);
                    ListFragment.listRestaurant.clear();
                    for(int i = 0; i < listRestaurantInfo.size(); i++){
                        Location restaurant = new Location("inicio");
                        restaurant.setLatitude(listRestaurantDir.get(i)[0]);
                        restaurant.setLongitude(listRestaurantDir.get(i)[1]);
                        if(myLocation.distanceTo(restaurant) <= distF){
                            listRestaurantInfoFilter.add(listRestaurantInfo.get(i));
                            listRestaurantImageFilter.add(listRestaurantImage.get(i));
                            listRestaurantComentFilter.add(listRestaurantComent.get(i));
                            listRestaurantDirFilter.add(listRestaurantDir.get(i));
                            String[] list = {listRestaurantInfo.get(i)[0],listRestaurantInfo.get(i)[5],listRestaurantInfo.get(i)[3]};
                            ListFragment.listRestaurant.add(list);
                        }
                    }
                }
                else
                    Toast.makeText(this,"Digite la distancia y elija un punto en el mapa",Toast.LENGTH_LONG).show();
                break;
            case 6:
                float max  = Float.parseFloat(sp_maxStars.getSelectedItem().toString());
                float min  = Float.parseFloat(sp_minStars.getSelectedItem().toString());
                if(max >= min){
                    listRestaurantImageFilter.clear();
                    listRestaurantComentFilter.clear();
                    listRestaurantInfoFilter.clear();
                    listRestaurantDirFilter.clear();
                    ListFragment.listRestaurant.clear();
                    for(int i = 0; i < listRestaurantInfo.size(); i++){
                        float stars = Float.parseFloat(listRestaurantInfo.get(i)[5]);
                        if(stars >= min && stars <= max){
                            listRestaurantInfoFilter.add(listRestaurantInfo.get(i));
                            listRestaurantImageFilter.add(listRestaurantImage.get(i));
                            listRestaurantComentFilter.add(listRestaurantComent.get(i));
                            listRestaurantDirFilter.add(listRestaurantDir.get(i));
                            String[] list = {listRestaurantInfo.get(i)[0],listRestaurantInfo.get(i)[5],listRestaurantInfo.get(i)[3]};
                            ListFragment.listRestaurant.add(list);
                        }
                    }
                }
                else
                    Toast.makeText(this,"El maximo de estrallas no puede ser menor al minimo.",Toast.LENGTH_LONG).show();
        }
        ListFragment.adapterList.notifyDataSetChanged();
        MapaFragment.addPointers();
    }

    private void requestData(String url) {
        RequestPackage requestPackage = new RequestPackage();
        requestPackage.setMethod("GET");
        requestPackage.setUrl(url);

        Downloader downloader = new Downloader(); //Instantiation of the Async task
        //that’s defined below

        downloader.execute(requestPackage);
    }

    private class Downloader extends AsyncTask<RequestPackage, String, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            return HttpManager.getData(params[0]);
        }

        //The String that is returned in the doInBackground() method is sent to the
        // onPostExecute() method below. The String should contain JSON data.
        @Override
        protected void onPostExecute(String result) {
            try {
                //We need to convert the string in result to a JSONObject
                JSONObject jsonObject = new JSONObject(result);

                //The “ask” value below is a field in the JSON Object that was
                //retrieved from the BitcoinAverage API. It contains the current
                //bitcoin price
                Log.i("GRARDEBUG", "onPostExecute: " + jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
