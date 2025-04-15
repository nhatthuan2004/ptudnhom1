package model;

import javafx.beans.property.*;

public class ChitietPhieuDatPhong {
	private final StringProperty maDatPhong = new SimpleStringProperty();
    private final StringProperty maPhong = new SimpleStringProperty();
    
    private final StringProperty trangThai = new SimpleStringProperty();
    private final StringProperty moTa = new SimpleStringProperty();
    private final DoubleProperty tienPhong = new SimpleDoubleProperty();
    private final DoubleProperty tienDichVu = new SimpleDoubleProperty();
    private final DoubleProperty thanhTien = new SimpleDoubleProperty();
    private final IntegerProperty soLuong = new SimpleIntegerProperty();
    private final BooleanProperty daThanhToan = new SimpleBooleanProperty();


    public ChitietPhieuDatPhong(String maDatPhong,String maPhong,  String trangThai, String moTa,
                                double tienPhong, double tienDichVu, double thanhTien, int soLuong, 
                                boolean daThanhToan) {
        setMaDatPhong(maDatPhong);
        setMaPhong(maPhong);
        setTrangThai(trangThai);
        setMoTa(moTa);
        setTienPhong(tienPhong);
        setTienDichVu(tienDichVu);
        setThanhTien(thanhTien);
        setSoLuong(soLuong);
        setDaThanhToan(daThanhToan);
    }

    

    public String getMaDatPhong() { return maDatPhong.get(); }
    public StringProperty maDatPhongProperty() { return maDatPhong; }
    public void setMaDatPhong(String maDatPhong) {
        this.maDatPhong.set(maDatPhong != null && !maDatPhong.trim().isEmpty() ? maDatPhong : null);
	}

	public String getMaPhong() {
		return maPhong.get();
	}

	public StringProperty maPhongProperty() {
		return maPhong;
	}

	public void setMaPhong(String maPhong) {
		this.maPhong.set(maPhong != null && !maPhong.trim().isEmpty() ? maPhong : null);
	}

    public String getTrangThai() { return trangThai.get(); }
    public StringProperty trangThaiProperty() { return trangThai; }
    public void setTrangThai(String trangThai) {
        this.trangThai.set(trangThai != null && !trangThai.trim().isEmpty() ? trangThai : null);
    }

    public String getMoTa() { return moTa.get(); }
    public StringProperty moTaProperty() { return moTa; }
    public void setMoTa(String moTa) {
        this.moTa.set(moTa != null ? moTa : null);
    }

    public double getTienPhong() { return tienPhong.get(); }
    public DoubleProperty tienPhongProperty() { return tienPhong; }
    public void setTienPhong(double tienPhong) {
        this.tienPhong.set(tienPhong >= 0 ? tienPhong : 0);
    }

    public double getTienDichVu() { return tienDichVu.get(); }
    public DoubleProperty tienDichVuProperty() { return tienDichVu; }
    public void setTienDichVu(double tienDichVu) {
        this.tienDichVu.set(tienDichVu >= 0 ? tienDichVu : 0);
    }

    public double getThanhTien() { return thanhTien.get(); }
    public DoubleProperty thanhTienProperty() { return thanhTien; }
    public void setThanhTien(double thanhTien) {
        this.thanhTien.set(thanhTien >= 0 ? thanhTien : 0);
    }

    public int getSoLuong() { return soLuong.get(); }
    public IntegerProperty soLuongProperty() { return soLuong; }
    public void setSoLuong(int soLuong) {
        this.soLuong.set(soLuong > 0 ? soLuong : 1);
    }

    public boolean getDaThanhToan() { return daThanhToan.get(); }
    public BooleanProperty daThanhToanProperty() { return daThanhToan; }
    public void setDaThanhToan(boolean daThanhToan) {
        this.daThanhToan.set(daThanhToan);
    }
}