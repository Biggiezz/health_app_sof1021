package com.example.health_app_sof1021;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(() -> {
                    Intent intent = new Intent(Splash.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }, 3000
        );
    }
}