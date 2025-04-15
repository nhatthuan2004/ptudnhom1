package model;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.*;
import javafx.collections.ObservableList;

public class NhanVien {
    private final StringProperty maNhanVien = new SimpleStringProperty();
    private final StringProperty tenNhanVien = new SimpleStringProperty();
    private final StringProperty soDienThoai = new SimpleStringProperty();
    private final BooleanProperty gioiTinh = new SimpleBooleanProperty();
    private final StringProperty diaChi = new SimpleStringProperty();
    private final StringProperty chucVu = new SimpleStringProperty();
    private final DoubleProperty luong = new SimpleDoubleProperty();
    private List<HoaDon> hoaDonList;

    public NhanVien() {
        this.hoaDonList = new ArrayList<>(); // Khởi tạo danh sách rỗng
    }

    public NhanVien(String maNhanVien, String tenNhanVien, String soDienThoai, boolean gioiTinh,
                    String diaChi, String chucVu, double luong) {
        this.hoaDonList = new ArrayList<>(); // Khởi tạo danh sách rỗng
        setMaNhanVien(maNhanVien);
        setTenNhanVien(tenNhanVien);
        setSoDienThoai(soDienThoai);
        setGioiTinh(gioiTinh);
        setDiaChi(diaChi);
        setChucVu(chucVu);
        setLuong(luong);
    }

    public String getMaNhanVien() { return maNhanVien.get(); }
    public StringProperty maNhanVienProperty() { return maNhanVien; }
    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien.set(maNhanVien != null && !maNhanVien.trim().isEmpty() ? maNhanVien : null);
    }

    public String getTenNhanVien() { return tenNhanVien.get(); }
    public StringProperty tenNhanVienProperty() { return tenNhanVien; }
    public void setTenNhanVien(String tenNhanVien) {
        if (tenNhanVien == null || tenNhanVien.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên nhân viên không được để trống.");
        }
        this.tenNhanVien.set(tenNhanVien);
    }

    public String getSoDienThoai() { return soDienThoai.get(); }
    public StringProperty soDienThoaiProperty() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai.set(soDienThoai != null && soDienThoai.matches("\\d{10,11}") ? soDienThoai : null);
    }

    public boolean isGioiTinh() { return gioiTinh.get(); }
    public BooleanProperty gioiTinhProperty() { return gioiTinh; }
    public void setGioiTinh(boolean gioiTinh) {
        this.gioiTinh.set(gioiTinh);
    }

    public String getDiaChi() { return diaChi.get(); }
    public StringProperty diaChiProperty() { return diaChi; }
    public void setDiaChi(String diaChi) {
        this.diaChi.set(diaChi != null ? diaChi : null);
    }

    public String getChucVu() { return chucVu.get(); }
    public StringProperty chucVuProperty() { return chucVu; }
    public void setChucVu(String chucVu) {
        this.chucVu.set(chucVu != null && !chucVu.trim().isEmpty() ? chucVu : null);
    }

    public double getLuong() { return luong.get(); }
    public DoubleProperty luongProperty() { return luong; }
    public void setLuong(double luong) {
        this.luong.set(luong >= 0 ? luong : 0);
    }

    public List<HoaDon> getHoaDonList() {
        if (hoaDonList == null) {
            hoaDonList = new ArrayList<>();
        }
        return hoaDonList;
    }

    public void setHoaDonList(List<HoaDon> hoaDonList) {
        this.hoaDonList = (hoaDonList != null) ? hoaDonList : new ArrayList<>();
    }

    public void addHoaDon(HoaDon hoaDon, ObservableList<HoaDon> hoaDonListExternal) {
        if (hoaDon != null && !this.hoaDonList.contains(hoaDon)) {
            this.hoaDonList.add(hoaDon);
        }
    }
}