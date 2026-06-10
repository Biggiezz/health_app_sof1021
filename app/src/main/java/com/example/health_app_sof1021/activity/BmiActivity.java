package com.example.health_app_sof1021.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.health_app_sof1021.R;
import com.example.health_app_sof1021.adapter.BmiAdapter;
import com.example.health_app_sof1021.dao.BmiDAO;
import com.example.health_app_sof1021.model.BmiRecord;
import com.example.health_app_sof1021.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BmiActivity extends AppCompatActivity {
    EditText edHeight, edWeight;
    Button btnCalc;
    TextView tvResult;
    RecyclerView rvHistory;
    MaterialToolbar toolbar;
    int userId;
    BmiDAO bmiDAO;
    BmiAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bmi);

        edHeight = findViewById(R.id.edHeight);
        edWeight = findViewById(R.id.edWeight);
        btnCalc = findViewById(R.id.btnCalculate);
        tvResult = findViewById(R.id.tvResult);
        rvHistory = findViewById(R.id.rvBmiHistory);
        toolbar = findViewById(R.id.toolbarBmi);


        bmiDAO = new BmiDAO(this);
        SessionManager sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();

        toolbar.setNavigationOnClickListener(v -> finish());
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        prepopulateFields();
        loadHistory();

        btnCalc.setOnClickListener(v -> {
            String hStr = edHeight.getText().toString();
            String wStr = edWeight.getText().toString();

            if (!hStr.isEmpty() && !wStr.isEmpty()) {
                double heightCm = Double.parseDouble(hStr);
                double weight = Double.parseDouble(wStr);
                double heightM = heightCm / 100;
                double bmi = weight / (heightM * heightM);

                String status = "";
                if (bmi < 18.5) status = "Gầy";
                else if (bmi < 24.9) status = "Bình thường";
                else if (bmi < 29.9) status = "Tiền béo phì";
                else status = "Béo phì";

                tvResult.setText(String.format(Locale.getDefault(), "BMI: %.2f - %s", bmi, status));

                BmiRecord record = new BmiRecord();
                record.setUserId(userId);
                record.setChieuCao(heightCm);
                record.setCanNang(weight);
                record.setChiSoBMI(bmi);
                record.setNgayDo(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()));

                bmiDAO.insertBMI(record);

                loadHistory();
                Toast.makeText(this, "Đã lưu chỉ số BMI thành công!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Vui lòng nhập đủ chiều cao và cân nặng", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void prepopulateFields() {
        // Lấy chiều cao, cân nặng hiện tại từ BMIRecord gần nhất để điền sẵn vào input
        List<BmiRecord> list = bmiDAO.getAllHistoryByUserId(userId);
        if (!list.isEmpty()) {
            BmiRecord latest = list.get(0);
            edHeight.setText(String.format(Locale.getDefault(), "%.1f", latest.getChieuCao()));
            edWeight.setText(String.format(Locale.getDefault(), "%.1f", latest.getCanNang()));
        }
    }

    private void loadHistory() {
        List<BmiRecord> list = bmiDAO.getAllHistoryByUserId(userId);
        if (!list.isEmpty()) {
            BmiRecord latest = list.get(0);
            double bmi = latest.getChiSoBMI();
            String status = "";
            if (bmi < 18.5) status = "Gầy";
            else if (bmi < 24.9) status = "Bình thường";
            else if (bmi < 29.9) status = "Tiền béo phì";
            else status = "Béo phì";
            tvResult.setText(String.format(Locale.getDefault(), "BMI: %.2f - %s", bmi, status));
        }
        adapter = new BmiAdapter(list);
        rvHistory.setAdapter(adapter);
    }
}
