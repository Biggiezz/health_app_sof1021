package com.example.health_app_sof1021;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivSetting, ivProfile;
    private TextView tvUserName, tvEmail, tvHeightValue, tvWeightValue;
    private LinearLayout btnHeartRate, btnSteps, btnSleep, btnWater, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        
        initViews();
        setupWindowInsets();
        setupClickListeners();
    }

    private void initViews() {
        ivSetting = findViewById(R.id.ivSetting);
        ivProfile = findViewById(R.id.ivProfile);
        tvUserName = findViewById(R.id.tvUserName);
        tvEmail = findViewById(R.id.tvEmail);
        tvHeightValue = findViewById(R.id.tvHeightValue);
        tvWeightValue = findViewById(R.id.tvWeightValue);
        
        btnHeartRate = findViewById(R.id.btnHeartRate);
        btnSteps = findViewById(R.id.btnSteps);
        btnSleep = findViewById(R.id.btnSleep);
        btnWater = findViewById(R.id.btnWater);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupClickListeners() {

        btnLogout.setOnClickListener(v -> {
            // Xử lý đăng xuất
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
             Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
             startActivity(intent);
             finish();
        });
    }
}
