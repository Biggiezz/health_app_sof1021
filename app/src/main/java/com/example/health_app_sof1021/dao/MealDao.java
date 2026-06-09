package com.example.health_app_sof1021.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.health_app_sof1021.database.DatabaseHelper;
import com.example.health_app_sof1021.model.Meal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MealDao {
    private final DatabaseHelper databaseHelper;

    public MealDao(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public Map<String, Integer> getDanhSachCaloMonAn() {
        Map<String, Integer> meals = new LinkedHashMap<>();
        meals.put("Cơm trắng", 130);
        meals.put("Ức gà luộc", 165);
        meals.put("Trứng gà", 78);
        meals.put("Cá hồi", 208);
        meals.put("Thịt bò", 250);
        meals.put("Rau luộc", 55);
        meals.put("Chuối", 89);
        meals.put("Sữa tươi", 60);
        meals.put("Bánh mì", 265);
        meals.put("Yến mạch", 389);
        return meals;
    }

    public int getCaloTheoMon(String tenMon) {
        Integer calo = getDanhSachCaloMonAn().get(tenMon);
        if (calo == null) {
            return 0;
        }
        return calo;
    }

    public boolean themMonAn(String tenMon, String loaiBua, int soLuong, String ngayAn) {
        int calo = getCaloTheoMon(tenMon);
        if (calo <= 0 || soLuong <= 0 || ngayAn.isEmpty()) {
            return false;
        }

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_MEAL_USER_ID, 1);
        values.put(DatabaseHelper.COL_TEN_MON, tenMon);
        values.put(DatabaseHelper.COL_LOAI_BUA, loaiBua);
        values.put(DatabaseHelper.COL_CALO, calo);
        values.put(DatabaseHelper.COL_SO_LUONG, soLuong);
        values.put(DatabaseHelper.COL_NGAY_AN, ngayAn);

        long result = db.insert(DatabaseHelper.TABLE_MEAL_PLAN, null, values);
        return result != -1;
    }

    public List<Meal> getDanhSachTheoNgay(String ngayAn) {
        List<Meal> meals = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_MEAL_PLAN,
                null,
                DatabaseHelper.COL_NGAY_AN + " = ?",
                new String[]{ngayAn},
                null,
                null,
                DatabaseHelper.COL_MEAL_ID + " DESC"
        );

        while (cursor.moveToNext()) {
            meals.add(new Meal(
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MEAL_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TEN_MON)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_LOAI_BUA)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CALO)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SO_LUONG)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NGAY_AN))
            ));
        }

        cursor.close();
        return meals;
    }

    public int getTongCaloTheoNgay(String ngayAn) {
        int tongCalo = 0;
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + DatabaseHelper.COL_CALO + " * " + DatabaseHelper.COL_SO_LUONG + ") FROM "
                        + DatabaseHelper.TABLE_MEAL_PLAN
                        + " WHERE " + DatabaseHelper.COL_NGAY_AN + " = ?",
                new String[]{ngayAn}
        );

        if (cursor.moveToFirst()) {
            tongCalo = cursor.getInt(0);
        }

        cursor.close();
        return tongCalo;
    }

    public boolean xoaMonAn(int mealId) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int result = db.delete(
                DatabaseHelper.TABLE_MEAL_PLAN,
                DatabaseHelper.COL_MEAL_ID + " = ?",
                new String[]{String.valueOf(mealId)}
        );
        return result > 0;
    }

    public boolean capNhatSoLuong(int mealId, int soLuong) {
        if (soLuong <= 0) {
            return false;
        }

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_SO_LUONG, soLuong);
        int result = db.update(
                DatabaseHelper.TABLE_MEAL_PLAN,
                values,
                DatabaseHelper.COL_MEAL_ID + " = ?",
                new String[]{String.valueOf(mealId)}
        );
        return result > 0;
    }
}
