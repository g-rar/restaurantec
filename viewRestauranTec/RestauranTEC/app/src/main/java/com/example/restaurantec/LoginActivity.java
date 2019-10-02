package com.example.restaurantec;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginActivity extends AppCompatActivity {

    public LoginButton loginButton;
    private CircleImageView circleImageView;
    private TextView txtName,txtEmail;
    int request_code = 1;
    private CallbackManager callbackManager;

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
        componentEnabled(false);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("facebook","False");
        startActivity(intent);
    }

    public void register(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void restore(View view){
        Intent intent = new Intent(this, RestoreActivity.class);
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
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    String first_name = object.getString("first_name");
                    String last_name = object.getString("last_name");
                    String email = object.getString("email");
                    String id = object.getString("id");
                    String image_url = "https://graph.facebook.com/"+id+ "/picture?type=normal";
                    //txtEmail.setText(email);
                    //txtName.setText(first_name +" "+last_name);
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.dontAnimate();
                    intent.putExtra("name", first_name+" "+last_name);
                    intent.putExtra("email",email);
                    intent.putExtra("facebook","True");
                    //Glide.with(LoginActivity.this).load(image_url).into(circleImageView);
                    intent.putExtra("image", image_url);
                    startActivityForResult(intent, request_code);


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this,"no se logro", Toast.LENGTH_LONG).show();
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

}
