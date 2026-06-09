package com.example.health_app_sof1021;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout tilFullName;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private TextInputLayout tilConfirmPassword;
    private TextInputEditText edtFullName;
    private TextInputEditText edtEmail;
    private TextInputEditText edtPassword;
    private TextInputEditText edtConfirmPassword;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseHelper = new DatabaseHelper(this);
        tilFullName = findViewById(R.id.tilFullName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        MaterialButton btnRegister = findViewById(R.id.btnRegister);
        MaterialButton btnBackLogin = findViewById(R.id.btnBackLogin);

        btnRegister.setOnClickListener(v -> register());
        btnBackLogin.setOnClickListener(v -> finish());
    }

    private void register() {
        String fullName = getText(edtFullName);
        String email = getText(edtEmail).toLowerCase();
        String password = getText(edtPassword);
        String confirmPassword = getText(edtConfirmPassword);

        clearErrors();

        if (TextUtils.isEmpty(fullName)) {
            tilFullName.setError("Vui lòng nhập họ tên");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Vui lòng nhập email");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Email không đúng định dạng");
            return;
        }

        if (databaseHelper.isEmailExists(email)) {
            tilEmail.setError("Email đã tồn tại");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Vui lòng nhập mật khẩu");
            return;
        }

        if (password.length() < 6) {
            tilPassword.setError("Mật khẩu tối thiểu 6 ký tự");
            return;
        }

        if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Mật khẩu nhập lại không khớp");
            return;
        }

        boolean success = databaseHelper.insertUser(fullName, email, password);
        if (success) {
            Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearErrors() {
        tilFullName.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);
    }

    private String getText(TextInputEditText editText) {
        if (editText.getText() == null) {
            return "";
        }
        return editText.getText().toString().trim();
    }
}
