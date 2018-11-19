package com.example.godin.proyecto_ed2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AdaptadorConversaciones extends ArrayAdapter<Conversacion> {
    private static final String TAG = "CancionListAdapter";
   public String usuariologeado;
    private Context contexto;
    private int recurso;

    public AdaptadorConversaciones( Context context, int resource, ArrayList<Conversacion> objects) {
        super(context, resource, objects);
        contexto=context;
        recurso=resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String nombre="";

        if (usuariologeado.equals(getItem(position).getUsuario1())){

            nombre=getItem(position).getUsuario2();
        }else{
            nombre=getItem(position).getUsuario1();

        }

        String ultimomensaje = getItem(position).getMensajes()[getItem(position).getMensajes().length-1].mensaje;


        LayoutInflater inflater = LayoutInflater.from(contexto);
        convertView=inflater.inflate(recurso,parent,false);
        TextView usuario=(TextView) convertView.findViewById(R.id.usuario);
        TextView mensaje=(TextView) convertView.findViewById(R.id.ultimomensaje);
        usuario.setText(nombre);
        mensaje.setText(String.valueOf(ultimomensaje));
        return  convertView;
    }



}