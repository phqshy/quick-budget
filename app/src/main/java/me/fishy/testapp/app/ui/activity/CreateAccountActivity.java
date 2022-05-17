package me.fishy.testapp.app.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import me.fishy.testapp.R;
import me.fishy.testapp.common.holders.UserDataHolder;
import me.fishy.testapp.common.request.post.JSONPostRequest;

public class CreateAccountActivity extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private EditText confirmedPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        this.username = findViewById(R.id.username_edittext);
        this.password = findViewById(R.id.password_edittext);
        this.confirmedPassword = findViewById(R.id.confirm_password_edittext);

        findViewById(R.id.create_account_button).setOnClickListener((v) -> {
            String username = this.username.getText().toString();
            String password = this.password.getText().toString();
            String confirm = this.confirmedPassword.getText().toString();

            if (username.equals("") || password.equals("")){
                Toast.makeText(v.getContext(), "Enter both a username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirm)) {
                this.password.setText("");
                this.confirmedPassword.setText("");
                Toast.makeText(v.getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            password = LoginActivity.encrypt(password);

            UserDataHolder holder = new UserDataHolder(username, password);

            try {
                new JSONPostRequest("http://159.223.120.100:1984/create_user").post(UserDataHolder.getGson().toJson(holder))
                .thenAcceptAsync((s) -> {
                    System.out.println(s);
                    if (!s.equals("Success")){
                        LoginActivity.setToastMessage("Error creating account: ".concat(s));
                    } else {
                        LoginActivity.setToastMessage("Account successfully created");
                    }
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}