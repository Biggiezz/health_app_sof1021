package com.example.health_app_sof1021;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {
    private MaterialCardView btnCardMeals, btnCardExercise, btnCardProfile;
    private FrameLayout btnCardNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initUi();
        initEvent();
    }

    private void initEvent() {
        btnCardMeals.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, MealActivity.class)));
        btnCardExercise.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ExerciseActivity.class)));
        btnCardProfile.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ProfileActivity.class)));
    }

    private void initUi() {
        btnCardMeals = findViewById(R.id.btnCardMeals);
        btnCardExercise = findViewById(R.id.btnCardExercise);
        btnCardNotification = findViewById(R.id.btnCardNotification);
        btnCardProfile = findViewById(R.id.btnCardProfile);
    }
}
