package com.example.health_app_sof1021.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.health_app_sof1021.R;
import com.example.health_app_sof1021.utils.SessionManager;

public class Splash extends AppCompatActivity {
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        sessionManager = new SessionManager(this);

        new Handler().postDelayed(() -> {
                    Intent intent;
                    if (sessionManager.isLoggedIn()) {
                        intent = new Intent(Splash.this, MainActivity.class);
                    } else {
                        intent = new Intent(Splash.this, LoginActivity.class);
                    }
                    startActivity(intent);
                    finish();
                }, 3000
        );
    }
}
