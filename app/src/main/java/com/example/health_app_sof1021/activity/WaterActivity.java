package com.example.health_app_sof1021.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.health_app_sof1021.R;
import com.example.health_app_sof1021.dao.WaterDAO;
import com.example.health_app_sof1021.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WaterActivity extends AppCompatActivity {
    TextView tvTarget, tvCurrent;
    ProgressBar pbWater;
    Button btn100, btn250, btn500, btnCustomAdd;
    EditText edCustomWater;
    Switch swWaterReminder;
    Spinner spnWaterInterval;
    LinearLayout layoutInterval;
    Button btnSaveReminder;
    MaterialToolbar toolbarWater;
    WaterDAO waterDAO;
    int userId;
    int targetAmount = 2000;
    int currentAmount = 0;
    String today;
    String[] intervals = {"1 giờ", "2 giờ", "3 giờ", "4 giờ"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_water);

        initUi();

        waterDAO = new WaterDAO(this);

        SessionManager sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();
        today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        toolbarWater.setNavigationOnClickListener(v -> finish());

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, intervals);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnWaterInterval.setAdapter(spinnerAdapter);

        swWaterReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                layoutInterval.setVisibility(View.VISIBLE);
            } else {
                layoutInterval.setVisibility(View.GONE);
            }
        });

        btn100.setOnClickListener(v -> addWater(100));
        btn250.setOnClickListener(v -> addWater(250));
        btn500.setOnClickListener(v -> addWater(500));
        btnCustomAdd.setOnClickListener(v -> {
            String customStr = edCustomWater.getText().toString().trim();
            if (!customStr.isEmpty()) {
                try {
                    int ml = Integer.parseInt(customStr);
                    addWater(ml);
                    edCustomWater.setText("");
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Lượng nước không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Vui lòng nhập lượng nước", Toast.LENGTH_SHORT).show();
            }
        });

        btnSaveReminder.setOnClickListener(v -> saveReminderSettings());
        loadTargetAndReminder();
        updateUI();
    }

    private void initUi() {
        tvCurrent = findViewById(R.id.tvWaterCurrent);
        tvTarget = findViewById(R.id.tvWaterTarget);
        pbWater = findViewById(R.id.pbWater);
        btn100 = findViewById(R.id.btnAdd100);
        btn250 = findViewById(R.id.btnAdd250);
        btn500 = findViewById(R.id.btnAdd500);
        btnCustomAdd = findViewById(R.id.btnCustomAdd);
        edCustomWater = findViewById(R.id.edCustomWater);
        swWaterReminder = findViewById(R.id.swWaterReminder);
        spnWaterInterval = findViewById(R.id.spnWaterInterval);
        layoutInterval = findViewById(R.id.layoutInterval);
        btnSaveReminder = findViewById(R.id.btnSaveReminder);
        toolbarWater = findViewById(R.id.toolbarWater);
    }

    private void loadTargetAndReminder() {
        WaterDAO.WaterReminderSettings settings = waterDAO.loadTargetAndReminder(userId);
        targetAmount = settings.getTargetAmount();
        tvTarget.setText("Mục tiêu hôm nay: " + targetAmount + " ml");
        pbWater.setMax(targetAmount);

        swWaterReminder.setChecked(settings.isReminderOn());
        layoutInterval.setVisibility(settings.isReminderOn() ? View.VISIBLE : View.GONE);
        for (int i = 0; i < intervals.length; i++) {
            if (intervals[i].equals(settings.getInterval())) {
                spnWaterInterval.setSelection(i);
                break;
            }
        }
    }

    private void addWater(int ml) {
        waterDAO.addWaterIntake(userId, ml, today);
        int oldAmount = currentAmount;
        updateUI();
        Toast.makeText(this, "Đã thêm " + ml + "ml nước", Toast.LENGTH_SHORT).show();

        if (oldAmount < targetAmount && currentAmount >= targetAmount) {
            Toast.makeText(this, "Chúc mừng! Bạn đã đạt mục tiêu uống nước hôm nay!", Toast.LENGTH_LONG).show();
        }
    }

    private void updateUI() {
        currentAmount = waterDAO.getWaterIntake(userId, today);
        tvCurrent.setText(String.valueOf(currentAmount));
        pbWater.setProgress(currentAmount);
    }

    private void saveReminderSettings() {
        boolean isOn = swWaterReminder.isChecked();
        String interval = spnWaterInterval.getSelectedItem().toString();

        if (waterDAO.saveReminderSettings(userId, isOn, interval)) {
            Toast.makeText(this, "Đã lưu cài đặt nhắc nhở!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Không thể lưu cài đặt!", Toast.LENGTH_SHORT).show();
        }
    }
}
