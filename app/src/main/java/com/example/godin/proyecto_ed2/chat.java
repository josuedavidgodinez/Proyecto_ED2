package com.example.godin.proyecto_ed2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lzw.LZW;

public class chat extends AppCompatActivity {
    String token;
    public String elegido;
    public String usuariologeado;
    ListView mensjesmostrados;
    LZW lzw=new LZW();
    zigzag z=new zigzag();
    Toolbar toolbar;
    RequestQueue queue;
 int clavecifrado;
    EditText messagetext;
   private  String id;
  ArrayList<mensaje> messages;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        queue= Volley.newRequestQueue(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Button act=(Button) findViewById(R.id.button3);
        Button env=(Button) findViewById(R.id.send);
        Button atach =(Button) findViewById(R.id.atach);
         messagetext =(EditText) findViewById(R.id.message);
        usuariologeado=getIntent().getExtras().getString("usuario");
        elegido=getIntent().getExtras().getString("usuarioelegido");
        token=getIntent().getExtras().getString("token");


        mensjesmostrados =(ListView) findViewById(R.id.mensajesenpantalla);

        messages=new ArrayList<>();
        act.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              onResume();

                final  Adaptadorchat adaptador =new Adaptadorchat(chat.this,R.layout.activity_adaptador_mensajes,messages );

                mensjesmostrados.setAdapter(adaptador);


            }
        });
        id="";
        env.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    sendmessage();
                    onResume();
                    final  Adaptadorchat adaptador =new Adaptadorchat(chat.this,R.layout.activity_adaptador_mensajes,messages );

                    mensjesmostrados.setAdapter(adaptador);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });



        atach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });



    }




    public void onResume() {
        super.onResume();


        obtenerdatos(new VolleyCallback<ArrayList<mensaje>>() {
            @Override
            public void onSuccess(ArrayList<mensaje> result) {
                messages = result;
            }
        });
        obteneridconversacion(new VolleyCallback<String>() {
            @Override
            public void onSuccess(String result) {
                id = result;
            }
        });



    }
    public void sendmessage() throws JSONException {
      onResume();
        clavecifrado=usuariologeado.length()+elegido.length();

        String url=Login.url+"/chat/"+id;
        JSONObject jsonBody=new JSONObject();

        JSONObject mensajeagregado=new JSONObject();
        mensajeagregado.put("mensaje",z.codezigzag(messagetext.getText().toString(),2));
        mensajeagregado.put("path","");
        mensajeagregado.put("emisor",usuariologeado);
        mensajeagregado.put("extensionarchivo","");
        JSONArray mensaje=arraytojsonarray(messages);

        mensaje.put(mensajeagregado);
        jsonBody.put("user1",usuariologeado);
        jsonBody.put("user2",elegido);
        jsonBody.put("messages",mensaje);

        queue.add(new JsonObjectRequest(Request.Method.PUT, url, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                    if(response.has("auth")){
                        Intent intento =new Intent(chat.this,Login.class);

                        startActivity(intento);

                    }else{
                        Toast.makeText(chat.this,"Su mensaje fue enviado con exito",Toast.LENGTH_SHORT ).show();
                    }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(chat.this,"Hubo un problema al enviar su mensaje",Toast.LENGTH_SHORT ).show();
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

       public void obtenerdatos(final VolleyCallback<ArrayList<mensaje>> callback){

           String url=Login.url+"/chat";

           queue.add(new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
               @Override
               public void onResponse(JSONObject response) {
                   try {

                       if (response.has("auth")){
                           Intent intento =new Intent(chat.this,Login.class);

                           startActivity(intento);
                       }else {
                           ArrayList<mensaje> lisa=new ArrayList<>();
                           JSONObject conversacion;
                           JSONArray array =response.getJSONArray("chats");

                           for (int i=0;i<array.length();i++){

                               conversacion=array.getJSONObject(i);
                               if(conversacion.getString("user1").equals(usuariologeado)){
                                   if(conversacion.getString("user2").equals(elegido)){

                                       lisa=JSONARRAYTOARRAY(conversacion.getJSONArray("messages"));
                                       callback.onSuccess(lisa);
                                   }

                               }else if(conversacion.getString("user2").equals(usuariologeado)){
                                   if(conversacion.getString("user1").equals(elegido)){

                                       lisa=JSONARRAYTOARRAY(conversacion.getJSONArray("messages"));
                                       callback.onSuccess(lisa);
                                   }

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


    public void obteneridconversacion(final VolleyCallback callback){

        String url=Login.url+"/chat";

        queue.add(new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject conversacion;
                    JSONArray array =response.getJSONArray("chats");

                    for (int i=0;i<array.length();i++){

                        conversacion=array.getJSONObject(i);
                        if(conversacion.getString("user1").equals(usuariologeado)){
                            if(conversacion.getString("user2").equals(elegido)){

                              callback.onSuccess(conversacion.getString("_id"));
                              break;
                            }

                        }else if(conversacion.getString("user2").equals(usuariologeado)){
                            if(conversacion.getString("user1").equals(elegido)){

                                callback.onSuccess(conversacion.getString("_id"));
                                break;
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
    public void enviardatos(){

        String url=Login.url+"/chat";

        JsonObjectRequest request=new JsonObjectRequest(Request.Method.PUT, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject conversacion;
                    JSONArray array =response.getJSONArray("");

                    for (int i=0;i<array.length();i++){

                        conversacion=array.getJSONObject(i);
                        if(conversacion.getString("user1").equals(usuariologeado)){
                            if(conversacion.getString("user2").equals(elegido)){

                                //mensajes=JSONARRAYTOARRAY(array);
                            }

                        }else if(conversacion.getString("user2").equals(usuariologeado)){
                            if(conversacion.getString("user1").equals(elegido)){

                              //  mensajes=JSONARRAYTOARRAY(array);
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

                                //mensajes=JSONARRAYTOARRAY(array);
                            }

                        }else if(conversacion.getString("user2").equals(usuariologeado)){
                            if(conversacion.getString("user1").equals(elegido)){

                               // mensajes=JSONARRAYTOARRAY(array);
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
    clavecifrado=usuariologeado.length()+elegido.length();
         ArrayList<mensaje> mensajes=new ArrayList();
        for(int x=0;x<jarray.length();x++){
            JSONObject mensajedeljson=jarray.getJSONObject(x);
            mensaje mensajeparainsertar=new mensaje();
            mensajeparainsertar.emisor= mensajedeljson.getString("emisor");
            mensajeparainsertar.mensaje= z.decodezigzag(mensajedeljson.getString("mensaje"),2);
            mensajeparainsertar.path= mensajedeljson.getString("path");
            mensajeparainsertar.extensionarchivo= mensajedeljson.getString("extensionarchivo");

            mensajes.add(mensajeparainsertar);

        }
        return mensajes;
    }

    public   JSONArray  arraytojsonarray(ArrayList<mensaje> lisa ) throws JSONException {

        JSONArray mensajes=new JSONArray();
        for(int x=0;x<lisa.size();x++){
            JSONObject jsonbod=new JSONObject();
           mensaje message=lisa.get(x);

            jsonbod.put("emisor",message.emisor);
            jsonbod.put("mensaje",z.codezigzag(message.mensaje,2));
            jsonbod.put("extensionarchivo",message.extensionarchivo);
            jsonbod.put("path",message.path);

            mensajes.put(jsonbod);
        }
        return mensajes;
    }


}
