package com.example.health_app_sof1021.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.health_app_sof1021.R;
import com.example.health_app_sof1021.dao.UserDAO;
import com.example.health_app_sof1021.model.User;
import com.example.health_app_sof1021.utils.SessionManager;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivSetting, ivBack, ivProfile;
    private TextView tvUserName, tvEmail, tvHeightValue, tvWeightValue;
    private LinearLayout btnHeartRate, btnSteps, btnSleep, btnWater, btnLogout;
    private SessionManager sessionManager;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        initUi();
        sessionManager = new SessionManager(this);
        userDAO = new UserDAO(this);
        loadUserInfo();
        setupClickListeners();
    }

    private void initUi() {
        ivBack = findViewById(R.id.ivBack);
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

    private void loadUserInfo() {
        int userId = sessionManager.getUserId();
        User user = userDAO.getUserById(userId);

        if (user == null) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            sessionManager.logoutUser();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return;
        }

        tvUserName.setText(user.getHoTen());
        tvEmail.setText(user.getEmail());
    }

    private void setupClickListeners() {

        btnLogout.setOnClickListener(v -> {
            sessionManager.logoutUser();
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        ivBack.setOnClickListener(v -> finish());
    }
}
