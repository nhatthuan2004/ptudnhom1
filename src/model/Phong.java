package model;

import javafx.beans.property.*;

public class Phong {
    private final StringProperty maPhong = new SimpleStringProperty();
    private final StringProperty loaiPhong = new SimpleStringProperty();
    private final DoubleProperty giaPhong = new SimpleDoubleProperty();
    private final StringProperty trangThai = new SimpleStringProperty();
    private final StringProperty donDep = new SimpleStringProperty();
    private final StringProperty viTri = new SimpleStringProperty();
    private final IntegerProperty soNguoiToiDa = new SimpleIntegerProperty();
    private final StringProperty moTa = new SimpleStringProperty();

    public Phong() {}

    public Phong(String maPhong, String loaiPhong, double giaPhong, String trangThai,
                 String donDep, String viTri, int soNguoiToiDa, String moTa) {
        setMaPhong(maPhong);
        setLoaiPhong(loaiPhong);
        setGiaPhong(giaPhong);
        setTrangThai(trangThai);
        setDonDep(donDep);
        setViTri(viTri);
        setSoNguoiToiDa(soNguoiToiDa);
        setMoTa(moTa);
    }

    public String getMaPhong() { return maPhong.get(); }
    public StringProperty maPhongProperty() { return maPhong; }
    public void setMaPhong(String maPhong) {
        this.maPhong.set(maPhong != null && !maPhong.trim().isEmpty() ? maPhong : null);
    }

    public String getLoaiPhong() { return loaiPhong.get(); }
    public StringProperty loaiPhongProperty() { return loaiPhong; }
    public void setLoaiPhong(String loaiPhong) {
        this.loaiPhong.set(loaiPhong != null && !loaiPhong.trim().isEmpty() ? loaiPhong : null);
    }

    public double getGiaPhong() { return giaPhong.get(); }
    public DoubleProperty giaPhongProperty() { return giaPhong; }
    public void setGiaPhong(double giaPhong) {
        this.giaPhong.set(giaPhong >= 0 ? giaPhong : 0);
    }

    public String getTrangThai() { return trangThai.get(); }
    public StringProperty trangThaiProperty() { return trangThai; }
    public void setTrangThai(String trangThai) {
        this.trangThai.set(trangThai != null && (trangThai.equals("Trống") || trangThai.equals("Đã đặt") || trangThai.equals("Bảo Trì")) ? trangThai : "Trống");
    }

    public String getDonDep() { return donDep.get(); }
    public StringProperty donDepProperty() { return donDep; }
    public void setDonDep(String donDep) {
        this.donDep.set(donDep != null && (donDep.equals("Sạch") || donDep.equals("Chưa dọn")) ? donDep : "Sạch");
    }

    public String getViTri() { return viTri.get(); }
    public StringProperty viTriProperty() { return viTri; }
    public void setViTri(String viTri) {
        this.viTri.set(viTri != null ? viTri : null);
    }

    public int getSoNguoiToiDa() { return soNguoiToiDa.get(); }
    public IntegerProperty soNguoiToiDaProperty() { return soNguoiToiDa; }
    public void setSoNguoiToiDa(int soNguoiToiDa) {
        this.soNguoiToiDa.set(soNguoiToiDa > 0 ? soNguoiToiDa : 1);
    }

    public String getMoTa() { return moTa.get(); }
    public StringProperty moTaProperty() { return moTa; }
    public void setMoTa(String moTa) {
        this.moTa.set(moTa != null ? moTa : null);
    }
}