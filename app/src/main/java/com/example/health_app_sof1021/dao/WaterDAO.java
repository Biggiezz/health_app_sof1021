package com.example.health_app_sof1021.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.health_app_sof1021.database.DatabaseHelper;

public class WaterDAO {
    private SQLiteDatabase db;

    public WaterDAO(Context context){
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public int getWaterIntake(int userId, String date)  {
        int total = 0;
        Cursor cursor = db.rawQuery("SELECT * FROM HealthRecord WHERE userId = ? AND ngayGhiNhan = ?",
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
}
