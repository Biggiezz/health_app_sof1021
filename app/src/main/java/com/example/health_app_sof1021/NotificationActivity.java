package com.example.health_app_sof1021;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.health_app_sof1021.adapter.NotificationAdapter;
import com.example.health_app_sof1021.dao.NotificationDao;
import com.example.health_app_sof1021.model.Notification;

import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView rvNotifications;
    private NotificationAdapter adapter;
    private NotificationDao notificationDao;
    private List<Notification> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        notificationDao = new NotificationDao(this);
        initViews();
        loadData();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void initViews() {
        rvNotifications = findViewById(R.id.rvNotifications);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadData() {
        list = notificationDao.getAllNotifications();
        
        adapter = new NotificationAdapter(this, list, new NotificationAdapter.OnNotificationClickListener() {
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

    private void markAsRead(Notification notification) {
        if (notificationDao.updateStatus(notification.getMaThongBao(), 1)) {
            loadData();
            Toast.makeText(this, "Đã đánh dấu là đã đọc", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDelete(Notification notification) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa thông báo")
                .setMessage("Bạn có chắc chắn muốn xóa thông báo này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    if (notificationDao.delete(notification.getMaThongBao())) {
                        loadData();
                        Toast.makeText(this, "Đã xóa thông báo", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
