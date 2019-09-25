package com.example.restaurantec.ui.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.restaurantec.AdapterList;
import com.example.restaurantec.MainActivity;
import com.example.restaurantec.R;
import com.example.restaurantec.RestaurantActivity;

import java.util.ArrayList;


public class ListFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    public static AdapterList adapterList;
    private ListView listViewRestaurant;
    public static ArrayList<String[]> listRestaurant;
    public static MainActivity main;

    public static ListFragment newInstance(int index) {
        ListFragment fragment = new ListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        listViewRestaurant = root.findViewById(R.id.listViewRest);

        listRestaurant = new ArrayList<String[]>();
        adapterList = new AdapterList(root.getContext(), listRestaurant);
        listViewRestaurant.setAdapter(adapterList);
        String[] lista = {"Adrian","2.5","Rapida"};
        ListFragment.listRestaurant.add(lista);
        ListFragment.adapterList.notifyDataSetChanged();

        listViewRestaurant.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(main, RestaurantActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }
}
