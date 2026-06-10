package com.example.health_app_sof1021.utils;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static void openDatePicker(Context context, TextView textView) {
        Calendar calendar = Calendar.getInstance();
        String currentText = textView.getText().toString().trim();
        if (!currentText.isEmpty()) {
            Date parsedDate = parseDate(currentText);
            if (parsedDate != null) {
                calendar.setTime(parsedDate);
            }
        }
        new DatePickerDialog(
                context,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    textView.setText(formatDate(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    public static Date parseDate(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String normalized = value.trim().replace('/', '-');
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            simpleDateFormat.setLenient(false);
            return simpleDateFormat.parse(normalized);
        } catch (ParseException exception) {
            return null;
        }
    }

    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        return simpleDateFormat.format(date);
    }

    public static void openTimePicker(Context context, TextView textView) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        String currentText = textView.getText().toString().trim();
        if (!currentText.isEmpty() && currentText.contains(":")) {
            try {
                String[] parts = currentText.split(":");
                hour = Integer.parseInt(parts[0]);
                minute = Integer.parseInt(parts[1]);
            } catch (Exception ignored) {
            }
        }

        new TimePickerDialog(context, (view, selectedHour, selectedMinute) -> {
            String time = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
            textView.setText(time);
        }, hour, minute, true).show();
    }
}

