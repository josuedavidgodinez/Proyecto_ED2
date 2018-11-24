package com.example.godin.proyecto_ed2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
import java.util.List;
import java.util.Map;

public class usuarios extends AppCompatActivity {
    String usuariologeado;
    ListView usuarios;
    RequestQueue queue;
    String token;
    List<String> usuariosdisponibles=new ArrayList<String>();
    ArrayAdapter<String> adaptador;
   static String elegido;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        usuariologeado=getIntent().getExtras().getString("usuario");
        token=getIntent().getExtras().getString("token");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios);
        usuarios =(ListView) findViewById(R.id.usuariosenpantalla);

        queue= Volley.newRequestQueue(this);
        usuariosdisponibles=new ArrayList<>();

        adaptador=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 );
        usuarios.setAdapter(adaptador);
         obtenerdatos();





        usuarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                elegido=usuarios.getItemAtPosition(i).toString();

                try {
                    crearconversacion();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public void iniciarconversacion(){

        Intent intento =new Intent(usuarios.this,chat.class);
        intento.putExtra("usuario", usuariologeado);
        intento.putExtra("usuarioelegido", elegido);
        intento.putExtra("token", token);
        startActivity(intento);
    }


    public void crearconversacion( ) throws JSONException {

        String petciones=Login.url+"/chat";
        JSONArray array=new JSONArray();

        JSONObject jsonBody = new JSONObject();
         jsonBody.put("user1",usuariologeado);
        jsonBody.put("user2",elegido);
        jsonBody.put("messages",array);


        queue.add(new JsonObjectRequest(Request.Method.POST, petciones, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                iniciarconversacion();

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



    public void obtenerdatos(){
        String url=Login.url+"/users";
        queue.add(new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    if (response.has("auth")){

                        Intent intento =new Intent(usuarios.this,Login.class);

                        startActivity(intento);
                    }else{
                        JSONArray array=response.getJSONArray("users");
                        ArrayList<String> lisa=new ArrayList<>();
                        for (int i=0;i<array.length();i++){

                            JSONObject conversacio=array.getJSONObject(i);
                            String user=conversacio.getString("user");
                            Toast.makeText(usuarios.this,user,Toast.LENGTH_SHORT);
                           if(!user.equals(usuariologeado)){
                               lisa.add(user);
                           }


                        }
                        adaptador.clear();
                        adaptador.addAll(lisa);
                        adaptador.notifyDataSetChanged();

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
                params.put("token", token);

                return params;
            } }
        ) ;

    }

}
