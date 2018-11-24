package com.example.godin.proyecto_ed2;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
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

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    Button atach, act , env;s
    private  String id;
    ArrayList<mensaje> messages;
    String filePath, file_extn;

    private String readTextFromUri(Uri uri) throws IOException {
        String salida="";

        InputStream inputStream = getContentResolver().openInputStream(uri);
        String cadena="";
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"utf8"),8192);
        while((cadena = reader.readLine())!=null) {
            salida=salida+cadena+"\n";
        }
        inputStream.close();
        reader.close();
        return salida;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        queue= Volley.newRequestQueue(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        act=(Button) findViewById(R.id.button3);
        env=(Button) findViewById(R.id.send);
        atach =(Button) findViewById(R.id.atach);
        messagetext =(EditText) findViewById(R.id.message);
        usuariologeado=getIntent().getExtras().getString("usuario");
        elegido=getIntent().getExtras().getString("usuarioelegido");
        token=getIntent().getExtras().getString("token");


        mensjesmostrados =(ListView) findViewById(R.id.mensajesenpantalla);

        act.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final  Adaptadorchat adaptador =new Adaptadorchat(chat.this,R.layout.activity_adaptador_mensajes,messages );
                mensjesmostrados.setAdapter(adaptador);


            }
        });

        env.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    uploadatach();
                    sendmessage();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        atach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intento = new Intent()
                        .addCategory(Intent.CATEGORY_OPENABLE)
                        .setType("*/*")
                        .setAction(Intent.ACTION_OPEN_DOCUMENT);

                startActivityForResult(Intent.createChooser(intento, "Seleccione un archivo"), 123);

            }
        });



    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK){
            Uri selectedImage = data.getData();

             filePath = null;
            file_extn = null;
            try {
                filePath = readTextFromUri(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            file_extn = filePath.substring(filePath.lastIndexOf(".") + 1);
            atach.setText(filePath);


        }
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

        clavecifrado=usuariologeado.length()+elegido.length();

        String url=Login.url+"/chat/"+id;
        JSONObject jsonBody=new JSONObject();

        JSONObject mensajeagregado=new JSONObject();
        mensajeagregado.put("mensaje",z.codezigzag(messagetext.getText().toString(),2));
        mensajeagregado.put("path",filePath);
        mensajeagregado.put("emisor",usuariologeado);
        mensajeagregado.put("extensionarchivo", file_extn);
        JSONArray mensaje=arraytojsonarray(messages);

        mensaje.put(mensajeagregado);
        jsonBody.put("user1",usuariologeado);
        jsonBody.put("user2",elegido);
        jsonBody.put("messages",mensaje);

        queue.add(new JsonObjectRequest(Request.Method.PUT, url, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Toast.makeText(chat.this,"Su mensaje fue enviado con exito",Toast.LENGTH_SHORT ).show();
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

    public void uploadatach() throws IOException {

        String url= Login.url + "/chat/upload";

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        if (filePath != null) {
            File file = new File(filePath);

            mpEntity.addPart("avatar", new FileBody(file, "application/octet"));
        }
        httppost.setEntity(mpEntity);
        HttpResponse response = httpclient.execute(httppost);
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
            JSONObject jsonbod=mensajes.getJSONObject(x);
            mensaje message=lisa.get(x);
            mensaje mensajeparainsertar=new mensaje();
            jsonbod.put("emisor",mensajeparainsertar.emisor);
            jsonbod.put("mensaje",mensajeparainsertar.mensaje);
            jsonbod.put("extensionarchivo",mensajeparainsertar.extensionarchivo);
            jsonbod.put("path",mensajeparainsertar.path);

            mensajes.put(jsonbod);
        }
        return mensajes;
    }


}
