package com.example.restaurantec;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.restaurantec.ui.main.ListFragment;
import com.example.restaurantec.ui.main.MapaFragment;
import com.example.restaurantec.ui.main.SectionsPagerAdapter;

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
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Spinner sp_type;
    private Spinner sp_typeFood;
    private TextView txtName;
    private TextView txtDist;
    private RadioGroup radioGroupPrecio;
    private ImageView imgDollar1;
    private ImageView imgDollar2;
    private ImageView imgDollar3;

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
            if(isFacebook.equalsIgnoreCase("True")) {
                String image_url = getIntent().getStringExtra("image");
                Glide.with(MainActivity.this).load(image_url).into(circleImageView);
                email.setText(getIntent().getStringExtra("email"));
                name.setText(getIntent().getStringExtra("name"));
            }
            else {
                Glide.with(MainActivity.this).load("https://image.flaticon.com/icons/svg/149/149071").into(circleImageView);
                email.setText("Email");
                name.setText("Usuario");
            }
        }

        txtName = findViewById(R.id.edTxtName);

        txtDist = findViewById(R.id.edTxtDist);

        radioGroupPrecio = findViewById(R.id.rGroupPrecio);

        imgDollar1 = findViewById(R.id.imgDolar1);

        imgDollar2 = findViewById(R.id.imgDolar2);

        imgDollar3 = findViewById(R.id.imgDolar3);

        sp_typeFood = (Spinner) findViewById(R.id.spTipoComida);
        String[] foods = {"Rapida","Mexicana","Casera","Italiana","Sandia"};
        sp_typeFood.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, foods));

        sp_type = (Spinner) findViewById(R.id.spTipoBusqueda);
        String[] letra = {"Todos","Nombre","Precio","Tipo Comida","Distancia Km"};
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
    }

    private void selectItem(int type){
        txtName.setVisibility(View.INVISIBLE);
        sp_typeFood.setVisibility(View.INVISIBLE);
        txtDist.setVisibility(View.INVISIBLE);
        radioGroupPrecio.setVisibility(View.INVISIBLE);
        imgDollar1.setVisibility(View.INVISIBLE);
        imgDollar2.setVisibility(View.INVISIBLE);
        imgDollar3.setVisibility(View.INVISIBLE);
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
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            Intent intent = new Intent(this, RestaurantAddActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            Intent data = new Intent();
            data.setData(Uri.parse("exit"));
            setResult(RESULT_OK, data);
            finish();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    public void agregar(View view){
        String[] lista = {"Adrian"};
        ListFragment.listRestaurant.add(lista);
        ListFragment.adapterList.notifyDataSetChanged();
    }

}
