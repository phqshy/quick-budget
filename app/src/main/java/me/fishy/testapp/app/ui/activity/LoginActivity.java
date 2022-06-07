package me.fishy.testapp.app.ui.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

import me.fishy.testapp.R;
import me.fishy.testapp.app.ui.fragment.schedule.ScheduledPaymentsFragment;
import me.fishy.testapp.common.holders.UserDataHolder;
import me.fishy.testapp.common.request.get.LoginGetRequest;

/**
 * Entry point to the app- all startup methods should be called here
 */
public class LoginActivity extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private static String toastMessage = "";
    private static boolean SESSIONFLAG = false;
    private static String name;
    private static String session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme((R.style.Theme_TestApp));
        super.onCreate(savedInstanceState);
        initAppData();
        setContentView(R.layout.activity_login);

        //WARNING- HACKY CODE AHEAD!!!
        //This auto-logs a user in if they have a set session ID, and makes checks later.
        //Haven't tried it with fake session IDs
        //Keep collapsed to avoid brain rot
        File uuid = new File(getCacheDir() + "/uuid.txt");
        if (uuid.exists()){
            try {
                Scanner scanner = new Scanner(uuid);
                String session = scanner.nextLine();
                String username = scanner.nextLine();

                System.out.println(session);
                System.out.println(username);

                scanner.close();

                LoginActivity.name = username;
                LoginActivity.session = session;

                SESSIONFLAG = true;
            } catch (Exception e){
                SESSIONFLAG = false;
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
                   /*
                   If we get to auto login, this never triggers
                   we can use this to our advantage by having two places
                   to update values that were modified offline without
                   having to worry about duplication
                    */
                   if (!(r.equals("Internal server error") || r.equals("Invalid credentials"))){
                       Intent intent = new Intent(this, MainActivity.class);
                       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                       UserDataHolder.setInstance(UserDataHolder.getGson().fromJson(r, UserDataHolder.class));
                       readOfflineUpdates();
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
                   } else {
                       password.setText("");
                       runOnUiThread(() -> Toast.makeText(this, "Your username or password is incorrect.", Toast.LENGTH_LONG).show());
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

    //if hacky code is true, log in and screw synchronization
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
    
    private void initAppData(){
        createNotificationChannel();

    }

    private void createNotificationChannel(){
        CharSequence name = "Quick Budget";
        String description = "Budgeting app for Android";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel channel = new NotificationChannel("quick-budget", name, importance);
        channel.setDescription(description);

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
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

    public static boolean didAutoLogin(){
        boolean fill = SESSIONFLAG;
        SESSIONFLAG = false;
        return fill;
    }

    private void readOfflineUpdates(){
        File f = new File(getFilesDir() + "/offline_updates.txt");

        if (f.exists()){
            try{
                List<String> lines = Files.readAllLines(Paths.get(getFilesDir() + "/offline_updates.txt"), Charset.defaultCharset());

                /*
                code to reading lines:
                ACTION^VALUE

                Examples:
                BALANCE$^-125 (remove 125 from balance)
                RMNOTIF^title1_title2+text1_text2 (remove notification from active notifications)
                 */

                for (String l : lines){
                    String[] actions = l.split("\\$\\^");
                    switch (actions[0]){
                        case "BALANCE":
                            updateBalance(actions[1]);
                        case "RMNOTIF":
                            removeNotification(actions[1]);
                    }
                }
            } catch (IOException e){
                e.printStackTrace();
            } finally {
            try{
                PrintWriter pw = new PrintWriter(getFilesDir() + "/offline_updates.txt");
                pw.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        }
    }

    private void updateBalance(String amount){
        double aDouble = Double.parseDouble(amount);
        UserDataHolder.getInstance().addToBalance(aDouble);
    }

    private void removeNotification(String undecodedTitleText){
        String[] titleText = undecodedTitleText.split("\\+");
        String title = titleText[0].replaceAll("_", " ");
        String text = titleText[0].replaceAll("_", " ");

        ArrayList<JSONObject> array = UserDataHolder.getInstance().getScheduled();
        try{
            for (JSONObject json : array){
                if (json.getString("title").equals(title) && json.getString("text").equals(text)){
                    array.remove(json);
                    UserDataHolder.getInstance().setScheduled(array);
                    break;
                }
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    public static String getSession(){
        return session;
    }

    public static String getName(){
        return name;
    }

    public static void setToastMessage(String message){
        toastMessage = message;
    }
}