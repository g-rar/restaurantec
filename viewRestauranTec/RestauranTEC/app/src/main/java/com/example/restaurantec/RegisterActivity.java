package com.example.restaurantec;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    private EditText edTxtName;
    private EditText edTxtApellido;
    private EditText edTxtEmail;
    private EditText edTxtPassword;
    private ArrayList<String[]> users;

    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        edTxtName = findViewById(R.id.edTxtName);
        edTxtApellido = findViewById(R.id.eTxtAp);
        edTxtEmail = findViewById(R.id.edTxtEmail);
        edTxtPassword = findViewById(R.id.eTxtPass);
        users = (ArrayList<String[]>) getIntent().getExtras().get("Users");
    }

    public void register(View view){
        String name = edTxtName.getText().toString();
        String apellido = edTxtApellido.getText().toString();
        String email = edTxtEmail.getText().toString();
        String password = edTxtPassword.getText().toString();

        boolean completed = !name.isEmpty() && !apellido.isEmpty() && !email.isEmpty() && !password.isEmpty();

        if(completed){
            if(email.contains("@gmail") || email.contains("@hotmail") || email.contains("@outlook")){
                for (int i = 0; i < users.size(); i++) {
                    if(users.get(i)[1].equalsIgnoreCase(email)){
                        Toast.makeText(this, "El correo ya esta registrado.", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                //guardar datos
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("facebook", "False");
                intent.putExtra("email",email);
                intent.putExtra("name",name + " " + apellido);
                startActivity(intent);
            }
            else
                Toast.makeText(this, "Ingrese un correo valido.", Toast.LENGTH_LONG).show();
        }
        else
            Toast.makeText(this, "Complete todos los datos solicitados.", Toast.LENGTH_LONG).show();
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

    public void onClickFacebookButton(View view) {
        LoginActivity.loginButton.performClick();
        LoginActivity.register = true;
        finish();
    }

}
