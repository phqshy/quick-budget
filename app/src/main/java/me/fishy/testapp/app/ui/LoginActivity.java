package me.fishy.testapp.app.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.VoiceInteractor;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;

import me.fishy.testapp.R;
import me.fishy.testapp.common.holders.UserDataHolder;
import me.fishy.testapp.common.request.LoginGetRequest;

public class LoginActivity extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private static String toastMessage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.username = findViewById(R.id.login_username);
        this.password = findViewById(R.id.login_password);

        if (!toastMessage.equals("")){
            Toast.makeText(getBaseContext(), toastMessage, Toast.LENGTH_SHORT).show();
        }

        findViewById(R.id.login_button).setOnClickListener(v -> {
            if (username.getText().toString().equals("") || password.getText().toString().equals("")) {
                Toast.makeText(v.getContext(), "Enter a username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                CompletableFuture<String> resp = new LoginGetRequest("http://159.223.120.100:1984/login", username.getText().toString().replace(' ', '_'), encrypt(password.getText().toString())).get();
                resp.thenAcceptAsync((r) -> {
                   System.out.println(r);
                   if (!(r.equals("Internal server error") || r.equals("Invalid credentials"))){
                       Intent intent = new Intent(this, MainActivity.class);
                       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                       UserDataHolder.setInstance(UserDataHolder.getGson().fromJson(r, UserDataHolder.class));
                       startActivity(intent);
                       finish();
                   }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        });

        findViewById(R.id.button).setOnClickListener((v) -> {
            Intent intent = new Intent(this, CreateAccountActivity.class);
            startActivity(intent);
            finish();
        });
    }

    public static String encrypt(String base) {
        try{
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hash = digest.digest(base.getBytes(StandardCharsets.UTF_8));
            final StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                final String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public static void setToastMessage(String message){
        toastMessage = message;
    }
}