package com.example.restaurantec;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterList extends BaseAdapter {
    private static LayoutInflater inflater = null;
    private Context context;
    private ArrayList<String[]> datos;

    public AdapterList(Context pContext, ArrayList<String[]> pDatos){
        context = pContext;
        datos = pDatos;
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return datos.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final View vista = inflater.inflate(R.layout.element_list,null);
        RatingBar rating =  vista.findViewById(R.id.ratingList);
        TextView txtNombre = vista.findViewById(R.id.txt_nombre);
        TextView txtTipo = vista.findViewById(R.id.txt_tipoComida);
//        txtNombre.setText(datos.get(i)[0]);
        rating.setRating(Float.parseFloat(datos.get(i)[1]));
        txtTipo.setText(datos.get(i)[1]);
        return vista;
    }

}
