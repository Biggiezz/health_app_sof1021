package com.example.health_app_sof1021.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.health_app_sof1021.R;
import com.example.health_app_sof1021.dao.UserDAO;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ResetPasswordActivity extends AppCompatActivity {
    private TextView tvEmail;
    private TextInputLayout tilNewPassword;
    private TextInputLayout tilConfirmPassword;
    private TextInputEditText edtNewPassword;
    private TextInputEditText edtConfirmPassword;
    private MaterialButton btnResetPassword;
    private MaterialButton btnBackLogin;
    private UserDAO userDAO;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initUi();
        userDAO = new UserDAO(this);


        email = getIntent().getStringExtra("email");

        if (TextUtils.isEmpty(email) || !userDAO.isEmailExists(email)) {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        tvEmail.setText("Tài khoản: " + email);
        btnResetPassword.setOnClickListener(v -> resetPassword());
        btnBackLogin.setOnClickListener(v -> finish());
    }

    private void initUi() {
        tvEmail = findViewById(R.id.tvEmail);
        tilNewPassword = findViewById(R.id.tilNewPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnBackLogin = findViewById(R.id.btnBackLogin);
    }

    private void resetPassword() {
        String newPassword = getText(edtNewPassword);
        String confirmPassword = getText(edtConfirmPassword);

        tilNewPassword.setError(null);
        tilConfirmPassword.setError(null);

        if (TextUtils.isEmpty(newPassword)) {
            tilNewPassword.setError("Vui lòng nhập mật khẩu mới");
            return;
        }

        if (newPassword.length() < 6) {
            tilNewPassword.setError("Mật khẩu tối thiểu 6 ký tự");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            tilConfirmPassword.setError("Mật khẩu nhập lại không khớp");
            return;
        }

        boolean success = userDAO.updatePasswordByEmail(email, newPassword);
        if (success) {
            Toast.makeText(this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
            finish();
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
}
