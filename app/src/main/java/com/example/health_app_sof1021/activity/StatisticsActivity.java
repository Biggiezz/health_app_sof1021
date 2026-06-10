package com.example.health_app_sof1021.activity;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.example.health_app_sof1021.R;
import com.example.health_app_sof1021.dao.BmiDAO;
import com.example.health_app_sof1021.dao.WaterDAO;
import com.example.health_app_sof1021.database.DatabaseHelper;
import com.example.health_app_sof1021.model.BmiRecord;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatisticsActivity extends AppCompatActivity {
    LineChart lineChart;
    BarChart barChart;
    Button btnPickDate, btnSearch;
    TextView tvSearchDate, tvResultTitle, tvResultBMI, tvResultWater, tvResultMeals, tvResultExercises;
    LinearLayout layoutResult;
    MaterialToolbar toolbar;
    BmiDAO bmiDAO;
    WaterDAO waterDAO;
    DatabaseHelper dbHelper;
    int userId;
    String selectedSearchDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_statistics);

        lineChart = findViewById(R.id.lineChartBmi);
        barChart = findViewById(R.id.barChartWater);
        btnPickDate = findViewById(R.id.btnPickSearchDate);
        btnSearch = findViewById(R.id.btnSearch);
        tvSearchDate = findViewById(R.id.tvSearchDate);
        tvResultTitle = findViewById(R.id.tvResultTitle);
        tvResultBMI = findViewById(R.id.tvResultBMI);
        tvResultWater = findViewById(R.id.tvResultWater);
        tvResultMeals = findViewById(R.id.tvResultMeals);
        tvResultExercises = findViewById(R.id.tvResultExercises);
        layoutResult = findViewById(R.id.layoutSearchResult);
        toolbar = findViewById(R.id.toolbarStats);

        bmiDAO = new BmiDAO(this);
        waterDAO = new WaterDAO(this);
        dbHelper = new DatabaseHelper(this);

        SharedPreferences pref = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        userId = pref.getInt("userId", -1);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Mặc định chọn ngày hiện tại để tìm kiếm
        selectedSearchDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        tvSearchDate.setText(selectedSearchDate);

        btnPickDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                Calendar selected = Calendar.getInstance();
                selected.set(year, month, dayOfMonth);
                selectedSearchDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selected.getTime());
                tvSearchDate.setText(selectedSearchDate);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnSearch.setOnClickListener(v -> searchHealthData(selectedSearchDate));

        setupBmiChart();
        setupWaterChart();
    }

    // Hàm tìm kiếm dữ liệu theo ngày
    private void searchHealthData(String date) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        boolean hasData = false;

        // 1. Truy vấn BMI trong ngày
        double weight = 0, height = 0, bmiVal = 0;
        Cursor cBmi = db.rawQuery("SELECT canNang, chieuCao, chiSoBMI FROM BMIRecord WHERE userId = ? AND ngayDo = ? ORDER BY BmiID DESC LIMIT 1",
                new String[]{String.valueOf(userId), date});
        if (cBmi.moveToFirst()) {
            weight = cBmi.getDouble(0);
            height = cBmi.getDouble(1);
            bmiVal = cBmi.getDouble(2);
            hasData = true;
            tvResultBMI.setText(String.format(Locale.getDefault(), "BMI: %.2f | Cân nặng: %.1f kg | Chiều cao: %.1f cm", bmiVal, weight, height));
            tvResultBMI.setVisibility(View.VISIBLE);
        } else {
            tvResultBMI.setVisibility(View.GONE);
        }
        cBmi.close();

        // 2. Truy vấn Nước uống trong ngày
        int waterSum = 0;
        Cursor cWater = db.rawQuery("SELECT SUM(luongNuoc) FROM HealthRecord WHERE userId = ? AND ngayGhiNhan = ?",
                new String[]{String.valueOf(userId), date});
        if (cWater.moveToFirst()) {
            waterSum = cWater.getInt(0);
            if (waterSum > 0) {
                hasData = true;
                tvResultWater.setText("Lượng nước đã uống: " + waterSum + " ml");
                tvResultWater.setVisibility(View.VISIBLE);
            } else {
                tvResultWater.setVisibility(View.GONE);
            }
        } else {
            tvResultWater.setVisibility(View.GONE);
        }
        cWater.close();

        // 3. Truy vấn Bữa ăn trong ngày
        int caloSum = 0;
        Cursor cMeals = db.rawQuery("SELECT SUM(hamLuongCalo) FROM MealPlan WHERE userId = ? AND ngayAn = ?",
                new String[]{String.valueOf(userId), date});
        if (cMeals.moveToFirst()) {
            caloSum = cMeals.getInt(0);
            if (caloSum > 0) {
                hasData = true;
                tvResultMeals.setText("Tổng lượng Calo bữa ăn: " + caloSum + " kcal");
                tvResultMeals.setVisibility(View.VISIBLE);
            } else {
                tvResultMeals.setVisibility(View.GONE);
            }
        } else {
            tvResultMeals.setVisibility(View.GONE);
        }
        cMeals.close();

        // 4. Truy vấn Tập luyện trong ngày
        int totalEx = 0;
        int completedEx = 0;
        Cursor cEx = db.rawQuery("SELECT trangThai FROM Exercise WHERE userId = ? AND ngayTap = ?",
                new String[]{String.valueOf(userId), date});
        if (cEx.moveToFirst()) {
            do {
                totalEx++;
                if ("Đã tập".equals(cEx.getString(0))) {
                    completedEx++;
                }
            } while (cEx.moveToNext());

            if (totalEx > 0) {
                hasData = true;
                tvResultExercises.setText("Bài tập đã hoàn thành: " + completedEx + " / " + totalEx + " bài");
                tvResultExercises.setVisibility(View.VISIBLE);
            } else {
                tvResultExercises.setVisibility(View.GONE);
            }
        } else {
            tvResultExercises.setVisibility(View.GONE);
        }
        cEx.close();

        if (hasData) {
            tvResultTitle.setText("Dữ liệu ghi nhận ngày " + date + ":");
            layoutResult.setVisibility(View.VISIBLE);
        } else {
            tvResultTitle.setText("Ngày " + date + ": Không có dữ liệu ghi nhận nào!");
            tvResultBMI.setVisibility(View.GONE);
            tvResultWater.setVisibility(View.GONE);
            tvResultMeals.setVisibility(View.GONE);
            tvResultExercises.setVisibility(View.GONE);
            layoutResult.setVisibility(View.VISIBLE);
        }

    }
    private void setupBmiChart(){
        List<BmiRecord> history = bmiDAO.getAllHistoryByUserId(userId);
        ArrayList<Entry> entries = new ArrayList<>();

        if (history.isEmpty()) {
            //Nếu không có dữ liệu thì biểu đồ trống
            LineDataSet dataSet = new LineDataSet(entries, "Chỉ số BMI");
            lineChart.setData(new LineData(dataSet));
            lineChart.invalidate();
            return;
        }

        // Lấy tối đa 7 bản ghi gần nhất và hiển thị theo thứ tự thời gian từ cũ đến mới
        int count = 0;
        int maxRecords = Math.min(history.size(), 7);
        for (int i = maxRecords - 1; i >= 0; i--) {
            entries.add(new Entry(count++, (float) history.get(i).getChiSoBMI()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Chỉ số BMI");
        dataSet.setColor(Color.parseColor("#10B981"));
        dataSet.setCircleColor(Color.parseColor("#059669"));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setValueTextColor(Color.parseColor("#1F2937"));
        dataSet.setValueTextSize(10f);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false);
        lineChart.invalidate();
    }
    private void setupWaterChart(){
        ArrayList<BarEntry> entries = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // truy vấn lượng nước trong 5 ngày gần đây
        int xPos = 1;
        for (int i = 4; i >= 0; i--) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -i);
            String dateStr = sdf.format(cal.getTime());

            int waterAmount = 0;
            Cursor cursor = db.rawQuery("SELECT SUM(luongNuoc) FROM HealthRecord WHERE userId = ? AND ngayGhiNhan = ?",
                    new String[]{String.valueOf(userId), dateStr});
            if (cursor.moveToFirst()) {
                waterAmount = cursor.getInt(0);
            }
            cursor.close();

            entries.add(new BarEntry(xPos++, waterAmount));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Lượng nước uống (ml)");
        dataSet.setColor(Color.parseColor("#3B82F6"));
        dataSet.setValueTextColor(Color.parseColor("#1F2937"));
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.invalidate();

    }
}