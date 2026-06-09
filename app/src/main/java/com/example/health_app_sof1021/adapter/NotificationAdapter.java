package com.example.health_app_sof1021.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.health_app_sof1021.R;
import com.example.health_app_sof1021.model.Notification;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<Notification> list;
    private final OnNotificationClickListener listener;
    private final Context context;

    public interface OnNotificationClickListener {
        void onMarkRead(Notification notification);
        void onDelete(Notification notification);
    }

    public NotificationAdapter(Context context, List<Notification> list, OnNotificationClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = list.get(position);
        holder.tvTitle.setText(notification.getTieuDe());
        holder.tvContent.setText(notification.getNoiDung());
        holder.tvDate.setText(notification.getNgayThongBao());

        // Xử lý trạng thái đã đọc/chưa đọc
        if (notification.getDaDoc() == 0) {
            // Chưa đọc: In đậm và màu nền trắng/nổi bật
            holder.tvTitle.setTypeface(null, Typeface.BOLD);
            holder.cardNotification.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));
            holder.btnMarkRead.setVisibility(View.VISIBLE);
        } else {
            // Đã đọc: Trở về màu nguyên bản (surface) và font bình thường
            holder.tvTitle.setTypeface(null, Typeface.NORMAL);
            holder.cardNotification.setCardBackgroundColor(ContextCompat.getColor(context, R.color.surface));
            holder.btnMarkRead.setVisibility(View.GONE);
        }

        holder.btnMarkRead.setOnClickListener(v -> {
            if (listener != null) listener.onMarkRead(notification);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(notification);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateData(List<Notification> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvDate;
        ImageView btnMarkRead, btnDelete;
        CardView cardNotification;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnMarkRead = itemView.findViewById(R.id.btnMarkRead);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            cardNotification = itemView.findViewById(R.id.cardNotification);
        }
    }
}
