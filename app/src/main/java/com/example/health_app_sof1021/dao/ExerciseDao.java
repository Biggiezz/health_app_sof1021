package com.example.health_app_sof1021.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.health_app_sof1021.database.DatabaseHelper;
import com.example.health_app_sof1021.model.Exercise;

import java.util.ArrayList;
import java.util.List;

public class ExerciseDao {
    private final DatabaseHelper dbHelper;

    public ExerciseDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long insert(Exercise exercise) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_EX_USER_ID, exercise.getUserId());
        values.put(DatabaseHelper.COL_EX_NAME, exercise.getTenBaiTap());
        values.put(DatabaseHelper.COL_EX_DATE, exercise.getNgayTap());
        values.put(DatabaseHelper.COL_EX_TIME, exercise.getGioTap());
        values.put(DatabaseHelper.COL_EX_STATUS, exercise.getTrangThai());
        return db.insert(DatabaseHelper.TABLE_EXERCISE, null, values);
    }

    public List<Exercise> getAllExercisesByUserId(int userId) {
        List<Exercise> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_EXERCISE, null, 
                DatabaseHelper.COL_EX_USER_ID + " = ?", 
                new String[]{String.valueOf(userId)}, 
                null, null, DatabaseHelper.COL_EX_DATE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Exercise ex = new Exercise();
                ex.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EX_ID)));
                ex.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EX_USER_ID)));
                ex.setTenBaiTap(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EX_NAME)));
                ex.setNgayTap(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EX_DATE)));
                ex.setGioTap(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EX_TIME)));
                ex.setTrangThai(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EX_STATUS)));
                list.add(ex);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public boolean update(Exercise exercise) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_EX_NAME, exercise.getTenBaiTap());
        values.put(DatabaseHelper.COL_EX_DATE, exercise.getNgayTap());
        values.put(DatabaseHelper.COL_EX_TIME, exercise.getGioTap());
        values.put(DatabaseHelper.COL_EX_STATUS, exercise.getTrangThai());
        return db.update(DatabaseHelper.TABLE_EXERCISE, values, DatabaseHelper.COL_EX_ID + " = ?",
                new String[]{String.valueOf(exercise.getId())}) > 0;
    }

    public boolean updateStatus(int id, int status) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_EX_STATUS, status);
        return db.update(DatabaseHelper.TABLE_EXERCISE, values, DatabaseHelper.COL_EX_ID + " = ?",
                new String[]{String.valueOf(id)}) > 0;
    }

    public boolean delete(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DatabaseHelper.TABLE_EXERCISE, DatabaseHelper.COL_EX_ID + " = ?",
                new String[]{String.valueOf(id)}) > 0;
    }
}
