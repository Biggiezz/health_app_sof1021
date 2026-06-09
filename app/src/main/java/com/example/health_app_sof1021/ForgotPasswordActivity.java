package com.example.health_app_sof1021;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.health_app_sof1021.database.DatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ForgotPasswordActivity extends AppCompatActivity {
    private TextInputLayout tilEmail;
    private TextInputEditText edtEmail;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseHelper = new DatabaseHelper(this);
        tilEmail = findViewById(R.id.tilEmail);
        edtEmail = findViewById(R.id.edtEmail);
        MaterialButton btnSendResetLink = findViewById(R.id.btnSendResetLink);
        MaterialButton btnBackLogin = findViewById(R.id.btnBackLogin);

        btnSendResetLink.setOnClickListener(v -> checkEmailAndOpenReset());
        btnBackLogin.setOnClickListener(v -> finish());
    }

    private void checkEmailAndOpenReset() {
        String email = getText(edtEmail).toLowerCase();
        tilEmail.setError(null);

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Vui lòng nhập email");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Email không đúng định dạng");
            return;
        }

        if (!databaseHelper.isEmailExists(email)) {
            tilEmail.setError("Email chưa được đăng ký");
            return;
        }

        Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
    }

    private String getText(TextInputEditText editText) {
        if (editText.getText() == null) {
            return "";
        }
        return editText.getText().toString().trim();
    }
}
