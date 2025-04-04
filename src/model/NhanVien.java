package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class NhanVien {
    private String maNhanVien, tenNhanVien, soDienThoai, diaChi, chucVu, taiKhoan, matKhau, trangThai;
    private boolean gioiTinh;
    private double luong;
    private ObservableList<HoaDon> danhSachHoaDon = FXCollections.observableArrayList();

    public NhanVien(String maNhanVien, String tenNhanVien, String soDienThoai, boolean gioiTinh, 
                    String diaChi, String chucVu, double luong, String taiKhoan, String matKhau, String trangThai) {
        this.maNhanVien = maNhanVien;
        this.tenNhanVien = tenNhanVien;
        this.soDienThoai = soDienThoai;
        this.gioiTinh = gioiTinh;
        this.diaChi = diaChi;
        this.chucVu = chucVu;
        this.luong = luong;
        this.taiKhoan = taiKhoan;
        this.matKhau = matKhau;
        this.trangThai = trangThai;
    }

    // Phương thức addHoaDon đã chỉnh sửa để phù hợp với DataManager
    public void addHoaDon(HoaDon hoaDon, ObservableList<HoaDon> hoaDonList) {
        if (hoaDon != null) {
            // Thêm hóa đơn vào danh sách chung nếu chưa có
            if (!hoaDonList.contains(hoaDon)) {
                hoaDonList.add(hoaDon);
            }
            // Thêm hóa đơn vào danh sách cá nhân của nhân viên nếu chưa có
            if (!danhSachHoaDon.contains(hoaDon)) {
                danhSachHoaDon.add(hoaDon);
            }
        }
    }

    // Getters và setters
    public String getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(String maNhanVien) { this.maNhanVien = maNhanVien; }

    public String getTenNhanVien() { return tenNhanVien; }
    public void setTenNhanVien(String tenNhanVien) { this.tenNhanVien = tenNhanVien; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public boolean isGioiTinh() { return gioiTinh; }
    public void setGioiTinh(boolean gioiTinh) { this.gioiTinh = gioiTinh; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public String getChucVu() { return chucVu; }
    public void setChucVu(String chucVu) { this.chucVu = chucVu; }

    public double getLuong() { return luong; }
    public void setLuong(double luong) { this.luong = luong; }

    public String getTaiKhoan() { return taiKhoan; }
    public void setTaiKhoan(String taiKhoan) { this.taiKhoan = taiKhoan; }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public ObservableList<HoaDon> getDanhSachHoaDon() { return danhSachHoaDon; }
}