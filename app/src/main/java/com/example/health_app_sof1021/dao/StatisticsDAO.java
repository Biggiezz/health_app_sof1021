package com.example.health_app_sof1021.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.health_app_sof1021.database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class StatisticsDAO {
    private final DatabaseHelper dbHelper;

    public StatisticsDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public DailyHealthData getDailyHealthData(int userId, String date) {
        BmiStatistics bmiStatistics = getBmiStatisticsByDate(userId, date);
        int waterAmount = getWaterAmountByDate(userId, date);
        int calories = getCaloriesByDate(userId, date);
        ExerciseStatistics exerciseStatistics = getExerciseStatisticsByDate(userId, date);

        return new DailyHealthData(bmiStatistics, waterAmount, calories, exerciseStatistics);
    }

    public BmiStatistics getBmiStatisticsByDate(int userId, String date) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_BMI_RECORD,
                new String[]{
                        DatabaseHelper.COL_CAN_NANG,
                        DatabaseHelper.COL_CHIEU_CAO,
                        DatabaseHelper.COL_CHI_SO_BMI
                },
                DatabaseHelper.COL_BMI_USER_ID + " = ? AND " + DatabaseHelper.COL_NGAY_DO + " = ?",
                new String[]{String.valueOf(userId), date},
                null,
                null,
                DatabaseHelper.COL_BMI_ID + " DESC",
                "1"
        );

        BmiStatistics bmiStatistics = null;
        if (cursor.moveToFirst()) {
            bmiStatistics = new BmiStatistics(
                    cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAN_NANG)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CHIEU_CAO)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CHI_SO_BMI))
            );
        }
        cursor.close();
        return bmiStatistics;
    }

    public int getWaterAmountByDate(int userId, String date) {
        return getSum(
                DatabaseHelper.TABLE_HEALTH_RECORD,
                DatabaseHelper.COL_LUONG_NUOC,
                DatabaseHelper.COL_HEALTH_USER_ID,
                DatabaseHelper.COL_NGAY_GHI_NHAN,
                userId,
                date
        );
    }

    public int getCaloriesByDate(int userId, String date) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + DatabaseHelper.COL_CALO + " * " + DatabaseHelper.COL_SO_LUONG + ") FROM "
                        + DatabaseHelper.TABLE_MEAL_PLAN
                        + " WHERE " + DatabaseHelper.COL_MEAL_USER_ID + " = ? AND "
                        + DatabaseHelper.COL_NGAY_AN + " = ?",
                new String[]{String.valueOf(userId), date}
        );

        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }

    public ExerciseStatistics getExerciseStatisticsByDate(int userId, String date) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*), SUM(CASE WHEN " + DatabaseHelper.COL_EX_STATUS + " = 1 THEN 1 ELSE 0 END) FROM "
                        + DatabaseHelper.TABLE_EXERCISE
                        + " WHERE " + DatabaseHelper.COL_EX_USER_ID + " = ? AND "
                        + DatabaseHelper.COL_EX_DATE + " = ?",
                new String[]{String.valueOf(userId), date}
        );

        int total = 0;
        int completed = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
            completed = cursor.getInt(1);
        }
        cursor.close();
        return new ExerciseStatistics(total, completed);
    }

    public List<Integer> getRecentWaterAmounts(int userId, int days) {
        List<Integer> waterAmounts = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        for (int i = days - 1; i >= 0; i--) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -i);
            String date = sdf.format(cal.getTime());
            waterAmounts.add(getWaterAmountByDate(userId, date));
        }

        return waterAmounts;
    }

    private int getSum(String tableName, String sumColumn, String userIdColumn,
                       String dateColumn, int userId, String date) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + sumColumn + ") FROM " + tableName
                        + " WHERE " + userIdColumn + " = ? AND " + dateColumn + " = ?",
                new String[]{String.valueOf(userId), date}
        );

        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }

    public static class DailyHealthData {
        private final BmiStatistics bmiStatistics;
        private final int waterAmount;
        private final int calories;
        private final ExerciseStatistics exerciseStatistics;

        public DailyHealthData(BmiStatistics bmiStatistics, int waterAmount,
                               int calories, ExerciseStatistics exerciseStatistics) {
            this.bmiStatistics = bmiStatistics;
            this.waterAmount = waterAmount;
            this.calories = calories;
            this.exerciseStatistics = exerciseStatistics;
        }

        public BmiStatistics getBmiStatistics() {
            return bmiStatistics;
        }

        public int getWaterAmount() {
            return waterAmount;
        }

        public int getCalories() {
            return calories;
        }

        public ExerciseStatistics getExerciseStatistics() {
            return exerciseStatistics;
        }

        public boolean hasData() {
            return bmiStatistics != null
                    || waterAmount > 0
                    || calories > 0
                    || exerciseStatistics.getTotal() > 0;
        }
    }

    public static class BmiStatistics {
        private final double weight;
        private final double height;
        private final double bmi;

        public BmiStatistics(double weight, double height, double bmi) {
            this.weight = weight;
            this.height = height;
            this.bmi = bmi;
        }

        public double getWeight() {
            return weight;
        }

        public double getHeight() {
            return height;
        }

        public double getBmi() {
            return bmi;
        }
    }

    public static class ExerciseStatistics {
        private final int total;
        private final int completed;

        public ExerciseStatistics(int total, int completed) {
            this.total = total;
            this.completed = completed;
        }

        public int getTotal() {
            return total;
        }

        public int getCompleted() {
            return completed;
        }
    }
}
