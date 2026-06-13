package com.example.health_app_sof1021.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.health_app_sof1021.R;
import com.example.health_app_sof1021.dao.BmiDAO;
import com.example.health_app_sof1021.dao.ExerciseDao;
import com.example.health_app_sof1021.dao.MealDao;
import com.example.health_app_sof1021.dao.UserDAO;
import com.example.health_app_sof1021.dao.WaterDAO;
import com.example.health_app_sof1021.model.BmiRecord;
import com.example.health_app_sof1021.model.User;
import com.example.health_app_sof1021.utils.SessionManager;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private MaterialCardView btnCardBMI, btnCardMeals, btnCardExercise, btnCardProfile, btnCardWater, btnCardStats;
    private FrameLayout btnCardNotification;
    private TextView tvHomeName, tvHomeHeightWeight, tvHomeBMIClass, tvHomeBMIValue;
    private TextView tvHomeWaterProgress, tvHomeCaloProgress, tvHomeWorkoutProgress;
    private ProgressBar pbHomeWater, pbHomeCalo, pbHomeWorkout;
    
    private SessionManager sessionManager;
    private UserDAO userDAO;
    private BmiDAO bmiDAO;
    private WaterDAO waterDAO;
    private MealDao mealDao;
    private ExerciseDao exerciseDao;

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
        waterDAO = new WaterDAO(this);
        mealDao = new MealDao(this);
        exerciseDao = new ExerciseDao(this);

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
        btnCardWater = findViewById(R.id.btnCardWater);
        btnCardStats = findViewById(R.id.btnCardStats);

        tvHomeName = findViewById(R.id.tvHomeName);
        tvHomeHeightWeight = findViewById(R.id.tvHomeHeightWeight);
        tvHomeBMIClass = findViewById(R.id.tvHomeBMIClass);
        tvHomeBMIValue = findViewById(R.id.tvHomeBMIValue);
        tvHomeWaterProgress = findViewById(R.id.tvHomeWaterProgress);
        tvHomeCaloProgress = findViewById(R.id.tvHomeCaloProgress);
        tvHomeWorkoutProgress = findViewById(R.id.tvHomeWorkoutProgress);
        pbHomeWater = findViewById(R.id.pbHomeWater);
        pbHomeCalo = findViewById(R.id.pbHomeCalo);
        pbHomeWorkout = findViewById(R.id.pbHomeWorkout);
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

        loadWaterProgress(userId);
        loadCalorieProgress(userId);
        loadWorkoutProgress(userId);
    }

    private void loadCalorieProgress(int userId) {
        String today = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        int currentCalorie = mealDao.getTongCaloTheoNgay(userId, today);
        int targetCalorie = Math.max(sessionManager.getGoalCalories(), 1);

        pbHomeCalo.setMax(targetCalorie);
        pbHomeCalo.setProgress(Math.min(currentCalorie, targetCalorie));
        tvHomeCaloProgress.setText(String.format(Locale.getDefault(),
                "%d / %d kcal", currentCalorie, targetCalorie));
    }

    private void loadWaterProgress(int userId) {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        int currentWater = waterDAO.getWaterIntake(userId, today);
        int targetWater = Math.max(sessionManager.getGoalWater(), 1);

        pbHomeWater.setMax(targetWater);
        pbHomeWater.setProgress(Math.min(currentWater, targetWater));
        tvHomeWaterProgress.setText(String.format(Locale.getDefault(),
                "%d / %d ml", currentWater, targetWater));
    }

    private void loadWorkoutProgress(int userId) {
        int completedWorkout = exerciseDao.getCompletedCountByUserId(userId);
        int totalWorkout = exerciseDao.getTotalCountByUserId(userId);

        pbHomeWorkout.setMax(Math.max(totalWorkout, 1));
        pbHomeWorkout.setProgress(Math.min(completedWorkout, totalWorkout));
        tvHomeWorkoutProgress.setText(String.format(Locale.getDefault(),
                "%d / %d bài", completedWorkout, totalWorkout));
    }

    private void initListener() {
        btnCardBMI.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, BmiActivity.class)));
        btnCardMeals.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, MealActivity.class)));
        btnCardExercise.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ExerciseActivity.class)));
        btnCardProfile.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ProfileActivity.class)));
        btnCardNotification.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, NotificationActivity.class)));
        btnCardWater.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, WaterActivity.class)));
        btnCardStats.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, StatisticsActivity.class)));
    }
}
