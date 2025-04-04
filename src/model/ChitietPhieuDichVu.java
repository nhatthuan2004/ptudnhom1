package model;

import javafx.beans.property.*;

public class ChitietPhieuDichVu {
    private final StringProperty maPhieuDichVu = new SimpleStringProperty();
    private final StringProperty maDichVu = new SimpleStringProperty();
    private final IntegerProperty soLuong = new SimpleIntegerProperty();
    private final DoubleProperty donGia = new SimpleDoubleProperty();
    private PhieuDichVu phieuDichVu;
    private DichVu dichVu;

    public ChitietPhieuDichVu() {}

    public ChitietPhieuDichVu(String maPhieuDichVu, String maDichVu, int soLuong, double donGia) {
        setMaPhieuDichVu(maPhieuDichVu);
        setMaDichVu(maDichVu);
        setSoLuong(soLuong);
        setDonGia(donGia);
    }

    public String getMaPhieuDichVu() { return maPhieuDichVu.get(); }
    public StringProperty maPhieuDichVuProperty() { return maPhieuDichVu; }
    public void setMaPhieuDichVu(String maPhieuDichVu) { 
        this.maPhieuDichVu.set(maPhieuDichVu != null ? maPhieuDichVu : ""); 
    }

    public String getMaDichVu() { return maDichVu.get(); }
    public StringProperty maDichVuProperty() { return maDichVu; }
    public void setMaDichVu(String maDichVu) { 
        this.maDichVu.set(maDichVu != null ? maDichVu : ""); 
    }

    public int getSoLuong() { return soLuong.get(); }
    public IntegerProperty soLuongProperty() { return soLuong; }
    public void setSoLuong(int soLuong) { 
        this.soLuong.set(soLuong > 0 ? soLuong : 1); 
    }

    public double getDonGia() { return donGia.get(); }
    public DoubleProperty donGiaProperty() { return donGia; }
    public void setDonGia(double donGia) { 
        this.donGia.set(donGia >= 0 ? donGia : 0); 
    }

    public PhieuDichVu getPhieuDichVu() { return phieuDichVu; }
    public void setPhieuDichVu(PhieuDichVu phieuDichVu) { this.phieuDichVu = phieuDichVu; }

    public DichVu getDichVu() { return dichVu; }
    public void setDichVu(DichVu dichVu) { this.dichVu = dichVu; }
}