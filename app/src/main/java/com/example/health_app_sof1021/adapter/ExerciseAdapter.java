package com.example.health_app_sof1021.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.health_app_sof1021.R;
import com.example.health_app_sof1021.model.Exercise;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {

    private List<Exercise> exerciseList;
    private final OnExerciseClickListener listener;

    public interface OnExerciseClickListener {
        void onDeleteClick(Exercise exercise);
        void onItemClick(Exercise exercise);
    }

    public ExerciseAdapter(List<Exercise> exerciseList, OnExerciseClickListener listener) {
        this.exerciseList = exerciseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercise exercise = exerciseList.get(position);
        holder.tvName.setText(exercise.getTenBaiTap());
        holder.tvDetail.setText(exercise.getNgayTap() + " • " + exercise.getGioTap());
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(exercise);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(exercise);
            }
        });
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    public void updateData(List<Exercise> newList) {
        this.exerciseList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDetail;
        ImageView btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvExerciseName);
            tvDetail = itemView.findViewById(R.id.tvExerciseDetail);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
