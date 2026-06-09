package com.example.health_app_sof1021.model;

public class Notification {
    private int maThongBao;
    private int maNguoiDung;
    private String tieuDe;
    private String noiDung;
    private String ngayThongBao;
    private int daDoc; // 0: chưa đọc, 1: đã đọc

    public Notification() {}

    public Notification(int maThongBao, int maNguoiDung, String tieuDe, String noiDung, String ngayThongBao, int daDoc) {
        this.maThongBao = maThongBao;
        this.maNguoiDung = maNguoiDung;
        this.tieuDe = tieuDe;
        this.noiDung = noiDung;
        this.ngayThongBao = ngayThongBao;
        this.daDoc = daDoc;
    }

    public int getMaThongBao() { return maThongBao; }
    public void setMaThongBao(int maThongBao) { this.maThongBao = maThongBao; }

    public int getMaNguoiDung() { return maNguoiDung; }
    public void setMaNguoiDung(int maNguoiDung) { this.maNguoiDung = maNguoiDung; }

    public String getTieuDe() { return tieuDe; }
    public void setTieuDe(String tieuDe) { this.tieuDe = tieuDe; }

    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }

    public String getNgayThongBao() { return ngayThongBao; }
    public void setNgayThongBao(String ngayThongBao) { this.ngayThongBao = ngayThongBao; }

    public int getDaDoc() { return daDoc; }
    public void setDaDoc(int daDoc) { this.daDoc = daDoc; }
}
