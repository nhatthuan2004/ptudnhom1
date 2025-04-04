package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;

public class KhachHang {
    private String maKhachHang, tenKhachHang, soDienThoai, email, diaChi, cccd, quocTich, gioiTinh;
    private LocalDate ngaySinh;
    private ObservableList<HoaDon> danhSachHoaDon = FXCollections.observableArrayList();

    public KhachHang(String maKhachHang, String tenKhachHang, String soDienThoai, String email, 
                     String diaChi, String cccd, LocalDate ngaySinh, String quocTich, String gioiTinh) {
        this.maKhachHang = maKhachHang;
        this.tenKhachHang = tenKhachHang;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.diaChi = diaChi;
        this.cccd = cccd;
        this.ngaySinh = ngaySinh;
        this.quocTich = quocTich;
        this.gioiTinh = gioiTinh;
    }

    // Phương thức addHoaDon đã chỉnh sửa để phù hợp với DataManager
    public void addHoaDon(HoaDon hoaDon, ObservableList<HoaDon> hoaDonList) {
        if (hoaDon != null) {
            // Thêm hóa đơn vào danh sách chung nếu chưa có
            if (!hoaDonList.contains(hoaDon)) {
                hoaDonList.add(hoaDon);
            }
            // Thêm hóa đơn vào danh sách cá nhân của khách hàng nếu chưa có
            if (!danhSachHoaDon.contains(hoaDon)) {
                danhSachHoaDon.add(hoaDon);
            }
        }
    }

    // Getters và setters
    public String getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(String maKhachHang) { this.maKhachHang = maKhachHang; }

    public String getTenKhachHang() { return tenKhachHang; }
    public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }

    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }

    public String getQuocTich() { return quocTich; }
    public void setQuocTich(String quocTich) { this.quocTich = quocTich; }

    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }

    public ObservableList<HoaDon> getDanhSachHoaDon() { return danhSachHoaDon; }
}