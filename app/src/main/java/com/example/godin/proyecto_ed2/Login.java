package com.example.godin.proyecto_ed2;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

/**
 * A login screen that offers login via email/password.
 */
public class Login extends AppCompatActivity {
   public static String url="http://192.168.1.6:3000";
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    zigzag z=new zigzag();
    private static final int REQUEST_READ_CONTACTS = 0;
    private static final int REQUEST_STORAGE = 0;
    private static final int REQUEST_INTERNET= 0;
    private RequestQueue queue;
    String keyforcipher;
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private Button register;

    public String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

         register=(Button) findViewById(R.id.iraregistro);
        mPasswordView = (EditText) findViewById(R.id.password);
        register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intento =new Intent(Login.this,Register.class);

                startActivity(intento);
            }
        });
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });


    }





    private boolean mayRequestInternet() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(INTERNET) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(INTERNET)) {

        } else {
            requestPermissions(new String[]{INTERNET}, REQUEST_INTERNET);
        }
        return false;
    }
    private boolean mayRequestStorage() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {

        } else {
            requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, REQUEST_STORAGE);
        }
        return false;
    }
    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {


        if (requestCode == REQUEST_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mayRequestStorage();
            }
        }
        if (requestCode == REQUEST_INTERNET) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mayRequestInternet();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {


        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }



        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            queue = Volley.newRequestQueue(this);
            mAuthTask = new UserLoginTask(email, password);

        }
    }


    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */






    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(Login.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */

    public void showbadlogin(){

        mPasswordView.setError(getString(R.string.error_incorrect_password));
        mPasswordView.requestFocus();

        mEmailView.setError("bad username");
        mEmailView.requestFocus();
    }
    public class UserLoginTask {

        String mUser;
        String mPassword;

        UserLoginTask(String email, String password) {
            mUser = email;
            mPassword = password;

           doInBackground();
        }
        boolean iscorrect=false;

        public void doInBackground() {


            JSONObject jsonBody = new JSONObject();
         String peticion=url+"/auth/login";

            try {
                jsonBody.put("name", "IDONTKNOW");
                jsonBody.put("user", mUser);
                jsonBody.put("mail", "IDONTKNOW");
                jsonBody.put("password", z.codezigzag(mPassword,mUser.length()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, peticion, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        boolean auth=response.getBoolean("auth");
                         token=response.getString("token");
                        if(auth){

                            Intent intento =new Intent(Login.this,Conversaciones.class);
                            intento.putExtra("usuario", mUser);
                            intento.putExtra("token", token);
                            startActivity(intento);
                        }else{

                           showbadlogin();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showbadlogin();
                }
            });

                //This is for Headers If You Needed


            queue.add(request);


        }




    }
}

