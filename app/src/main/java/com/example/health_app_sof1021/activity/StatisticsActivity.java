package com.example.health_app_sof1021.activity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.health_app_sof1021.utils.SessionManager;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.example.health_app_sof1021.R;
import com.example.health_app_sof1021.dao.BmiDAO;
import com.example.health_app_sof1021.dao.StatisticsDAO;
import com.example.health_app_sof1021.model.BmiRecord;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
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
    StatisticsDAO statisticsDAO;
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
        statisticsDAO = new StatisticsDAO(this);
        SessionManager sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();

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
        boolean hasData = false;

        StatisticsDAO.BmiStatistics bmi = statisticsDAO.getBmiStatisticsByDate(userId, date);
        if (bmi != null) {
            hasData = true;
            tvResultBMI.setText(String.format(Locale.getDefault(),
                    "BMI: %.2f | Cân nặng: %.1f kg | Chiều cao: %.1f cm",
                    bmi.getBmi(), bmi.getWeight(), bmi.getHeight()));
            tvResultBMI.setVisibility(View.VISIBLE);
        } else {
            tvResultBMI.setVisibility(View.GONE);
        }

        int waterSum = statisticsDAO.getWaterAmountByDate(userId, date);
        if (waterSum > 0) {
            hasData = true;
            tvResultWater.setText("Lượng nước đã uống: " + waterSum + " ml");
            tvResultWater.setVisibility(View.VISIBLE);
        } else {
            tvResultWater.setVisibility(View.GONE);
        }

        int caloSum = statisticsDAO.getCaloriesByDate(userId, date);
        if (caloSum > 0) {
            hasData = true;
            tvResultMeals.setText("Tổng lượng Calo bữa ăn: " + caloSum + " kcal");
            tvResultMeals.setVisibility(View.VISIBLE);
        } else {
            tvResultMeals.setVisibility(View.GONE);
        }

        StatisticsDAO.ExerciseStatistics exercise = statisticsDAO.getExerciseStatisticsByDate(userId, date);
        if (exercise.getTotal() > 0) {
            hasData = true;
            tvResultExercises.setText("Bài tập đã hoàn thành: "
                    + exercise.getCompleted() + " / " + exercise.getTotal() + " bài");
            tvResultExercises.setVisibility(View.VISIBLE);
        } else {
            tvResultExercises.setVisibility(View.GONE);
        }

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
        ArrayList<String> dateLabels = new ArrayList<>();
        int recentDays = 5;
        List<Integer> waterAmounts = statisticsDAO.getRecentWaterAmounts(userId, recentDays);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());

        for (int i = recentDays - 1; i >= 0; i--) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -i);
            dateLabels.add(sdf.format(cal.getTime()));
        }

        for (int i = 0; i < waterAmounts.size(); i++) {
            entries.add(new BarEntry(i, waterAmounts.get(i)));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Lượng nước uống (ml)");
        dataSet.setColor(Color.parseColor("#3B82F6"));
        dataSet.setValueTextColor(Color.parseColor("#1F2937"));
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(dateLabels.size());
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index < 0 || index >= dateLabels.size()) {
                    return "";
                }
                return dateLabels.get(index);
            }
        });

        barChart.invalidate();

    }
}
