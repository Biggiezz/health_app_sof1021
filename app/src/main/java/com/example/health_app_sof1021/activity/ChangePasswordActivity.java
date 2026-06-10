package com.example.health_app_sof1021.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.health_app_sof1021.R;
import com.example.health_app_sof1021.dao.UserDAO;
import com.example.health_app_sof1021.model.User;
import com.example.health_app_sof1021.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ChangePasswordActivity extends AppCompatActivity {
    private ImageView ivBack;
    private TextView tvAccount;
    private TextInputLayout tilCurrentPassword, tilNewPassword, tilConfirmPassword;
    private TextInputEditText edtCurrentPassword, edtNewPassword, edtConfirmPassword;
    private MaterialButton btnSavePassword;
    private SessionManager sessionManager;
    private UserDAO userDAO;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initUi();
        sessionManager = new SessionManager(this);
        userDAO = new UserDAO(this);

        int userId = sessionManager.getUserId();
        currentUser = userDAO.getUserById(userId);
        if (currentUser == null) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            logoutToLogin();
            return;
        }

        tvAccount.setText("Tài khoản: " + currentUser.getEmail());
        ivBack.setOnClickListener(v -> finish());
        btnSavePassword.setOnClickListener(v -> changePassword());
    }

    private void initUi() {
        ivBack = findViewById(R.id.ivBack);
        tvAccount = findViewById(R.id.tvAccount);
        tilCurrentPassword = findViewById(R.id.tilCurrentPassword);
        tilNewPassword = findViewById(R.id.tilNewPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        edtCurrentPassword = findViewById(R.id.edtCurrentPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnSavePassword = findViewById(R.id.btnSavePassword);
    }

    private void changePassword() {
        String currentPassword = getText(edtCurrentPassword);
        String newPassword = getText(edtNewPassword);
        String confirmPassword = getText(edtConfirmPassword);

        tilCurrentPassword.setError(null);
        tilNewPassword.setError(null);
        tilConfirmPassword.setError(null);

        if (TextUtils.isEmpty(currentPassword)) {
            tilCurrentPassword.setError("Vui lòng nhập mật khẩu hiện tại");
            return;
        }

        if (!currentPassword.equals(currentUser.getMatKhau())) {
            tilCurrentPassword.setError("Mật khẩu hiện tại không đúng");
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            tilNewPassword.setError("Vui lòng nhập mật khẩu mới");
            return;
        }

        if (newPassword.length() < 6) {
            tilNewPassword.setError("Mật khẩu tối thiểu 6 ký tự");
            return;
        }

        if (newPassword.equals(currentPassword)) {
            tilNewPassword.setError("Mật khẩu mới phải khác mật khẩu hiện tại");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            tilConfirmPassword.setError("Mật khẩu nhập lại không khớp");
            return;
        }

        boolean success = userDAO.updatePasswordByUserId(currentUser.getUserId(), newPassword);
        if (success) {
            Toast.makeText(this, "Đổi mật khẩu thành công, vui lòng đăng nhập lại", Toast.LENGTH_LONG).show();
            logoutToLogin();
        } else {
            Toast.makeText(this, "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private String getText(TextInputEditText editText) {
        if (editText.getText() == null) {
            return "";
        }
        return editText.getText().toString().trim();
    }

    private void logoutToLogin() {
        sessionManager.logoutUser();
        Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
