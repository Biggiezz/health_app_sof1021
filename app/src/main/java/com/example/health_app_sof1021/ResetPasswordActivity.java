package com.example.health_app_sof1021;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.health_app_sof1021.database.DatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ResetPasswordActivity extends AppCompatActivity {
    private TextInputLayout tilNewPassword;
    private TextInputLayout tilConfirmPassword;
    private TextInputEditText edtNewPassword;
    private TextInputEditText edtConfirmPassword;
    private DatabaseHelper databaseHelper;
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

        databaseHelper = new DatabaseHelper(this);
        TextView tvEmail = findViewById(R.id.tvEmail);
        tilNewPassword = findViewById(R.id.tilNewPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        MaterialButton btnResetPassword = findViewById(R.id.btnResetPassword);
        MaterialButton btnBackLogin = findViewById(R.id.btnBackLogin);

        email = getIntent().getStringExtra("email");

        if (TextUtils.isEmpty(email) || !databaseHelper.isEmailExists(email)) {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        tvEmail.setText("Tài khoản: " + email);
        btnResetPassword.setOnClickListener(v -> resetPassword());
        btnBackLogin.setOnClickListener(v -> finish());
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

        boolean success = databaseHelper.updatePasswordByEmail(email, newPassword);
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
