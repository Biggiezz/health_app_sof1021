package com.example.health_app_sof1021.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.health_app_sof1021.database.DatabaseHelper;
import com.example.health_app_sof1021.model.User;

public class UserDAO {
    private final DatabaseHelper dbHelper;

    public UserDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public boolean insert(User user) {
        if (isEmailExists(user.getEmail())) {
            return false;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_HO_TEN, user.getHoTen());
        values.put(DatabaseHelper.COL_EMAIL, user.getEmail());
        values.put(DatabaseHelper.COL_MAT_KHAU, user.getMatKhau());

        long result = db.insert(DatabaseHelper.TABLE_USER, null, values);
        return result != -1;
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USER,
                new String[]{DatabaseHelper.COL_USER_ID},
                DatabaseHelper.COL_EMAIL + " = ?",
                new String[]{email},
                null,
                null,
                null
        );

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean checkLogin(String email, String matKhau) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USER,
                new String[]{DatabaseHelper.COL_USER_ID},
                DatabaseHelper.COL_EMAIL + " = ? AND " + DatabaseHelper.COL_MAT_KHAU + " = ?",
                new String[]{email, matKhau},
                null,
                null,
                null
        );

        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }

    public int getUserIdByEmail(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USER,
                new String[]{DatabaseHelper.COL_USER_ID},
                DatabaseHelper.COL_EMAIL + " = ?",
                new String[]{email},
                null,
                null,
                null
        );

        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID));
        }

        cursor.close();
        return userId;
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USER,
                null,
                DatabaseHelper.COL_USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                null
        );

        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID)));
            user.setHoTen(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_HO_TEN)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EMAIL)));
            user.setMatKhau(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MAT_KHAU)));
            user.setNgayTao(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NGAY_TAO)));
        }

        cursor.close();
        return user;
    }

    public boolean updatePasswordByEmail(String email, String matKhauMoi) {
        if (!isEmailExists(email)) {
            return false;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_MAT_KHAU, matKhauMoi);

        int result = db.update(
                DatabaseHelper.TABLE_USER,
                values,
                DatabaseHelper.COL_EMAIL + " = ?",
                new String[]{email}
        );
        return result > 0;
    }

    public boolean updatePasswordByUserId(int userId, String matKhauMoi) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_MAT_KHAU, matKhauMoi);

        int result = db.update(
                DatabaseHelper.TABLE_USER,
                values,
                DatabaseHelper.COL_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}
        );
        return result > 0;
    }
}
