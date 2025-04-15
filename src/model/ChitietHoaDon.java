package model;

import javafx.beans.property.*;

public class ChitietHoaDon {
    private final StringProperty maHoaDon = new SimpleStringProperty();
    private final StringProperty maPhong = new SimpleStringProperty();
    private final DoubleProperty thueVat = new SimpleDoubleProperty(); // Changed to Double to match SQL FLOAT
    private final IntegerProperty soNgay = new SimpleIntegerProperty();
    private final DoubleProperty khuyenMai = new SimpleDoubleProperty();
    private final DoubleProperty tienPhong = new SimpleDoubleProperty();
    private final DoubleProperty tienDichVu = new SimpleDoubleProperty();
    private final StringProperty maChuongTrinhKhuyenMai = new SimpleStringProperty();
    private final StringProperty maDatPhong = new SimpleStringProperty();
    private final StringProperty maPhieuDichVu = new SimpleStringProperty();

    // Constructor
    public ChitietHoaDon(String maHoaDon, String maPhong, double thueVat, int soNgay, double khuyenMai,
                         double tienPhong, double tienDichVu, String maChuongTrinhKhuyenMai,
                         String maDatPhong, String maPhieuDichVu) {
        setMaHoaDon(maHoaDon);
        setMaPhong(maPhong);
        setThueVat(thueVat);
        setSoNgay(soNgay);
        setKhuyenMai(khuyenMai);
        setTienPhong(tienPhong);
        setTienDichVu(tienDichVu);
        setMaChuongTrinhKhuyenMai(maChuongTrinhKhuyenMai);
        setMaDatPhong(maDatPhong);
        setMaPhieuDichVu(maPhieuDichVu);
    }

    // Getters & Setters
    public String getMaHoaDon() {
        return maHoaDon.get();
    }

    public void setMaHoaDon(String value) {
        this.maHoaDon.set(value != null && !value.trim().isEmpty() ? value : null);
    }

    public StringProperty maHoaDonProperty() {
        return maHoaDon;
    }

    public String getMaPhong() {
        return maPhong.get();
    }

    public void setMaPhong(String value) {
        this.maPhong.set(value != null && !value.trim().isEmpty() ? value : null);
    }

    public StringProperty maPhongProperty() {
        return maPhong;
    }

    public double getThueVat() {
        return thueVat.get();
    }

    public void setThueVat(double value) {
        this.thueVat.set(value >= 0 && value <= 100 ? value : 0);
    }

    public DoubleProperty thueVatProperty() {
        return thueVat;
    }

    public int getSoNgay() {
        return soNgay.get();
    }

    public void setSoNgay(int value) {
        this.soNgay.set(value >= 0 ? value : 0);
    }

    public IntegerProperty soNgayProperty() {
        return soNgay;
    }

    public double getKhuyenMai() {
        return khuyenMai.get();
    }

    public void setKhuyenMai(double value) {
        this.khuyenMai.set(value >= 0 && value <= 100 ? value : 0);
    }

    public DoubleProperty khuyenMaiProperty() {
        return khuyenMai;
    }

    public double getTienPhong() {
        return tienPhong.get();
    }

    public void setTienPhong(double value) {
        this.tienPhong.set(value >= 0 ? value : 0);
    }

    public DoubleProperty tienPhongProperty() {
        return tienPhong;
    }

    public double getTienDichVu() {
        return tienDichVu.get();
    }

    public void setTienDichVu(double value) {
        this.tienDichVu.set(value >= 0 ? value : 0);
    }

    public DoubleProperty tienDichVuProperty() {
        return tienDichVu;
    }

    public String getMaChuongTrinhKhuyenMai() {
        return maChuongTrinhKhuyenMai.get();
    }

    public void setMaChuongTrinhKhuyenMai(String value) {
        this.maChuongTrinhKhuyenMai.set(value != null && !value.trim().isEmpty() ? value : null);
    }

    public StringProperty maChuongTrinhKhuyenMaiProperty() {
        return maChuongTrinhKhuyenMai;
    }

    public String getMaDatPhong() {
        return maDatPhong.get();
    }

    public void setMaDatPhong(String value) {
        this.maDatPhong.set(value != null && !value.trim().isEmpty() ? value : null);
    }

    public StringProperty maDatPhongProperty() {
        return maDatPhong;
    }

    public String getMaPhieuDichVu() {
        return maPhieuDichVu.get();
    }

    public void setMaPhieuDichVu(String value) {
        this.maPhieuDichVu.set(value != null && !value.trim().isEmpty() ? value : null);
    }

    public StringProperty maPhieuDichVuProperty() {
        return maPhieuDichVu;
    }
}