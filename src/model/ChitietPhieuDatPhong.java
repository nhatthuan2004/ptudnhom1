package model;

import javafx.beans.property.*;

public class ChitietPhieuDatPhong {
    private final StringProperty maPhong = new SimpleStringProperty();
    private final StringProperty maDatPhong = new SimpleStringProperty();
    private final DoubleProperty thanhTien = new SimpleDoubleProperty();
    private final DoubleProperty giaPhong = new SimpleDoubleProperty();
    private final IntegerProperty soLuong = new SimpleIntegerProperty();
    private Phong phong;
    private PhieuDatPhong phieuDatPhong;

    public ChitietPhieuDatPhong() {}

    public ChitietPhieuDatPhong(String maPhong, String maDatPhong, double thanhTien, double giaPhong, int soLuong) {
        setMaPhong(maPhong);
        setMaDatPhong(maDatPhong);
        setThanhTien(thanhTien);
        setGiaPhong(giaPhong);
        setSoLuong(soLuong);
    }

    public String getMaPhong() { return maPhong.get(); }
    public StringProperty maPhongProperty() { return maPhong; }
    public void setMaPhong(String maPhong) { 
        this.maPhong.set(maPhong != null ? maPhong : ""); 
    }

    public String getMaDatPhong() { return maDatPhong.get(); }
    public StringProperty maDatPhongProperty() { return maDatPhong; }
    public void setMaDatPhong(String maDatPhong) { 
        this.maDatPhong.set(maDatPhong != null ? maDatPhong : ""); 
    }

    public double getThanhTien() { return thanhTien.get(); }
    public DoubleProperty thanhTienProperty() { return thanhTien; }
    public void setThanhTien(double thanhTien) { 
        this.thanhTien.set(thanhTien >= 0 ? thanhTien : 0); 
    }

    public double getGiaPhong() { return giaPhong.get(); }
    public DoubleProperty giaPhongProperty() { return giaPhong; }
    public void setGiaPhong(double giaPhong) { 
        this.giaPhong.set(giaPhong >= 0 ? giaPhong : 0); 
    }

    public int getSoLuong() { return soLuong.get(); }
    public IntegerProperty soLuongProperty() { return soLuong; }
    public void setSoLuong(int soLuong) { 
        this.soLuong.set(soLuong > 0 ? soLuong : 1); 
    }

    public Phong getPhong() { return phong; }
    public void setPhong(Phong phong) { this.phong = phong; }

    public PhieuDatPhong getPhieuDatPhong() { return phieuDatPhong; }
    public void setPhieuDatPhong(PhieuDatPhong phieuDatPhong) { this.phieuDatPhong = phieuDatPhong; }
}