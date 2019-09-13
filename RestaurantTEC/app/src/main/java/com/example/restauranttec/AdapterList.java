package com.example.restauranttec;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
        TextView txtFecha = vista.findViewById(R.id.txt_fecha);
        TextView txtHora = vista.findViewById(R.id.txt_hora);
        TextView duration = vista.findViewById(R.id.txt_duration);
        TextView distancia = vista.findViewById(R.id.txt_distancia);
        txtFecha.setText(datos.get(i)[0]);
        txtHora.setText(datos.get(i)[1]);
        distancia.setText("Recorrido: "+datos.get(i)[2]);
        duration.setText("Duracion: "+datos.get(i)[3]);
        return vista;
    }
}
