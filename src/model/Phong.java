package model;

import javafx.beans.property.*;

public class Phong {
    private final StringProperty maPhong;
    private final StringProperty loaiPhong;
    private final DoubleProperty giaPhong;
    private final StringProperty trangThai;
    private final StringProperty donDep;
    private final StringProperty viTri;
    private final IntegerProperty soNguoiToiDa;
    private final StringProperty moTa;
    public Phong(String maPhong, String loaiPhong, double giaPhong, String trangThai,
                 String donDep, String viTri, int soNguoiToiDa, String moTa) {
        this.maPhong = new SimpleStringProperty(maPhong);
        this.loaiPhong = new SimpleStringProperty(loaiPhong);
        this.giaPhong = new SimpleDoubleProperty(giaPhong);
        this.trangThai = new SimpleStringProperty(trangThai);
        this.donDep = new SimpleStringProperty(donDep);
        this.viTri = new SimpleStringProperty(viTri);
        this.soNguoiToiDa = new SimpleIntegerProperty(soNguoiToiDa);
        this.moTa = new SimpleStringProperty(moTa);
    }

    // Getters
    public String getMaPhong() { return maPhong.get(); }
    public String getLoaiPhong() { return loaiPhong.get(); }
    public double getGiaPhong() { return giaPhong.get(); }
    public String getTrangThai() { return trangThai.get(); }
    public String getDonDep() { return donDep.get(); }
    public String getViTri() { return viTri.get(); }
    public int getSoNguoiToiDa() { return soNguoiToiDa.get(); }
    public String getMoTa() { return moTa.get(); }

    // Setters
    public void setMaPhong(String value) { maPhong.set(value); }
    public void setLoaiPhong(String value) { loaiPhong.set(value); }
    public void setGiaPhong(double value) { giaPhong.set(value); }
    public void setTrangThai(String value) { trangThai.set(value); }
    public void setDonDep(String value) { donDep.set(value); }
    public void setViTri(String value) { viTri.set(value); }
    public void setSoNguoiToiDa(int value) { soNguoiToiDa.set(value); }
    public void setMoTa(String value) { moTa.set(value); }

    // JavaFX properties
    public StringProperty maPhongProperty() { return maPhong; }
    public StringProperty loaiPhongProperty() { return loaiPhong; }
    public DoubleProperty giaPhongProperty() { return giaPhong; }
    public StringProperty trangThaiProperty() { return trangThai; }
    public StringProperty donDepProperty() { return donDep; }
    public StringProperty viTriProperty() { return viTri; }
    public IntegerProperty soNguoiToiDaProperty() { return soNguoiToiDa; }
    public StringProperty moTaProperty() { return moTa; }

	public String getTenKhachHang() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSoDienThoai() {
		// TODO Auto-generated method stub
		return null;
	}
}
