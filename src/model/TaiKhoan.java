package model;

import javafx.beans.property.*;

public class TaiKhoan {
    private final StringProperty tenDangNhap = new SimpleStringProperty();
    private final StringProperty matKhau = new SimpleStringProperty();
    private final StringProperty maNhanVien = new SimpleStringProperty();

    public TaiKhoan() {}

    public TaiKhoan(String tenDangNhap, String matKhau, String maNhanVien) {
        setTenDangNhap(tenDangNhap);
        setMatKhau(matKhau);
        setMaNhanVien(maNhanVien);
    }

    public String getTenDangNhap() {
        return tenDangNhap.get();
    }

    public StringProperty tenDangNhapProperty() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống!");
        }
        if (tenDangNhap.length() > 50) {
            throw new IllegalArgumentException("Tên đăng nhập không được vượt quá 50 ký tự!");
        }
        this.tenDangNhap.set(tenDangNhap.trim());
    }

    public String getMatKhau() {
        return matKhau.get();
    }

    public StringProperty matKhauProperty() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        if (matKhau == null || matKhau.trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống!");
        }
        this.matKhau.set(matKhau.trim());
    }

    public String getMaNhanVien() {
        return maNhanVien.get();
    }

    public StringProperty maNhanVienProperty() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien.set(maNhanVien != null && !maNhanVien.trim().isEmpty() ? maNhanVien.trim() : null);
    }

    @Override
    public String toString() {
        return "TaiKhoan{" +
                "tenDangNhap=" + tenDangNhap.get() +
                ", matKhau=[PROTECTED]" +
                ", maNhanVien=" + maNhanVien.get() +
                '}';
    }
}