package com.example.health_app_sof1021.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.health_app_sof1021.database.DatabaseHelper;
import com.example.health_app_sof1021.utils.SessionManager;

public class WaterDAO {
    private static final String WATER_REMINDER_TYPE = "Uống nước";

    private final Context context;
    private final SQLiteDatabase db;

    public WaterDAO(Context context){
        this.context = context;
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public int getWaterIntake(int userId, String date)  {
        int total = 0;
        Cursor cursor = db.rawQuery("SELECT SUM(luongNuoc) FROM HealthRecord WHERE userId = ? AND ngayGhiNhan = ?",
                new String[]{String.valueOf(userId), date});
        if(cursor.moveToFirst()){
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }

    public long addWaterIntake(int userId, int ml, String date) {
        ContentValues values = new ContentValues();
        values.put("userId", userId);
        values.put("luongNuoc", ml);
        values.put("ngayGhiNhan", date);
        return db.insert("HealthRecord", null, values);
    }

    public WaterReminderSettings loadTargetAndReminder(int userId) {
        int targetAmount = getTargetAmount(userId);
        boolean reminderOn = false;
        String interval = "1 giờ";

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_REMINDER,
                null,
                DatabaseHelper.COL_REMINDER_USER_ID + " = ? AND " + DatabaseHelper.COL_REMINDER_TYPE + " = ?",
                new String[]{String.valueOf(userId), WATER_REMINDER_TYPE},
                null,
                null,
                null,
                "1"
        );

        if (cursor.moveToFirst()) {
            reminderOn = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_REMINDER_STATUS)) == 1;
            interval = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_REMINDER_TIME));
        }
        cursor.close();

        return new WaterReminderSettings(targetAmount, reminderOn, interval);
    }

    public boolean saveReminderSettings(int userId, boolean isOn, String interval) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_REMINDER_USER_ID, userId);
        values.put(DatabaseHelper.COL_REMINDER_TYPE, WATER_REMINDER_TYPE);
        values.put(DatabaseHelper.COL_REMINDER_TIME, interval);
        values.put(DatabaseHelper.COL_REMINDER_STATUS, isOn ? 1 : 0);

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_REMINDER,
                new String[]{DatabaseHelper.COL_REMINDER_ID},
                DatabaseHelper.COL_REMINDER_USER_ID + " = ? AND " + DatabaseHelper.COL_REMINDER_TYPE + " = ?",
                new String[]{String.valueOf(userId), WATER_REMINDER_TYPE},
                null,
                null,
                null
        );
        boolean exists = cursor.moveToFirst();
        cursor.close();

        if (exists) {
            return db.update(
                    DatabaseHelper.TABLE_REMINDER,
                    values,
                    DatabaseHelper.COL_REMINDER_USER_ID + " = ? AND " + DatabaseHelper.COL_REMINDER_TYPE + " = ?",
                    new String[]{String.valueOf(userId), WATER_REMINDER_TYPE}
            ) > 0;
        }

        return db.insert(DatabaseHelper.TABLE_REMINDER, null, values) > 0;
    }

    private int getTargetAmount(int userId) {
        SessionManager sessionManager = new SessionManager(context);
        return sessionManager.getGoalWater();
    }

    public static class WaterReminderSettings {
        private final int targetAmount;
        private final boolean reminderOn;
        private final String interval;

        public WaterReminderSettings(int targetAmount, boolean reminderOn, String interval) {
            this.targetAmount = targetAmount;
            this.reminderOn = reminderOn;
            this.interval = interval;
        }

        public int getTargetAmount() {
            return targetAmount;
        }

        public boolean isReminderOn() {
            return reminderOn;
        }

        public String getInterval() {
            return interval;
        }
    }
}
