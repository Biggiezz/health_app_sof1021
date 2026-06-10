package com.example.health_app_sof1021.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.health_app_sof1021.R;
import com.example.health_app_sof1021.adapter.ExerciseAdapter;
import com.example.health_app_sof1021.dao.ExerciseDao;
import com.example.health_app_sof1021.dao.NotificationDao;
import com.example.health_app_sof1021.model.Exercise;
import com.example.health_app_sof1021.model.Notification;
import com.example.health_app_sof1021.utils.DateUtils;
import com.example.health_app_sof1021.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExerciseActivity extends AppCompatActivity {

    private ImageView btnBack, btnAddExercise;
    private RecyclerView rvExercises;
    private ExerciseAdapter adapter;
    private ExerciseDao exerciseDao;
    private TextView tvExerciseCount;

    private List<Exercise> list;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        SessionManager sessionManager = new SessionManager(this);
        currentUserId = sessionManager.getUserId();

        if (currentUserId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        exerciseDao = new ExerciseDao(this);
        initUi();
        setupRecyclerView();
        loadData();
        initListener();
    }

    private void initUi() {
        rvExercises = findViewById(R.id.rvExercises);
        tvExerciseCount = findViewById(R.id.tvTotalCalories);
        btnBack = findViewById(R.id.btnBack);
        btnAddExercise = findViewById(R.id.btnAddExercise);
    }

    private void initListener() {
        btnBack.setOnClickListener(v -> finish());
        btnAddExercise.setOnClickListener(v -> showAddDialog());
    }

    private void setupRecyclerView() {
        rvExercises.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadData() {
        list = exerciseDao.getAllExercisesByUserId(currentUserId);

        if (adapter == null) {
            adapter = new ExerciseAdapter(list, new ExerciseAdapter.OnExerciseClickListener() {
                @Override
                public void onDeleteClick(Exercise exercise) {
                    deleteExercise(exercise);
                }

                @Override
                public void onItemClick(Exercise exercise) {
                    showUpdateDialog(exercise);
                }
            });
            rvExercises.setAdapter(adapter);
        } else {
            adapter.updateData(list);
        }
        updateExerciseCount();
    }

    private void updateExerciseCount() {
        int total = list.size();
        tvExerciseCount.setText("Tổng số: " + total + " bài tập");
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lên lịch tập luyện");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_exercise, null);
        EditText etName = view.findViewById(R.id.etExName);
        EditText etDate = view.findViewById(R.id.etExDate);
        EditText etTime = view.findViewById(R.id.etExTime);

        etDate.setOnClickListener(v -> DateUtils.openDatePicker(this, etDate));
        etTime.setOnClickListener(v -> DateUtils.openTimePicker(this, etTime));

        builder.setView(view);
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String time = etTime.getText().toString().trim();

            if (!name.isEmpty() && !date.isEmpty() && !time.isEmpty()) {
                Exercise ex = new Exercise();
                ex.setUserId(currentUserId);
                ex.setTenBaiTap(name);
                ex.setNgayTap(date);
                ex.setGioTap(time);
                ex.setTrangThai(0);

                if (exerciseDao.insert(ex) > 0) {
                    createNotification("Lịch tập mới: " + name,
                            "Bạn có lịch tập " + name + " vào lúc " + time + " ngày " + date);
                    loadData();
                    Toast.makeText(this, "Đã thêm lịch tập", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void showUpdateDialog(Exercise exercise) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cập nhật lịch tập");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_exercise, null);
        EditText etName = view.findViewById(R.id.etExName);
        EditText etDate = view.findViewById(R.id.etExDate);
        EditText etTime = view.findViewById(R.id.etExTime);

        etName.setText(exercise.getTenBaiTap());
        etDate.setText(exercise.getNgayTap());
        etDate.setOnClickListener(v -> DateUtils.openDatePicker(this, etDate));

        etTime.setText(exercise.getGioTap());
        etTime.setOnClickListener(v -> DateUtils.openTimePicker(this, etTime));

        builder.setView(view);
        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String time = etTime.getText().toString().trim();

            if (!name.isEmpty() && !date.isEmpty() && !time.isEmpty()) {
                exercise.setTenBaiTap(name);
                exercise.setNgayTap(date);
                exercise.setGioTap(time);

                if (exerciseDao.update(exercise)) {
                    createNotification("Cập nhật lịch tập: " + name,
                            "Lịch tập " + name + " đã dời sang " + time + " ngày " + date);
                    loadData();
                    Toast.makeText(this, "Đã cập nhật lịch tập", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void createNotification(String title, String content) {
        NotificationDao notifDao = new NotificationDao(this);
        Notification notif = new Notification();
        notif.setMaNguoiDung(currentUserId);
        notif.setTieuDe(title);
        notif.setNoiDung(content);
        notif.setNgayThongBao(new SimpleDateFormat("HH:mm • dd-MM-yyyy", Locale.getDefault()).format(new Date()));
        notif.setDaDoc(0);
        notifDao.insert(notif);
    }

    private void deleteExercise(Exercise exercise) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa lịch tập")
                .setMessage("Bạn có chắc chắn muốn xóa bài tập này khỏi lịch?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    if (exerciseDao.delete(exercise.getId())) {
                        loadData();
                        Toast.makeText(this, "Đã xóa", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
