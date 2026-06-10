package com.example.health_app_sof1021.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.health_app_sof1021.R;
import com.example.health_app_sof1021.dao.BmiDAO;
import com.example.health_app_sof1021.dao.UserDAO;
import com.example.health_app_sof1021.model.BmiRecord;
import com.example.health_app_sof1021.model.User;
import com.example.health_app_sof1021.utils.SessionManager;
import com.google.android.material.card.MaterialCardView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private MaterialCardView btnCardBMI, btnCardMeals, btnCardExercise, btnCardProfile;
    private FrameLayout btnCardNotification;
    private TextView tvHomeName, tvHomeHeightWeight, tvHomeBMIClass, tvHomeBMIValue;
    
    private SessionManager sessionManager;
    private UserDAO userDAO;
    private BmiDAO bmiDAO;

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

        sessionManager = new SessionManager(this);
        userDAO = new UserDAO(this);
        bmiDAO = new BmiDAO(this);

        initUi();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHomeData();
    }

    private void initUi() {
        btnCardBMI = findViewById(R.id.btnCardBMI);
        btnCardMeals = findViewById(R.id.btnCardMeals);
        btnCardExercise = findViewById(R.id.btnCardExercise);
        btnCardNotification = findViewById(R.id.btnCardNotification);
        btnCardProfile = findViewById(R.id.btnCardProfile);

        tvHomeName = findViewById(R.id.tvHomeName);
        tvHomeHeightWeight = findViewById(R.id.tvHomeHeightWeight);
        tvHomeBMIClass = findViewById(R.id.tvHomeBMIClass);
        tvHomeBMIValue = findViewById(R.id.tvHomeBMIValue);
    }

    private void loadHomeData() {
        int userId = sessionManager.getUserId();
        
        // 1. Hiển thị tên người dùng
        User user = userDAO.getUserById(userId);
        if (user != null) {
            tvHomeName.setText(user.getHoTen());
        }

        // 2. Hiển thị thông tin BMI mới nhất
        BmiRecord latestBmi = bmiDAO.getBMIByUserId(userId);
        if (latestBmi != null) {
            tvHomeHeightWeight.setText(String.format(Locale.getDefault(), 
                    "Chiều cao: %.1f cm | Cân nặng: %.1f kg", 
                    latestBmi.getChieuCao(), latestBmi.getCanNang()));
            
            double bmiValue = latestBmi.getChiSoBMI();
            tvHomeBMIValue.setText(String.format(Locale.getDefault(), "%.1f", bmiValue));

            String status;
            if (bmiValue < 18.5) status = "Gầy";
            else if (bmiValue < 24.9) status = "Bình thường";
            else if (bmiValue < 29.9) status = "Tiền béo phì";
            else status = "Béo phì";
            
            tvHomeBMIClass.setText(String.format("Thể trạng: %s", status));
        } else {
            tvHomeHeightWeight.setText("Chiều cao: -- cm | Cân nặng: -- kg");
            tvHomeBMIValue.setText("--");
            tvHomeBMIClass.setText("Thể trạng: --");
        }
    }

    private void initListener() {
        btnCardBMI.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, BmiActivity.class)));
        btnCardMeals.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, MealActivity.class)));
        btnCardExercise.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ExerciseActivity.class)));
        btnCardProfile.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ProfileActivity.class)));
        btnCardNotification.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, NotificationActivity.class)));
    }
}
