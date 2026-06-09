package com.example.health_app_sof1021.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.health_app_sof1021.database.DatabaseHelper;
import com.example.health_app_sof1021.model.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationDao {
    private final DatabaseHelper dbHelper;

    public NotificationDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long insert(Notification notification) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_NOTIF_USER_ID, notification.getMaNguoiDung());
        values.put(DatabaseHelper.COL_NOTIF_TITLE, notification.getTieuDe());
        values.put(DatabaseHelper.COL_NOTIF_CONTENT, notification.getNoiDung());
        values.put(DatabaseHelper.COL_NOTIF_DATE, notification.getNgayThongBao());
        values.put(DatabaseHelper.COL_NOTIF_IS_READ, notification.getDaDoc());
        return db.insert(DatabaseHelper.TABLE_NOTIFICATION, null, values);
    }

    public List<Notification> getAllNotifications() {
        List<Notification> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_NOTIFICATION, null, null, null, null, null, DatabaseHelper.COL_NOTIF_ID + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Notification notif = new Notification();
                notif.setMaThongBao(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NOTIF_ID)));
                notif.setMaNguoiDung(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NOTIF_USER_ID)));
                notif.setTieuDe(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NOTIF_TITLE)));
                notif.setNoiDung(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NOTIF_CONTENT)));
                notif.setNgayThongBao(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NOTIF_DATE)));
                notif.setDaDoc(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NOTIF_IS_READ)));
                list.add(notif);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public boolean updateStatus(int id, int status) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_NOTIF_IS_READ, status);
        return db.update(DatabaseHelper.TABLE_NOTIFICATION, values, DatabaseHelper.COL_NOTIF_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean delete(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DatabaseHelper.TABLE_NOTIFICATION, DatabaseHelper.COL_NOTIF_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }
}
