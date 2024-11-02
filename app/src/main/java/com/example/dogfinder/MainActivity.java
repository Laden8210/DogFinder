package com.example.dogfinder;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;

import com.example.dogfinder.Util.Messenger;
import com.example.dogfinder.Util.SessionManager;
import com.example.dogfinder.api.PostCallback;
import com.example.dogfinder.api.PostTask;
import com.example.dogfinder.model.Officer;
import com.example.dogfinder.view.HeroActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements PostCallback {
    private Button btnLogin;
    private TextInputLayout tilUsername, tilPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tilPassword = findViewById(R.id.til_password);
        tilUsername = findViewById(R.id.til_username);

        btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this::loginAction);

        if (SessionManager.getInstance(this).getOfficer() != null) {
            Intent intent = new Intent(this, HeroActivity.class);
            startActivity(intent);
        }

    }

    private void loginAction(View view) {
        String username = tilUsername.getEditText().getText().toString();
        String password = tilPassword.getEditText().getText().toString();

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("username", username);
            jsonObject.put("password", password);

            new PostTask(this, this, "Error", "login.php").execute(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPostSuccess(String responseData) {
        Gson gson = new Gson();

        Officer officer = gson.fromJson(responseData, Officer.class);
        SessionManager.getInstance(this).login(officer);

        Intent intent = new Intent(this, HeroActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPostError(String errorMessage) {
        Messenger.showAlertDialog(this, "Error", errorMessage, "OK").show() ;
    }
}