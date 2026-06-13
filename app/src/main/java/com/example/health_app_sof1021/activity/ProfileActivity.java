package com.example.health_app_sof1021.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.health_app_sof1021.R;
import com.example.health_app_sof1021.dao.BmiDAO;
import com.example.health_app_sof1021.dao.UserDAO;
import com.example.health_app_sof1021.model.BmiRecord;
import com.example.health_app_sof1021.model.User;
import com.example.health_app_sof1021.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivBack, ivProfile;
    private TextView tvUserName, tvEmail, tvHeightValue, tvWeightValue;
    private LinearLayout btnChangePassword, btnLogout;
    private TextInputEditText tietGoalCalories, tietGoalWater;
    private Button btnUpdateGoals;
    
    private SessionManager sessionManager;
    private UserDAO userDAO;
    private BmiDAO bmiDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        sessionManager = new SessionManager(this);
        userDAO = new UserDAO(this);
        bmiDAO = new BmiDAO(this);

        initUi();
        loadGoals();
        setupClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật lại thông tin mỗi khi quay lại màn hình Profile
        loadUserInfo();
    }

    private void initUi() {
        ivBack = findViewById(R.id.ivBack);
        ivProfile = findViewById(R.id.ivProfile);
        tvUserName = findViewById(R.id.tvUserName);
        tvEmail = findViewById(R.id.tvEmail);
        tvHeightValue = findViewById(R.id.tvHeightValue);
        tvWeightValue = findViewById(R.id.tvWeightValue);

        tietGoalCalories = findViewById(R.id.tietGoalCalories);
        tietGoalWater = findViewById(R.id.tietGoalWater);
        btnUpdateGoals = findViewById(R.id.btnUpdateGoals);

        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnLogout = findViewById(R.id.btnLogout);
    }

    /// Load lượng calories và nước mục tiêu mỗi ngày
    private void loadGoals() {
        tietGoalCalories.setText(String.valueOf(sessionManager.getGoalCalories()));
        tietGoalWater.setText(String.valueOf(sessionManager.getGoalWater()));
    }

    private void loadUserInfo() {
        int userId = sessionManager.getUserId();
        User user = userDAO.getUserById(userId);

        // Lấy bản ghi BMI mới nhất (chứa chiều cao/cân nặng mới nhất)
        BmiRecord bmi = bmiDAO.getBMIByUserId(userId);

        if (user == null) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            sessionManager.logoutUser();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        tvUserName.setText(user.getHoTen());
        tvEmail.setText(user.getEmail());

        if (bmi != null) {
            tvHeightValue.setText(String.format(Locale.getDefault(), "%.1f cm", bmi.getChieuCao()));
            tvWeightValue.setText(String.format(Locale.getDefault(), "%.1f kg", bmi.getCanNang()));
        } else {
            tvHeightValue.setText("-- cm");
            tvWeightValue.setText("-- kg");
        }
    }

    private void setupClickListeners() {
        btnLogout.setOnClickListener(v -> {
            sessionManager.logoutUser();
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        
        btnChangePassword.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, ChangePasswordActivity.class)));
        
        ivBack.setOnClickListener(v -> finish());
        
        ivProfile.setOnClickListener(v -> showEditProfileDialog());

        btnUpdateGoals.setOnClickListener(v -> updateGoals());
    }

    private void updateGoals() {
        String calStr = tietGoalCalories.getText().toString().trim();
        String waterStr = tietGoalWater.getText().toString().trim();

        if (calStr.isEmpty() || waterStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ mục tiêu", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int calories = Integer.parseInt(calStr);
            int water = Integer.parseInt(waterStr);

            if (calories <= 0 || water <= 0) {
                Toast.makeText(this, "Mục tiêu phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                return;
            }

            sessionManager.setGoalCalories(calories);
            sessionManager.setGoalWater(water);

            Toast.makeText(this, "Cập nhật mục tiêu thành công!", Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng nhập số hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    private void showEditProfileDialog() {
        int userId = sessionManager.getUserId();
        User user = userDAO.getUserById(userId);
        if (user == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chỉnh sửa thông tin cá nhân");

        // Tạo layout cho Dialog
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 40, 60, 20);

        final EditText edtName = new EditText(this);
        edtName.setHint("Họ tên");
        edtName.setText(user.getHoTen());
        layout.addView(edtName);

        final EditText edtEmail = new EditText(this);
        edtEmail.setHint("Email");
        edtEmail.setText(user.getEmail());
        layout.addView(edtEmail);

        builder.setView(layout);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newName = edtName.getText().toString().trim();
            String newEmail = edtEmail.getText().toString().trim();

            if (newName.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (userDAO.updateUserInfo(userId, newName, newEmail)) {
                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                loadUserInfo(); // Cập nhật lại UI
            } else {
                Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}
