package com.example.health_app_sof1021.model;

public class User {
    private int userId;
    private String hoTen;
    private String email;
    private String matKhau;
    private String ngayTao;

    public User() {
    }

    public User(int userId, String hoTen, String email, String matKhau, String ngayTao) {
        this.userId = userId;
        this.hoTen = hoTen;
        this.email = email;
        this.matKhau = matKhau;
        this.ngayTao = ngayTao;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(String ngayTao) {
        this.ngayTao = ngayTao;
    }
}
