package com.example.health_app_sof1021.model;

public class Exercise {
    private int id;
    private int userId;
    private String tenBaiTap;
    private String ngayTap;
    private String gioTap;
    private int trangThai; // 0: chưa tập, 1: đã tập

    public Exercise() {}

    public Exercise(int id, int userId, String tenBaiTap, String ngayTap, String gioTap, int trangThai) {
        this.id = id;
        this.userId = userId;
        this.tenBaiTap = tenBaiTap;
        this.ngayTap = ngayTap;
        this.gioTap = gioTap;
        this.trangThai = trangThai;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTenBaiTap() {
        return tenBaiTap;
    }

    public void setTenBaiTap(String tenBaiTap) {
        this.tenBaiTap = tenBaiTap;
    }

    public String getNgayTap() {
        return ngayTap;
    }

    public void setNgayTap(String ngayTap) {
        this.ngayTap = ngayTap;
    }

    public String getGioTap() {
        return gioTap;
    }

    public void setGioTap(String gioTap) {
        this.gioTap = gioTap;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }
}
