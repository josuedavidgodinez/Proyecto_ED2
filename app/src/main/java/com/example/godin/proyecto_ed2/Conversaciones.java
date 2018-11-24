package com.example.godin.proyecto_ed2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.view.*;
import android.widget.Toast;

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
  ListView chatstoshow;
    RequestQueue queue;
    String token;
    ArrayList<Conversacion> conversacionesmostradas;
    Button showusers;
    Button actualizacion;
  Button mlogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        queue= Volley.newRequestQueue(Conversaciones.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversaciones);
        usuariologeado=getIntent().getExtras().getString("usuario");
        token=getIntent().getExtras().getString("token");
        mlogout=(Button)findViewById(R.id.logout) ;
        actualizacion=(Button)findViewById(R.id.actualizar);
        showusers=(Button)findViewById(R.id.newchat) ;
        chatstoshow =(ListView) findViewById(R.id.conversaciones);
        conversacionesmostradas=new ArrayList<>();

        mlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String peticion=Login.url+"/auth/logout";
                JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET, peticion, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean auth=response.getBoolean("auth");
                            if(!auth){
                                token=response.getString("token");
                                Intent intento =new Intent(Conversaciones.this,Login.class);

                                startActivity(intento);
                            }else{
                                Toast.makeText(Conversaciones.this,"was a problem for logout",Toast.LENGTH_SHORT).show();

                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Conversaciones.this,"was a problem for logout",Toast.LENGTH_SHORT).show();
                    }
                });

                //This is for Headers If You Needed


                queue.add(request);
            }
        });

        actualizacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final   AdaptadorConversaciones adaptador=new AdaptadorConversaciones(Conversaciones.this,R.layout.activity_adaptador_conversaciones, conversacionesmostradas);
                adaptador.usuariologeado=usuariologeado;
                chatstoshow.setAdapter(adaptador);
            }
        });

        showusers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intento =new Intent(Conversaciones.this,usuarios.class);
                intento.putExtra("usuario",usuariologeado);
                intento.putExtra("token",token);

                startActivity(intento);





            }
        });



        chatstoshow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                intento.putExtra("token",token);
                startActivity(intento);
            }
        });
        final   AdaptadorConversaciones adaptador=new AdaptadorConversaciones(Conversaciones.this,R.layout.activity_adaptador_conversaciones, conversacionesmostradas);
        adaptador.usuariologeado=usuariologeado;
        chatstoshow.setAdapter(adaptador);

    }
    public void onResume(){

        super.onResume();


        obtenerdatos(new VolleyCallback<ArrayList<Conversacion>>() {
            @Override
            public void onSuccess(ArrayList<Conversacion> result) {
                conversacionesmostradas = result;
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

    public void obtenerdatos(final VolleyCallback<ArrayList<Conversacion>> callback){

        String peticion=Login.url+"/chat";
       queue.add(new JsonObjectRequest(Request.Method.GET, peticion, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    if (response.has("auth")){

                    }else{ArrayList<Conversacion> conversacions=new ArrayList<>();
                        JSONArray array=response.getJSONArray("chats");
                        zigzag z =new zigzag();
                        for (int i=0;i<array.length();i++){

                            JSONObject conversacio=array.getJSONObject(i);
                            String usuario1=conversacio.getString("user1");
                            String usuario2=conversacio.getString("user2");
                            JSONArray mensajes=conversacio.getJSONArray("messages");
                            mensaje mensajesparainsertar[]=new mensaje[mensajes.length()];
                            for(int x=0;x<mensajes.length();x++){

                                JSONObject mensajedeljson=mensajes.getJSONObject(x);
                                mensaje mensajeparainsertar=new mensaje();
                                mensajeparainsertar.emisor= mensajedeljson.getString("emisor");
                                mensajeparainsertar.mensaje= z.decodezigzag(mensajedeljson.getString("mensaje"),2);
                                mensajeparainsertar.path= mensajedeljson.getString("path");
                                mensajeparainsertar.extensionarchivo= mensajedeljson.getString("extensionarchivo");

                                mensajesparainsertar[x]=mensajeparainsertar;

                            }
                            if(usuario1.equals(usuariologeado)||usuario2.equals(usuariologeado)){
                                Conversacion conv=new Conversacion();
                                conv.setUsuario1(usuario1);
                                conv.setUsuario2(usuario2);
                                conv.setMensajes(mensajesparainsertar);
                                conversacions.add(conv);

                            }

                        }callback.onSuccess(conversacions);}


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
            } });


    }
}
