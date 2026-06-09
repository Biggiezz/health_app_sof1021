package com.example.health_app_sof1021.model;

public class Meal {
    private final int id;
    private final String tenMon;
    private final String loaiBua;
    private final int calo;
    private final int soLuong;
    private final String ngayAn;

    public Meal(int id, String tenMon, String loaiBua, int calo, int soLuong, String ngayAn) {
        this.id = id;
        this.tenMon = tenMon;
        this.loaiBua = loaiBua;
        this.calo = calo;
        this.soLuong = soLuong;
        this.ngayAn = ngayAn;
    }

    public int getId() {
        return id;
    }

    public String getTenMon() {
        return tenMon;
    }

    public String getLoaiBua() {
        return loaiBua;
    }

    public int getCalo() {
        return calo;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public String getNgayAn() {
        return ngayAn;
    }

    public int getTongCalo() {
        return calo * soLuong;
    }

    @Override
    public String toString() {
        return loaiBua + " - " + tenMon + " x" + soLuong + " (" + getTongCalo() + " calo)";
    }
}
