package com.example.restauranttec.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.restauranttec.AdapterList;
import com.example.restauranttec.MainActivity;
import com.example.restauranttec.R;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static boolean ARG_SECTION_NUMBER2 = true;
    public static AdapterList adapterList;
    public static ArrayList<String[]> listRestaurant;
    private static TextView textView;
    public static MainActivity mk;

    private PageViewModel pageViewModel;

    public static PlaceholderFragment newInstance(int index) {
        Log.i("on"," 4");
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        if(adapterList != null) {
            String[] p = {"Hola"};
            textView.setText(textView.getText()+"Que tal pues");
            listRestaurant.add(p);
            adapterList.notifyDataSetChanged();
        }
        return fragment;
    }

    public static void orta(MainActivity p){
        mk = p;
    }

    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onResume() {

        super.onResume();
        Fragment frg =null;
        frg = mk.getSupportFragmentManager().findFragmentByTag("MapaP");
        final FragmentTransaction ft = mk.getSupportFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
    }

