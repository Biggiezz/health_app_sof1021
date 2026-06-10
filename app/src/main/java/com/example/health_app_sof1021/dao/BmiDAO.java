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
    private final DatabaseHelper dbHelper;

    public BmiDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long insertBMI(BmiRecord record) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_BMI_USER_ID, record.getUserId());
        values.put(DatabaseHelper.COL_CHIEU_CAO, record.getChieuCao());
        values.put(DatabaseHelper.COL_CAN_NANG, record.getCanNang());
        values.put(DatabaseHelper.COL_CHI_SO_BMI, record.getChiSoBMI());
        values.put(DatabaseHelper.COL_NGAY_DO, record.getNgayDo());
        return db.insert(DatabaseHelper.TABLE_BMI_RECORD, null, values);
    }

    public List<BmiRecord> getAllHistoryByUserId(int userId) {
        List<BmiRecord> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(DatabaseHelper.TABLE_BMI_RECORD, null,
                DatabaseHelper.COL_BMI_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}, null, null,
                DatabaseHelper.COL_BMI_ID + " DESC");

        if (cursor.moveToFirst()) {
            do {
                BmiRecord r = new BmiRecord();
                r.setBmiId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BMI_ID)));
                r.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BMI_USER_ID)));
                r.setChieuCao(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CHIEU_CAO)));
                r.setCanNang(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAN_NANG)));
                r.setChiSoBMI(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CHI_SO_BMI)));
                r.setNgayDo(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NGAY_DO)));
                list.add(r);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /**
     * Lấy bản ghi BMI mới nhất của người dùng (chứa chiều cao và cân nặng gần nhất)
     */
    public BmiRecord getBMIByUserId(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_BMI_RECORD,
                null,
                DatabaseHelper.COL_BMI_USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                DatabaseHelper.COL_BMI_ID + " DESC",
                "1"
        );

        BmiRecord bmi = null;
        if (cursor.moveToFirst()) {
            bmi = new BmiRecord();
            bmi.setBmiId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BMI_ID)));
            bmi.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BMI_USER_ID)));
            bmi.setChieuCao(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CHIEU_CAO)));
            bmi.setCanNang(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CAN_NANG)));
            bmi.setChiSoBMI(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CHI_SO_BMI)));
            bmi.setNgayDo(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NGAY_DO)));
        }

        cursor.close();
        return bmi;
    }
}
