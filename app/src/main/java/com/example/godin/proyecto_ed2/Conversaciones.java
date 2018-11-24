package com.example.godin.proyecto_ed2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.view.*;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Conversaciones extends AppCompatActivity {
  String usuariologeado;
  ListView conversaciones;
    RequestQueue queue;
    String token;
    ArrayList<Conversacion> conversacionesmostradas;
    Button showusers;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

         usuariologeado=getIntent().getExtras().getString("usuario");
        token=getIntent().getExtras().getString("token");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversaciones);
        showusers=(Button)findViewById(R.id.newchat) ;
        conversaciones =(ListView) findViewById(R.id.conversaciones);
        conversacionesmostradas =new ArrayList<>();

        showusers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intento =new Intent(Conversaciones.this,usuarios.class);
                intento.putExtra("usuario",usuariologeado);
                intento.putExtra("Authorization",token);

                startActivity(intento);
            }
        });
        queue= Volley.newRequestQueue(this);
        obtenerdatos();


        AdaptadorConversaciones adaptador=new AdaptadorConversaciones(this,R.layout.activity_adaptador_conversaciones, conversacionesmostradas);
        adaptador.usuariologeado=usuariologeado;
        conversaciones.setAdapter(adaptador);

        conversaciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String elegido;
               if(usuariologeado.equals(conversacionesmostradas.get(i).getUsuario1())){

                   elegido= conversacionesmostradas.get(i).getUsuario2();
                }else{
                    elegido= conversacionesmostradas.get(i).getUsuario1();
               }

                Intent intento =new Intent(Conversaciones.this,chat.class);
                intento.putExtra("usuario",usuariologeado);
                intento.putExtra("usuarioelegido",elegido);
                startActivity(intento);
            }
        });


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void obtenerdatos(){

        String peticion=Login.url+"/chat";
        final JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET, peticion, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {


                    JSONArray array=response.getJSONArray("chats");
                    for (int i=0;i<array.length();i++){

                        JSONObject conversacio=array.getJSONObject(i);
                        String usuario1=conversacio.getString("user1");
                        String usuario2=conversacio.getString("user2");
                        JSONArray mensajes=conversacio.getJSONArray("messages");
                        mensaje mensajesparainsertar[]=new mensaje[mensajes.length()];
                       for(int x=0;x<mensajes.length();x++){
                           JSONObject mensajedeljson=mensajes.getJSONObject(i);
                           mensaje mensajeparainsertar=new mensaje();
                                  mensajeparainsertar.emisor= mensajedeljson.getString("emisor");
                           mensajeparainsertar.mensaje= mensajedeljson.getString("message");
                           mensajeparainsertar.path= mensajedeljson.getString("path");
                           mensajeparainsertar.extensionarchivo= mensajedeljson.getString("extensionarchivo");

                           mensajesparainsertar[i]=mensajeparainsertar;

                       }
                        if(usuario1.equals(usuariologeado)||usuario2.equals(usuariologeado)){
                        Conversacion conv=new Conversacion();
                        conv.setUsuario1(usuario1);
                            conv.setUsuario2(usuario2);
                            conv.setMensajes(mensajesparainsertar);
                        conversacionesmostradas.add(conv);

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
        }){

            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", token);
                return params;
            }

            //Pass Your Parameters here
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user", usuariologeado);

                return params;
            } };
        queue.add(request);

    }
}
