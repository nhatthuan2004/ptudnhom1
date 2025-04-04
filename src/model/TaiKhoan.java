package model;

import javafx.beans.property.*;

public class TaiKhoan {
    private final StringProperty tenDangNhap = new SimpleStringProperty();
    private final StringProperty matKhau = new SimpleStringProperty();
    private NhanVien nhanVien;

    public TaiKhoan() {}

    public TaiKhoan(String tenDangNhap, String matKhau, NhanVien nhanVien) {
        setTenDangNhap(tenDangNhap);
        setMatKhau(matKhau);
        setNhanVien(nhanVien);
    }

    public String getTenDangNhap() { return tenDangNhap.get(); }
    public StringProperty tenDangNhapProperty() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { 
        this.tenDangNhap.set(tenDangNhap != null ? tenDangNhap : ""); 
    }

    public String getMatKhau() { return matKhau.get(); }
    public StringProperty matKhauProperty() { return matKhau; }
    public void setMatKhau(String matKhau) { 
        this.matKhau.set(matKhau != null && matKhau.length() >= 6 ? matKhau : ""); 
    }

    public NhanVien getNhanVien() { return nhanVien; }
    public void setNhanVien(NhanVien nhanVien) { this.nhanVien = nhanVien; }
}