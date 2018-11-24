package com.example.godin.proyecto_ed2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

public class Adaptadorchat extends ArrayAdapter<mensaje> implements Filterable {

    private Context contexto;
    private int recurso;
    CustomFilter cs;
ArrayList<mensaje> tempmessages,originalmessages;
    public Adaptadorchat(Context context, int resource, ArrayList<mensaje> objects) {
        super(context, resource, objects);
        contexto=context;
        recurso=resource;
        tempmessages=objects;
        originalmessages=objects;
    }


    @NonNull
    @Override
    public mensaje getItem(int i){
 return originalmessages.get(i);
    }



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

    @Override
    public int getCount(){

        return originalmessages.size();
    }
    @Override
    public long getItemId(int i){

        return i;
    }
    @Override
    public android.widget.Filter getFilter(){

        if(cs==null){
            cs=new CustomFilter();
        }
        return cs;
    }

                class  CustomFilter extends Filter{


                    @Override
                    protected FilterResults performFiltering(CharSequence constraint) {
                        FilterResults results=new FilterResults();
                        if(constraint!=null&&constraint.length()>0){

                            constraint=constraint.toString().toUpperCase();

                        ArrayList<mensaje> mensajesfiltrados=new ArrayList<mensaje>();
                        for (int i =0;i<tempmessages.size();i++) {

                            if (tempmessages.get(i).mensaje.toUpperCase().contains(constraint)) {

                                mensaje mens = new mensaje();
                                mens.mensaje = tempmessages.get(i).mensaje;
                                mens.emisor = tempmessages.get(i).emisor;
                                mens.path = tempmessages.get(i).path;
                                mens.extensionarchivo = tempmessages.get(i).extensionarchivo;
                                mensajesfiltrados.add(mens);
                                results.count = mensajesfiltrados.size();
                            }

                        }
                            results.count = mensajesfiltrados.size();
                            results.values = mensajesfiltrados;
                        }else{

                            results.count=tempmessages.size();
                            results.values=tempmessages;
                        }


                      return results;
    }
                    @Override
                    protected void publishResults(CharSequence constraint, FilterResults results) {
                        originalmessages=(ArrayList<mensaje>)results.values;
                        notifyDataSetChanged();
                    }
                }}
