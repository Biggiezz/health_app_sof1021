package com.example.health_app_sof1021.model;

public class Exercise {
    private int id;
    private int userId;
    private String name;
    private int duration; // in minutes
    private int calories;
    private String date;

    public Exercise() {}

    public Exercise(int id, int userId, String name, int duration, int calories, String date) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.duration = duration;
        this.calories = calories;
        this.date = date;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
