package com.example.health_app_sof1021.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.health_app_sof1021.database.DatabaseHelper;
import com.example.health_app_sof1021.model.BmiRecord;

import java.util.ArrayList;
import java.util.List;


public class BmiDAO {
    private SQLiteDatabase db;

    public BmiDAO(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public long insertBMI(BmiRecord record) {
        ContentValues values = new ContentValues();
        values.put("userId", record.getUserId());
        values.put("chieuCao", record.getChieuCao());
        values.put("canNang", record.getCanNang());
        values.put("chiSoBMI", record.getChiSoBMI());
        values.put("ngayDo", record.getNgayDo());
        return db.insert("BMIRecord", null, values);
    }

    public List<BmiRecord> getHistory(int userId) {
        List<BmiRecord> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM BMIRecord WHERE userId = ? ORDER BY BmiID DESC", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            do {
                BmiRecord r = new BmiRecord();
                r.setBmiId(cursor.getInt(0));
                r.setUserId(cursor.getInt(1));
                r.setChieuCao(cursor.getDouble(2));
                r.setCanNang(cursor.getDouble(3));
                r.setChiSoBMI(cursor.getDouble(4));
                r.setNgayDo(cursor.getString(5));
                list.add(r);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}
