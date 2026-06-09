package com.example.health_app_sof1021;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.health_app_sof1021.adapter.ExerciseAdapter;
import com.example.health_app_sof1021.dao.ExerciseDao;
import com.example.health_app_sof1021.model.Exercise;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExerciseActivity extends AppCompatActivity {

    private RecyclerView rvExercises;
    private ExerciseAdapter adapter;
    private ExerciseDao exerciseDao;
    private TextView tvTotalCalories;
    private List<Exercise> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        exerciseDao = new ExerciseDao(this);
        initViews();
        setupRecyclerView();
        loadData();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnAddExercise).setOnClickListener(v -> showAddDialog());
    }

    private void initViews() {
        rvExercises = findViewById(R.id.rvExercises);
        tvTotalCalories = findViewById(R.id.tvTotalCalories);
    }

    private void setupRecyclerView() {
        rvExercises.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadData() {
        list = exerciseDao.getAllExercises();
        
        // Add sample data if empty for demonstration as requested "dữ liệu tạo sẵn"
        if (list.isEmpty()) {
            exerciseDao.insert(new Exercise(0, 1, "Chạy bộ", 30, 300, getCurrentDate()));
            exerciseDao.insert(new Exercise(0, 1, "Đạp xe", 45, 450, getCurrentDate()));
            exerciseDao.insert(new Exercise(0, 1, "Nhảy dây", 15, 200, getCurrentDate()));
            list = exerciseDao.getAllExercises();
        }

        if (adapter == null) {
            adapter = new ExerciseAdapter(list, this::deleteExercise);
            rvExercises.setAdapter(adapter);
        } else {
            adapter.updateData(list);
        }
        updateTotalCalories();
    }

    private void updateTotalCalories() {
        int total = 0;
        for (Exercise ex : list) {
            total += ex.getCalories();
        }
        tvTotalCalories.setText(total + " kcal");
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm bài tập");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_exercise, null);
        EditText etName = view.findViewById(R.id.etExName);
        EditText etDuration = view.findViewById(R.id.etExDuration);
        EditText etCalories = view.findViewById(R.id.etExCalories);

        builder.setView(view);
        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String durationStr = etDuration.getText().toString().trim();
            String caloriesStr = etCalories.getText().toString().trim();

            if (!name.isEmpty() && !durationStr.isEmpty() && !caloriesStr.isEmpty()) {
                Exercise ex = new Exercise();
                ex.setUserId(1); // Default user
                ex.setName(name);
                ex.setDuration(Integer.parseInt(durationStr));
                ex.setCalories(Integer.parseInt(caloriesStr));
                ex.setDate(getCurrentDate());

                if (exerciseDao.insert(ex) > 0) {
                    loadData();
                    Toast.makeText(this, "Đã thêm bài tập", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void deleteExercise(Exercise exercise) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa bài tập")
                .setMessage("Bạn có chắc chắn muốn xóa bài tập này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    if (exerciseDao.delete(exercise.getId())) {
                        loadData();
                        Toast.makeText(this, "Đã xóa", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
    }
}
