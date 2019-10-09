package com.example.restaurantec;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    public static LoginButton loginButton;
    private EditText txtEmail;
    private EditText txtPass;
    int request_code = 1;
    private CallbackManager callbackManager;
    public static ArrayList<String[]> users;
    public static boolean register;
    private JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        loginButton = findViewById(R.id.login_button);

        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions(Arrays.asList("email","public_profile"));
        componentEnabled(false);
        checkLoginStatus();

        txtEmail = findViewById(R.id.edTxtUser);
        txtPass = findViewById(R.id.eTxtPass);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult)
            {

            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this,error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        users = new ArrayList<String[]>();
        requestData("https://serene-anchorage-77141.herokuapp.com/usuarios.json");
        register = false;
    }

    private void componentEnabled(boolean enabled){
        Button btnIniciar = findViewById(R.id.btnIniciar);
        Button btnRegister = findViewById(R.id.btnRegistrar);
        ImageView imgFacebook = findViewById(R.id.imgFacebook);
        EditText edEmail = findViewById(R.id.edTxtUser);
        EditText edPass = findViewById(R.id.eTxtPass);
        TextView txtRestore = findViewById(R.id.txtRestore);
        ImageView imgLoad = findViewById(R.id.imgLoad);
        btnIniciar.setEnabled(enabled);
        btnRegister.setEnabled(enabled);
        imgFacebook.setEnabled(enabled);
        edEmail.setEnabled(enabled);
        edPass.setEnabled(enabled);
        txtRestore.setEnabled(enabled);
        if(enabled)
            imgLoad.setVisibility(View.INVISIBLE);
        else
            imgLoad.setVisibility(View.VISIBLE);
    }

    public void login(View view){
        String email = txtEmail.getText().toString();
        String password = txtPass.getText().toString();

        boolean isNotComp = email.isEmpty() || password.isEmpty();

        if(!isNotComp) {
            componentEnabled(false);
            boolean isLogin = false;
            for(int i = 0; i < users.size(); i++){
                if(users.get(i)[1].equalsIgnoreCase(email) && users.get(i)[2].equals(password)){
                    isLogin = true;
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("facebook", "False");
                    intent.putExtra("email",email);
                    intent.putExtra("name",users.get(i)[0]);
                    intent.putExtra("Users", users);
                    startActivity(intent);
                }
            }
            if(!isLogin) {
                Toast.makeText(this, "El usuario o contraseña son incorrectos.", Toast.LENGTH_LONG).show();
                componentEnabled(true);
            }
        }
        else
            Toast.makeText(this,"Complete todo lo solicitado",Toast.LENGTH_LONG).show();
    }

    public void register(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("Users", users);
        startActivity(intent);
    }

    public void restore(View view){
        Intent intent = new Intent(this, RestoreActivity.class);
        intent.putExtra("Users", users);
        startActivity(intent);
    }

    public void onClickFacebookButton(View view) {
        componentEnabled(false);
        actionFacebookButton();
    }

    private void actionFacebookButton(){
        loginButton.performClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
        if ((requestCode == request_code) && (resultCode == RESULT_OK)){
            LoginManager.getInstance().logOut();
            componentEnabled(true);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    AccessTokenTracker tokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken)
        {
            if(currentAccessToken==null)
            {
                componentEnabled(true);
            }
            else
                loadUserProfile(currentAccessToken);
        }
    };

    private void loadUserProfile(AccessToken newAccessToken)
    {
        GraphRequest request = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response)
            {
                try {
                    componentEnabled(false);

                    Log.i("GEREE",users.toString());
                    String first_name = object.getString("first_name");
                    String last_name = object.getString("last_name");
                    String email = object.getString("email");
                    String id = object.getString("id");
                    String image_url = "https://graph.facebook.com/"+id+ "/picture?type=normal";
                    if(register){
                        for (int i = 0; i < users.size(); i++) {
                            if(users.get(i)[1].equalsIgnoreCase(email)){
                                Toast.makeText(LoginActivity.this, "El usuario ya esta registrado.", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                                intent.putExtra("Users", users);
                                startActivity(intent);
                                register = false;
                                return;
                            }
                        }
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.dontAnimate();
                        intent.putExtra("name", first_name+" "+last_name);
                        intent.putExtra("email",email);
                        intent.putExtra("facebook","True");
                        intent.putExtra("image", image_url);
                        intent.putExtra("Users", users);
                        startActivityForResult(intent, request_code);
                        return;
                    }
                    else {
                        // for(int i = 0; i < users.size(); i++){
                        // if(users.get(i)[1].equalsIgnoreCase(email)){
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.dontAnimate();
                        intent.putExtra("name", first_name + " " + last_name);
                        intent.putExtra("email", email);
                        intent.putExtra("facebook", "True");
                        intent.putExtra("image", image_url);
                        intent.putExtra("Users", users);
                        startActivityForResult(intent, request_code);
                        // return;
                        //}
                        //}
                    }
                  //  Toast.makeText(LoginActivity.this,"El usuario no esta registrado", Toast.LENGTH_LONG).show();


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this,"No se logro", Toast.LENGTH_LONG).show();
                }

            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields","first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();

    }

    private void checkLoginStatus()
    {
        if(AccessToken.getCurrentAccessToken()!=null)
        {
            loadUserProfile(AccessToken.getCurrentAccessToken());
        }
        else {
            componentEnabled(true);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
    private void requestData(String url) {
        RequestPackage requestPackage = new RequestPackage();
        requestPackage.setMethod("GET");
        requestPackage.setUrl(url);

        Downloader downloader = new Downloader(); //Instantiation of the Async task
        //that’s defined below

        downloader.execute(requestPackage);
    }

    private class Downloader extends AsyncTask<RequestPackage, String, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            return HttpManager.getData(params[0]);
        }

        //The String that is returned in the doInBackground() method is sent to the
        // onPostExecute() method below. The String should contain JSON data.
        @Override
        protected void onPostExecute(String result) {
            try {
                //We need to convert the string in result to a JSONObject
                jsonObject = new JSONObject(result);
                JSONArray userJsonArray = jsonObject.getJSONArray("usuarios");
                for(int i = 0; i < userJsonArray.length(); i++){
                    String userC[] = new String[3];
                    userC[0] = userJsonArray.getJSONObject(i).getString("nombreusuario") + " "+
                            userJsonArray.getJSONObject(i).getString("apellidousuario");
                    userC[1] = userJsonArray.getJSONObject(i).getString("correousuario");
                    userC[2] = userJsonArray.getJSONObject(i).getString("contrasenausuario");
                    users.add(userC);
                }

                Log.i("GRARDEBUG", "onPostExecute: " + jsonObject.getJSONArray("usuarios").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
