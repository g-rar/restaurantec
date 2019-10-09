package com.example.restaurantec;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class RestoreActivity extends AppCompatActivity {

    private EditText edTxtEmail;
    private Session session;
    private TextView notify;
    private ArrayList<String[]> users;

    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        edTxtEmail = findViewById(R.id.edTxtEmail);
        notify = findViewById(R.id.txtNotify);
        users = (ArrayList<String[]>) getIntent().getExtras().get("Users");
    }

    public void restorePassword(View view) throws MessagingException {
        String email = edTxtEmail.getText().toString();
        if(email.contains("@gmail")){
            for (int i = 0; i < users.size(); i++) {
                if(users.get(i)[1].equalsIgnoreCase(email)){

                    return;
                }
            }
            sendEmail("Adri98.1mm@gmail.com",email,"smtp.googlemail.com");
            notify.setText("Se ha enviado un correo con la contraseña.");
            Toast.makeText(this, "No hay ninguna cuenta asociada.", Toast.LENGTH_LONG).show();
        }
        else if(email.contains("@hotmail") || email.contains("@outlook")){
            for (int i = 0; i < users.size(); i++) {
                if(users.get(i)[1].equalsIgnoreCase(email)){
                    //users.get(i)[2]
                    return;
                }
            }
            sendEmail("adri-98.1@hotmail.com",email,"smtp.live.com");
            notify.setText("Se ha enviado un correo con la contraseña.");
            Toast.makeText(this, "No hay ninguna cuenta asociada.", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this,"Ingrese un correo valido.", Toast.LENGTH_LONG).show();
            notify.setText("");
        }
    }

    private void sendEmail(String pEmail, String pEmailRes, String pType) throws MessagingException {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Properties properties = new Properties();
        properties.put("mail.smtp.host",pType);
        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.starttls.enable","true");
        try{
            session = Session.getDefaultInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return super.getPasswordAuthentication();
                }
            });
        }catch (Exception ex){
            ex.printStackTrace();
        }
        if (session!=null){
            javax.mail.Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(pEmail));
            message.setSubject("Recuperación de contraseña Restaurantec");
            message.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(pEmailRes));//"melinamonserrat2010@hotmail.com"
            message.setContent("Yo tenia un sueño era pequeño pero era un sueño. Les voy a contar la mejor historia de sus vidas asi que les pido que se preparen y ajusten sus cinturones por que lo que van a leer no tendra ni inicio ni fin gracias los quiero.","text/html; charset=utf-8");
            Transport transport = session.getTransport("smtp");
            transport.connect(pType,pEmail,"258456159357268a");
            transport.sendMessage(message,message.getAllRecipients());
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
