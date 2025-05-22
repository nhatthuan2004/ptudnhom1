
package model;

import javafx.beans.property.*;

public class ChitietPhieuDichVu {
    private final StringProperty maPhieuDichVu = new SimpleStringProperty();
    private final StringProperty maDichVu = new SimpleStringProperty();
    private final IntegerProperty soLuong = new SimpleIntegerProperty();
    private final DoubleProperty donGia = new SimpleDoubleProperty();
    private final StringProperty maPhong = new SimpleStringProperty();

    // Constructor mặc định
    public ChitietPhieuDichVu() {}

    // Constructor đầy đủ
    public ChitietPhieuDichVu(String maPhieuDichVu, String maDichVu, int soLuong, double donGia, String maPhong) {
        setMaPhieuDichVu(maPhieuDichVu);
        setMaDichVu(maDichVu);
        setSoLuong(soLuong);
        setDonGia(donGia);
        setMaPhong(maPhong);
    }

    // Getter và Setter cho maPhieuDichVu
    public String getMaPhieuDichVu() {
        return maPhieuDichVu.get();
    }

    public StringProperty maPhieuDichVuProperty() {
        return maPhieuDichVu;
    }

    public void setMaPhieuDichVu(String maPhieuDichVu) {
        this.maPhieuDichVu.set(maPhieuDichVu != null ? maPhieuDichVu.trim() : "");
    }

    // Getter và Setter cho maDichVu
    public String getMaDichVu() {
        return maDichVu.get();
    }

    public StringProperty maDichVuProperty() {
        return maDichVu;
    }

    public void setMaDichVu(String maDichVu) {
        this.maDichVu.set(maDichVu != null ? maDichVu.trim() : "");
    }

    // Getter và Setter cho soLuong
    public int getSoLuong() {
        return soLuong.get();
    }

    public IntegerProperty soLuongProperty() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong.set(soLuong > 0 ? soLuong : 1); // Đảm bảo số lượng không âm
    }

    // Getter và Setter cho donGia
    public double getDonGia() {
        return donGia.get();
    }

    public DoubleProperty donGiaProperty() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        this.donGia.set(donGia >= 0 ? donGia : 0); // Đảm bảo đơn giá không âm
    }

    // Getter và Setter cho maPhong
    public String getMaPhong() {
        return maPhong.get();
    }

    public StringProperty maPhongProperty() {
        return maPhong;
    }

    public void setMaPhong(String maPhong) {
        this.maPhong.set(maPhong != null ? maPhong.trim() : "");
    }

    // Phương thức toString để dễ debug
    @Override
    public String toString() {
        return "ChitietPhieuDichVu{" +
                "maPhieuDichVu=" + maPhieuDichVu.get() +
                ", maDichVu=" + maDichVu.get() +
                ", soLuong=" + soLuong.get() +
                ", donGia=" + donGia.get() +
                ", maPhong=" + maPhong.get() +
                '}';
    }
}
