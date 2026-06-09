package com.example.health_app_sof1021.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "HealthApp.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_USER = "User";
    public static final String COL_USER_ID = "userId";
    public static final String COL_HO_TEN = "hoTen";
    public static final String COL_EMAIL = "email";
    public static final String COL_MAT_KHAU = "matKhau";
    public static final String COL_NGAY_TAO = "ngayTao";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTable = "CREATE TABLE " + TABLE_USER + " ("
                + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_HO_TEN + " TEXT NOT NULL, "
                + COL_EMAIL + " TEXT UNIQUE NOT NULL, "
                + COL_MAT_KHAU + " TEXT NOT NULL, "
                + COL_NGAY_TAO + " TEXT DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(createUserTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    public boolean insertUser(String hoTen, String email, String matKhau) {
        if (isEmailExists(email)) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_HO_TEN, hoTen);
        values.put(COL_EMAIL, email);
        values.put(COL_MAT_KHAU, matKhau);

        long result = db.insert(TABLE_USER, null, values);
        return result != -1;
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_USER,
                new String[]{COL_USER_ID},
                COL_EMAIL + " = ?",
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
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_USER,
                new String[]{COL_USER_ID},
                COL_EMAIL + " = ? AND " + COL_MAT_KHAU + " = ?",
                new String[]{email, matKhau},
                null,
                null,
                null
        );

        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }
}
