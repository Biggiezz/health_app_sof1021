package com.example.health_app_sof1021.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "HealthAppSession";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_GOAL_CALORIES = "goalCalories";
    private static final String KEY_GOAL_WATER = "goalWater";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;
    private final Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(int userId) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    public void setGoalCalories(int calories) {
        editor.putInt(KEY_GOAL_CALORIES, calories);
        editor.apply();
    }

    public int getGoalCalories() {
        return pref.getInt(KEY_GOAL_CALORIES, 2200); // Default 2200
    }

    public void setGoalWater(int ml) {
        editor.putInt(KEY_GOAL_WATER, ml);
        editor.apply();
    }

    public int getGoalWater() {
        return pref.getInt(KEY_GOAL_WATER, 2000); // Default 2000ml
    }

    public void logoutUser() {
        editor.clear();
        editor.apply();
    }
}
