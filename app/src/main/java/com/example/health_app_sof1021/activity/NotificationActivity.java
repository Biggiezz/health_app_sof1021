package com.example.health_app_sof1021.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.health_app_sof1021.R;
import com.example.health_app_sof1021.adapter.NotificationAdapter;
import com.example.health_app_sof1021.dao.ExerciseDao;
import com.example.health_app_sof1021.dao.NotificationDao;
import com.example.health_app_sof1021.model.Notification;
import com.example.health_app_sof1021.utils.SessionManager;

import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView rvNotifications;
    private TextView tvEmptyState;
    private NotificationDao notificationDao;
    private ExerciseDao exerciseDao;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        SessionManager sessionManager = new SessionManager(this);
        currentUserId = sessionManager.getUserId();

        notificationDao = new NotificationDao(this);
        exerciseDao = new ExerciseDao(this);
        initUi();
        loadData();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void initUi() {
        rvNotifications = findViewById(R.id.rvNotifications);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadData() {
        // Lấy thông báo theo userId của người dùng hiện tại
        List<Notification> list = notificationDao.getAllByUserId(currentUserId);

        if (list.isEmpty()) {
            rvNotifications.setVisibility(View.GONE);
            tvEmptyState.setVisibility(View.VISIBLE);
        } else {
            rvNotifications.setVisibility(View.VISIBLE);
            tvEmptyState.setVisibility(View.GONE);

            NotificationAdapter adapter = new NotificationAdapter(this, list,
                    new NotificationAdapter.OnNotificationClickListener() {
                        @Override
                        public void onMarkRead(Notification notification) {
                            markAsRead(notification);
                        }

                        @Override
                        public void onDelete(Notification notification) {
                            confirmDelete(notification);
                        }
                    });
            rvNotifications.setAdapter(adapter);
        }
    }

    private void markAsRead(Notification notification) {
        if (notificationDao.updateStatus(notification.getMaThongBao(), 1)) {
            markExerciseCompleted(notification);
            loadData();
            Toast.makeText(this, "Đã đọc", Toast.LENGTH_SHORT).show();
        }
    }

    private void markExerciseCompleted(Notification notification) {
        String title = notification.getTieuDe();
        String content = notification.getNoiDung();
        if (title == null || content == null || !title.toLowerCase().contains("lịch tập")) {
            return;
        }

        String exerciseName = getExerciseNameFromTitle(title);
        String exerciseDate = getExerciseDateFromContent(content);
        if (!exerciseName.isEmpty() && !exerciseDate.isEmpty()) {
            exerciseDao.markCompletedByNameAndDate(currentUserId, exerciseName, exerciseDate);
        }
    }

    private String getExerciseNameFromTitle(String title) {
        int index = title.indexOf(":");
        if (index == -1 || index + 1 >= title.length()) {
            return "";
        }
        return title.substring(index + 1).trim();
    }

    private String getExerciseDateFromContent(String content) {
        String keyword = " ngày ";
        int index = content.lastIndexOf(keyword);
        if (index == -1) {
            return "";
        }
        return content.substring(index + keyword.length()).trim();
    }

    private void confirmDelete(Notification notification) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa thông báo")
                .setMessage("Bạn có chắc chắn muốn xóa thông báo này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    if (notificationDao.delete(notification.getMaThongBao())) {
                        loadData();
                        Toast.makeText(this, "Đã xóa", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
