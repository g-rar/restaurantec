package com.example.restaurantec;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterImageList extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private Context context;
    private ArrayList<Bitmap> datos;

    public AdapterImageList(Context pContext, ArrayList<ArrayList<Bitmap>> pDatos) {
        context = pContext;
        if(pDatos.size() > 0)
            datos = pDatos.get(pDatos.size()-1);
        else
            datos = new ArrayList<Bitmap>();
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
        final View vista = inflater.inflate(R.layout.image_list, null);
        ImageView imgRestaurant = vista.findViewById(R.id.img_restaurant);
        imgRestaurant.setImageBitmap(datos.get(i));
        return vista;
    }
}
