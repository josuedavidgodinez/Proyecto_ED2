package com.example.godin.proyecto_ed2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class usuarios extends AppCompatActivity {
    String usuariologeado;
    ListView usuarios;
    RequestQueue queue;
    String token;
    ArrayList<String> usuariosdisponibles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        usuariologeado=getIntent().getExtras().getString("usuario");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios);
        usuarios =(ListView) findViewById(R.id.usuariosenpantalla);

        queue= Volley.newRequestQueue(this);
        obtenerdatos();


        ArrayAdapter<String> adaptador=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, usuariosdisponibles);

        usuarios.setAdapter(adaptador);

        usuarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String elegido;
                elegido=usuariosdisponibles.get(i);

              //crearconversacion(elegido);
                Intent intento =new Intent(usuarios.this,chat.class);
                intento.putExtra("usuario",usuariologeado);
                intento.putExtra("usuarioelegido",elegido);
                startActivity(intento);
            }
        });


    }
    public void crearconversacion(String elegido){

        String petciones=Login.url+"/users";
        JSONObject jsonBody = new JSONObject();




        JsonArrayRequest request=new JsonArrayRequest(Request.Method.GET, petciones, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {

                    for (int i=0;i<response.length();i++){

                        JSONObject conversacio=response.getJSONObject(i);
                        String user=conversacio.getString("user");

                        usuariosdisponibles.add(user);
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
        queue.add(request);

    }


    public class peticiones{

        
    }
    public void obtenerdatos(){
         usuariosdisponibles=new ArrayList<>();
        String url=Login.url+"/users";
        JsonArrayRequest request=new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {

                    for (int i=0;i<response.length();i++){

                        JSONObject conversacio=response.getJSONObject(i);
                        String user=conversacio.getString("user");
                        Toast.makeText(usuarios.this,user,Toast.LENGTH_SHORT);
                       usuariosdisponibles.add(user);
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
        queue.add(request);

    }

}
