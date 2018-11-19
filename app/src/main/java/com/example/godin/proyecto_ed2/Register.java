package com.example.godin.proyecto_ed2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class Register extends AppCompatActivity {
        EditText nombre;
        EditText mail;
        EditText pass;
        EditText user;
        Button btnreg;
        RequestQueue queue;
        String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        nombre=(EditText) findViewById(R.id.name);
        mail=(EditText) findViewById(R.id.mail);
        user=(EditText) findViewById(R.id.user);
        pass=(EditText) findViewById(R.id.password);
        btnreg=(Button) findViewById(R.id.register) ;

        btnreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queue= Volley.newRequestQueue(Register.this);
                registro(nombre.getText().toString(),mail.getText().toString(),pass.getText().toString(),user.getText().toString());
            }
        });

    }

    public void registro(String name, String mail, String pass, final String user){



        String peticion=Login.url+"/auth/register";
        JSONObject jsonBody = new JSONObject();


        try {
            jsonBody.put("name", name);
            jsonBody.put("user", user);
            jsonBody.put("mail", mail);
            jsonBody.put("password", pass);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, peticion, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean auth=response.getBoolean("auth");
                    if(auth){
                         Toast.makeText(Register.this,"usuario registrado exitosamente",Toast.LENGTH_LONG);
                        Intent intento =new Intent(Register.this,Conversaciones.class);
                        intento.putExtra("usuario", user);
                        startActivity(intento);

                    }else{

                        Toast.makeText(Register.this,"error with authorizate",Toast.LENGTH_SHORT);
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
