package me.fishy.testapp.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

import me.fishy.testapp.R;
import me.fishy.testapp.common.holders.UserDataHolder;
import me.fishy.testapp.common.request.LoginGetRequest;
import me.fishy.testapp.common.request.SessionGetRequest;

public class LoginActivity extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private static String toastMessage = "";
    private static boolean SESSIONFLAG = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //WARNING- HACKY CODE AHEAD!!!
        //This auto-logs a user in if they have a set session ID, and makes checks later. Haven't tried it with fake session IDs
        File uuid = new File(getCacheDir() + "/uuid.txt");
        if (uuid.exists()){
            try {
                Scanner scanner = new Scanner(uuid);
                String session = scanner.nextLine();
                String username = scanner.nextLine();

                System.out.println(session);
                System.out.println(username);

                scanner.close();

                SESSIONFLAG = true;

                new SessionGetRequest("https://phqsh.me/login_session", username, session)
                        .get()
                        .thenAccept((s) -> {
                            System.out.println(s);
                            if (!(s.equals("Invalid session ID") || s.equals("Internal server error"))){
                                UserDataHolder.setInstance(UserDataHolder.getGson().fromJson(s, UserDataHolder.class));
                           }
                        });
            } catch (Exception e){
                SESSIONFLAG = false;
                e.printStackTrace();
            }
        }

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
                CompletableFuture<String> resp = new LoginGetRequest("https://phqsh.me/login", username.getText().toString().replace(' ', '_'), encrypt(password.getText().toString())).get();
                resp.thenAcceptAsync((r) -> {
                   System.out.println(r);
                   if (!(r.equals("Internal server error") || r.equals("Invalid credentials"))){
                       Intent intent = new Intent(this, MainActivity.class);
                       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                       UserDataHolder.setInstance(UserDataHolder.getGson().fromJson(r, UserDataHolder.class));
                       try {
                           File f = new File(getCacheDir() + "/uuid.txt");

                           if (f.exists()){
                               System.out.println("writing null");
                               PrintWriter pw = new PrintWriter(getCacheDir() + "/uuid.txt");
                               pw.close();
                           }
                           FileWriter writer = new FileWriter(f);
                           System.out.println("writing uuid");
                           writer.write(UserDataHolder.getInstance().getUUID());
                           System.out.println("writing username");
                           writer.write("\n");
                           writer.write(UserDataHolder.getInstance().getUsername());
                           writer.close();

                           System.out.println("starting activity");
                           startActivity(intent);
                           finish();
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
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

    @Override
    protected void onStart() {
        super.onStart();

        System.out.println("checking session");
        if (SESSIONFLAG){
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            System.out.println("starting activity");
            startActivity(intent);
            finish();
        }
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