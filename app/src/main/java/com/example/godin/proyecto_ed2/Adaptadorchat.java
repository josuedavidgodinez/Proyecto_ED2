package com.example.godin.proyecto_ed2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class Adaptadorchat extends ArrayAdapter<mensaje> {

    private Context contexto;
    private int recurso;

    public Adaptadorchat(Context context, int resource, ArrayList<mensaje> objects) {
        super(context, resource, objects);
        contexto=context;
        recurso=resource;
    }


    @NonNull

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String emisor="";
        String mensaje="";
        String path=getItem(position).path;;

            emisor=getItem(position).emisor;

            if(getItem(position).extensionarchivo==""){

                mensaje=path.substring(path.lastIndexOf('/')+1,path.length()-1);
            }else
            {

                mensaje=getItem(position).mensaje;
            }





        LayoutInflater inflater = LayoutInflater.from(contexto);
        convertView=inflater.inflate(recurso,parent,false);
        TextView emi=(TextView) convertView.findViewById(R.id.nombre);
        TextView men=(TextView) convertView.findViewById(R.id.mensaje);
        emi.setText(emisor);
        men.setText(mensaje);
        return  convertView;
    }
}
