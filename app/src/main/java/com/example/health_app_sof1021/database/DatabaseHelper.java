package com.example.health_app_sof1021.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "HealthApp.db";
    private static final int DATABASE_VERSION = 7;

    public static final String TABLE_USER = "User";
    public static final String COL_USER_ID = "userId";
    public static final String COL_HO_TEN = "hoTen";
    public static final String COL_EMAIL = "email";
    public static final String COL_MAT_KHAU = "matKhau";
    public static final String COL_NGAY_TAO = "ngayTao";

    public static final String TABLE_MEAL_PLAN = "MealPlan";
    public static final String COL_MEAL_ID = "mealId";
    public static final String COL_MEAL_USER_ID = "userId";
    public static final String COL_TEN_MON = "tenMon";
    public static final String COL_LOAI_BUA = "loaiBua";
    public static final String COL_CALO = "calo";
    public static final String COL_SO_LUONG = "soLuong";
    public static final String COL_NGAY_AN = "ngayAn";

    public static final String TABLE_EXERCISE = "Exercise";
    public static final String COL_EX_ID = "id";
    public static final String COL_EX_USER_ID = "userId";
    public static final String COL_EX_NAME = "tenBaiTap";
    public static final String COL_EX_DATE = "ngayTap";
    public static final String COL_EX_TIME = "gioTap";
    public static final String COL_EX_STATUS = "trangThai";

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
        createNotificationTable(db);
        createBMIRecordTable(db);

        String createTableBMIRecord = "CREATE TABLE BMIRecord (" +
                "BmiID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userId INTEGER, " +
                "chieuCao REAL, " +
                "canNang REAL, " +
                "chiSoBMI REAL, " +
                "ngayDo TEXT, " +
                "FOREIGN KEY(userId) REFERENCES User(UserID))";
        db.execSQL(createTableBMIRecord);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            createMealPlanTable(db);
        }
        if (oldVersion < 4) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISE);
            createExerciseTable(db);
        }
        if (oldVersion < 5) {
            createNotificationTable(db);
        }
        if (oldVersion < 6) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISE);
            createExerciseTable(db);
        }
    }

    private void createMealPlanTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_MEAL_PLAN + " ("
                + COL_MEAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_MEAL_USER_ID + " INTEGER NOT NULL, "
                + COL_TEN_MON + " TEXT NOT NULL, "
                + COL_LOAI_BUA + " TEXT NOT NULL, "
                + COL_CALO + " INTEGER NOT NULL, "
                + COL_SO_LUONG + " INTEGER NOT NULL, "
                + COL_NGAY_AN + " TEXT NOT NULL)";
        db.execSQL(sql);
    }

    private void createBMIRecordTable(SQLiteDatabase db) {
        String createTableBMIRecord = "CREATE TABLE BMIRecord (" +
                "BmiID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userId INTEGER, " +
                "chieuCao REAL, " +
                "canNang REAL, " +
                "chiSoBMI REAL, " +
                "ngayDo TEXT, " +
                "FOREIGN KEY(userId) REFERENCES User(UserID))";
        db.execSQL(createTableBMIRecord);
    }

    private void createExerciseTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_EXERCISE + " ("
                + COL_EX_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_EX_USER_ID + " INTEGER NOT NULL, "
                + COL_EX_NAME + " TEXT NOT NULL, "
                + COL_EX_DATE + " TEXT NOT NULL, "
                + COL_EX_TIME + " TEXT NOT NULL, "
                + COL_EX_STATUS + " INTEGER DEFAULT 0)";
        db.execSQL(sql);
    }

    private void createNotificationTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NOTIFICATION + " ("
                + COL_NOTIF_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NOTIF_USER_ID + " INTEGER NOT NULL, "
                + COL_NOTIF_TITLE + " TEXT NOT NULL, "
                + COL_NOTIF_CONTENT + " TEXT NOT NULL, "
                + COL_NOTIF_DATE + " TEXT NOT NULL, "
                + COL_NOTIF_IS_READ + " INTEGER DEFAULT 0)";
        db.execSQL(sql);
    }
}
