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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("on"," 55");
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            Log.i("on"," 5");
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
        if(adapterList != null) {
            String[] p = {"Hola"};
            listRestaurant.add(p);
            adapterList.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.i("on"," 6");
        View root = null;
        if(ARG_SECTION_NUMBER2) {
            ARG_SECTION_NUMBER2 = false;
            root = inflater.inflate(R.layout.fragment_map, container, false);

            textView = root.findViewById(R.id.section_label);
            pageViewModel.getText().observe(this, new Observer<String>() {
                @Override
                public void onChanged(@Nullable String s) {
                    Log.i("on", " 25");
                    textView.setText(s);
                }
            });

            //MainActivity.mapFragment.getMapAsync(mk);
        }
        else{
            root = inflater.inflate(R.layout.fragment_main, container, false);
            final ListView listViewRestaurant = root.findViewById(R.id.listViewRest);
            if(adapterList == null) {
                listRestaurant = new ArrayList<String[]>();
                adapterList = new AdapterList(root.getContext(), listRestaurant);
                listViewRestaurant.setAdapter(adapterList);
                String[] p = {"Hola"};
                listRestaurant.add(p);
                String[] pa = {"Hola"};
                listRestaurant.add(pa);
                adapterList.notifyDataSetChanged();
            }
            else{
                String[] p = {"Hola"};
                listRestaurant.add(p);
                adapterList.notifyDataSetChanged();
            }
            MainActivity.insere();
        }
        return root;
    }
}