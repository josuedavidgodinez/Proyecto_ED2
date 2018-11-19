package com.example.godin.proyecto_ed2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.*;

public class chat extends AppCompatActivity {
    String token;
    public String elegido;
    public String usuariologeado;
    ListView mensjesmostrados;
    ArrayList<mensaje> mensajes=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Button act=(Button) findViewById(R.id.button3);
        Button env=(Button) findViewById(R.id.send);
        Button atach =(Button) findViewById(R.id.atach);
        usuariologeado=getIntent().getExtras().getString("usuario");
        elegido=getIntent().getExtras().getString("usuarioelegido");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        act.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mensjesmostrados =(ListView) findViewById(R.id.mensajesenpantalla);


                obtenerdatos();
                Adaptadorchat adaptador=new Adaptadorchat(chat.this,R.layout.activity_adaptador_mensajes,  mensajes);

                mensjesmostrados.setAdapter(adaptador);
            }
        });

        env.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        atach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });



    }

       public void obtenerdatos(){

           String url="peticion de conversaciones";

           JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
               @Override
               public void onResponse(JSONObject response) {
                   try {
                       JSONObject conversacion;
                       JSONArray array =response.getJSONArray("");

                       for (int i=0;i<array.length();i++){

                           conversacion=array.getJSONObject(i);
                           if(conversacion.getString("user1").equals(usuariologeado)){
                               if(conversacion.getString("user2").equals(elegido)){

                                   mensajes=JSONARRAYTOARRAY(array);
                               }

                           }else if(conversacion.getString("user2").equals(usuariologeado)){
                               if(conversacion.getString("user1").equals(elegido)){

                                   mensajes=JSONARRAYTOARRAY(array);
                               }

                           }
                       }
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }
               }
           }, new Response.ErrorListener() {
               @Override
               public void onErrorResponse(VolleyError error) {

               }
           });
       }

    public void enviardatos(){

        String url="peticion de conversaciones";

        JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject conversacion;
                    JSONArray array =response.getJSONArray("");

                    for (int i=0;i<array.length();i++){

                        conversacion=array.getJSONObject(i);
                        if(conversacion.getString("user1").equals(usuariologeado)){
                            if(conversacion.getString("user2").equals(elegido)){

                                mensajes=JSONARRAYTOARRAY(array);
                            }

                        }else if(conversacion.getString("user2").equals(usuariologeado)){
                            if(conversacion.getString("user1").equals(elegido)){

                                mensajes=JSONARRAYTOARRAY(array);
                            }

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    public void uploadatach(){

        String url="peticion de conversaciones";

        JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject conversacion;
                    JSONArray array =response.getJSONArray("");

                    for (int i=0;i<array.length();i++){

                        conversacion=array.getJSONObject(i);
                        if(conversacion.getString("user1").equals(usuariologeado)){
                            if(conversacion.getString("user2").equals(elegido)){

                                mensajes=JSONARRAYTOARRAY(array);
                            }

                        }else if(conversacion.getString("user2").equals(usuariologeado)){
                            if(conversacion.getString("user1").equals(elegido)){

                                mensajes=JSONARRAYTOARRAY(array);
                            }

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }
    public  ArrayList<mensaje>  JSONARRAYTOARRAY(JSONArray jarray) throws JSONException {

         ArrayList<mensaje> mensajes=new ArrayList();
        for(int x=0;x<jarray.length();x++){
            JSONObject mensajedeljson=jarray.getJSONObject(x);
            mensaje mensajeparainsertar=new mensaje();
            mensajeparainsertar.emisor= mensajedeljson.getString("emisor");
            mensajeparainsertar.mensaje= mensajedeljson.getString("mensaje");
            mensajeparainsertar.path= mensajedeljson.getString("path");
            mensajeparainsertar.extensionarchivo= mensajedeljson.getString("ext");

            mensajes.add(mensajeparainsertar);

        }
        return mensajes;
    }


}
