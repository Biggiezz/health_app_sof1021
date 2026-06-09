package com.example.health_app_sof1021.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "HealthApp.db";
    private static final int DATABASE_VERSION = 5; // Incremented version to 5

    public static final String TABLE_USER = "User";
    public static final String COL_USER_ID = "userId";
    public static final String COL_HO_TEN = "hoTen";
    public static final String COL_EMAIL = "email";
    public static final String COL_MAT_KHAU = "matKhau";
    public static final String COL_NGAY_TAO = "ngayTao";

    /// Tai khoan da dang ky de test: manhphuc3005@gmail.com | 123456

    public static final String TABLE_MEAL_PLAN = "MealPlan";
    public static final String COL_MEAL_ID = "mealId";
    public static final String COL_MEAL_USER_ID = "userId";
    public static final String COL_TEN_MON = "tenMon";
    public static final String COL_LOAI_BUA = "loaiBua";
    public static final String COL_CALO = "calo";
    public static final String COL_SO_LUONG = "soLuong";
    public static final String COL_NGAY_AN = "ngayAn";

    /// Exercise Table
    public static final String TABLE_EXERCISE = "Exercise";
    public static final String COL_EX_ID = "exId";
    public static final String COL_EX_USER_ID = "userId";
    public static final String COL_EX_NAME = "tenBaiTap";
    public static final String COL_EX_DURATION = "thoiGian";
    public static final String COL_EX_CALORIES = "caloTieuThu";
    public static final String COL_EX_DATE = "ngayTap";

    /// Notification Table
    public static final String TABLE_NOTIFICATION = "Notification";
    public static final String COL_NOTIF_ID = "maThongBao";
    public static final String COL_NOTIF_USER_ID = "userId";
    public static final String COL_NOTIF_TITLE = "tieuDe";
    public static final String COL_NOTIF_CONTENT = "noiDung";
    public static final String COL_NOTIF_DATE = "ngayThongBao";
    public static final String COL_NOTIF_IS_READ = "daDoc";

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
        createMealPlanTable(db);
        createExerciseTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            createMealPlanTable(db);
        }
        if (oldVersion < 4) {
            createExerciseTable(db);
        }
        if (oldVersion < 5) {
            createNotificationTable(db);
        }
    }

    private void createMealPlanTable(SQLiteDatabase db) {
        String createMealPlanTable = "CREATE TABLE IF NOT EXISTS " + TABLE_MEAL_PLAN + " ("
                + COL_MEAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_MEAL_USER_ID + " INTEGER DEFAULT 1, "
                + COL_TEN_MON + " TEXT NOT NULL, "
                + COL_LOAI_BUA + " TEXT NOT NULL, "
                + COL_CALO + " INTEGER NOT NULL, "
                + COL_SO_LUONG + " INTEGER NOT NULL, "
                + COL_NGAY_AN + " TEXT NOT NULL)";
        db.execSQL(createMealPlanTable);
    }

    private void createExerciseTable(SQLiteDatabase db) {
        String createExerciseTable = "CREATE TABLE IF NOT EXISTS " + TABLE_EXERCISE + " ("
                + COL_EX_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_EX_USER_ID + " INTEGER DEFAULT 1, "
                + COL_EX_NAME + " TEXT NOT NULL, "
                + COL_EX_DURATION + " INTEGER NOT NULL, "
                + COL_EX_CALORIES + " INTEGER NOT NULL, "
                + COL_EX_DATE + " TEXT NOT NULL)";
        db.execSQL(createExerciseTable);
    }

    private void createNotificationTable(SQLiteDatabase db) {
        String createNotifTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NOTIFICATION + " ("
                + COL_NOTIF_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NOTIF_USER_ID + " INTEGER DEFAULT 1, "
                + COL_NOTIF_TITLE + " TEXT NOT NULL, "
                + COL_NOTIF_CONTENT + " TEXT NOT NULL, "
                + COL_NOTIF_DATE + " TEXT NOT NULL, "
                + COL_NOTIF_IS_READ + " INTEGER DEFAULT 0)";
        db.execSQL(createNotifTable);
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

    public boolean updatePasswordByEmail(String email, String matKhauMoi) {
        if (!isEmailExists(email)) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_MAT_KHAU, matKhauMoi);

        int result = db.update(
                TABLE_USER,
                values,
                COL_EMAIL + " = ?",
                new String[]{email}
        );
        return result > 0;
    }
}
