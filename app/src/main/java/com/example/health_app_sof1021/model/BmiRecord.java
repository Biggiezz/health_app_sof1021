package com.example.health_app_sof1021.model;

public class BmiRecord {
    private int bmiId;
    private int userId;
    private double chieuCao;
    private double canNang;
    private double chiSoBMI;
    private String ngayDo;

    public BmiRecord() {
    }

    public int getBmiId() {
        return bmiId;
    }

    public void setBmiId(int bmiId) {
        this.bmiId = bmiId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getChieuCao() {
        return chieuCao;
    }

    public void setChieuCao(double chieuCao) {
        this.chieuCao = chieuCao;
    }

    public double getCanNang() {
        return canNang;
    }

    public void setCanNang(double canNang) {
        this.canNang = canNang;
    }

    public double getChiSoBMI() {
        return chiSoBMI;
    }

    public void setChiSoBMI(double chiSoBMI) {
        this.chiSoBMI = chiSoBMI;
    }

    public String getNgayDo() {
        return ngayDo;
    }

    public void setNgayDo(String ngayDo) {
        this.ngayDo = ngayDo;
    }
}
