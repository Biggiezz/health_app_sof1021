package com.example.health_app_sof1021.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.health_app_sof1021.R;
import com.example.health_app_sof1021.model.BmiRecord;

import java.util.List;
import java.util.Locale;

public class BmiAdapter extends RecyclerView.Adapter<BmiAdapter.BmiViewHolder> {

    private List<BmiRecord> list;

    public BmiAdapter(List<BmiRecord> list) {
        this.list = list;
    }

    @Override
    public BmiViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bmi, parent, false);
       return new BmiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BmiViewHolder holder, int position) {
        BmiRecord bmiRecord = list.get(position);
        holder.tvDate.setText("Ngày: " + bmiRecord.getNgayDo());
        holder.tvBmiValue.setText(String.format(Locale.getDefault(), "BMI: %.2f", bmiRecord.getChiSoBMI()));
        holder.tvWeightHeight.setText(String.format(Locale.getDefault(), "Cân nặng: %.1fkg - Chiều cao: %.1fcm", bmiRecord.getCanNang(), bmiRecord.getChieuCao()));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class BmiViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        TextView tvDate, tvBmiValue,tvWeightHeight;
        public BmiViewHolder(android.view.View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvBmiValue = itemView.findViewById(R.id.tvBmiValue);
            tvWeightHeight = itemView.findViewById(R.id.tvWeightHeight);
        }
    }

}
