package dao;

import model.TaiKhoan;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import ConnectDB.ConnectDB;

public class TaiKhoan_Dao {
    private final ConnectDB connectDB;

    public TaiKhoan_Dao() {
        this.connectDB = ConnectDB.getInstance();
    }

    // Kiểm tra đăng nhập
    public TaiKhoan authenticate(String tenDangNhap, String matKhau) throws SQLException {
        String sql = "SELECT tenDangNhap, matKhau, maNhanVien FROM TaiKhoan WHERE tenDangNhap = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
                throw new SQLException("Tên đăng nhập không được để trống!");
            }
            if (matKhau == null || matKhau.trim().isEmpty()) {
                throw new SQLException("Mật khẩu không được để trống!");
            }

            ps.setString(1, tenDangNhap.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("matKhau");
                    if (matKhau.trim().equals(storedPassword)) {
                        TaiKhoan tk = new TaiKhoan();
                        tk.setTenDangNhap(rs.getString("tenDangNhap"));
                        tk.setMaNhanVien(rs.getString("maNhanVien") != null ? rs.getString("maNhanVien") : "");
                        return tk;
                    } else {
                        System.out.println("Mật khẩu không khớp cho " + tenDangNhap + ": nhập=" + matKhau + ", lưu=" + storedPassword);
                    }
                } else {
                    System.out.println("Không tìm thấy tài khoản: " + tenDangNhap);
                }
            }
        }
        return null;
    }

    // Thêm tài khoản
    public boolean themTaiKhoan(TaiKhoan tk) throws SQLException {
        String sql = "INSERT INTO TaiKhoan (tenDangNhap, matKhau, maNhanVien) VALUES (?, ?, ?)";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (tk.getTenDangNhap().isEmpty()) {
                throw new SQLException("Tên đăng nhập không được để trống!");
            }
            if (tk.getMatKhau().isEmpty()) {
                throw new SQLException("Mật khẩu không được để trống!");
            }

            ps.setString(1, tk.getTenDangNhap());
            ps.setString(2, tk.getMatKhau());
            ps.setString(3, tk.getMaNhanVien() != null && !tk.getMaNhanVien().isEmpty() ? tk.getMaNhanVien() : null);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                throw new SQLException("Lỗi: Mã nhân viên không hợp lệ hoặc tên đăng nhập đã tồn tại!", e);
            }
            throw e;
        }
    }

    // Xóa tài khoản
    public boolean xoaTaiKhoan(String tenDangNhap) throws SQLException {
        String sql = "DELETE FROM TaiKhoan WHERE tenDangNhap = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
                throw new SQLException("Tên đăng nhập không được để trống!");
            }

            ps.setString(1, tenDangNhap.trim());
            return ps.executeUpdate() > 0;
        }
    }

    // Sửa tài khoản
    public boolean suaTaiKhoan(TaiKhoan tk) throws SQLException {
        String sql = "UPDATE TaiKhoan SET matKhau = ?, maNhanVien = ? WHERE tenDangNhap = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (tk.getTenDangNhap().isEmpty()) {
                throw new SQLException("Tên đăng nhập không được để trống!");
            }
            if (tk.getMatKhau().isEmpty()) {
                throw new SQLException("Mật khẩu không được để trống!");
            }

            ps.setString(1, tk.getMatKhau());
            ps.setString(2, tk.getMaNhanVien() != null && !tk.getMaNhanVien().isEmpty() ? tk.getMaNhanVien() : null);
            ps.setString(3, tk.getTenDangNhap());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                throw new SQLException("Lỗi: Mã nhân viên không hợp lệ!", e);
            }
            throw e;
        }
    }

    // Thay đổi mật khẩu
    public boolean doiMatKhau(String tenDangNhap, String matKhauMoi) throws SQLException {
        String sql = "UPDATE TaiKhoan SET matKhau = ? WHERE tenDangNhap = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
                throw new SQLException("Tên đăng nhập không được để trống!");
            }
            if (matKhauMoi == null || matKhauMoi.trim().isEmpty()) {
                throw new SQLException("Mật khẩu mới không được để trống!");
            }

            ps.setString(1, matKhauMoi.trim());
            ps.setString(2, tenDangNhap.trim());

            return ps.executeUpdate() > 0;
        }
    }

    // Tìm kiếm tài khoản
    public List<TaiKhoan> timKiemTaiKhoan(String tuKhoa) throws SQLException {
        List<TaiKhoan> list = new ArrayList<>();
        String sql = "SELECT tenDangNhap, maNhanVien FROM TaiKhoan WHERE tenDangNhap LIKE ? OR maNhanVien LIKE ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String searchKeyword = tuKhoa != null ? tuKhoa.trim() : "";
            ps.setString(1, "%" + searchKeyword + "%");
            ps.setString(2, "%" + searchKeyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TaiKhoan tk = new TaiKhoan();
                    tk.setTenDangNhap(rs.getString("tenDangNhap"));
                    tk.setMaNhanVien(rs.getString("maNhanVien") != null ? rs.getString("maNhanVien") : "");
                    list.add(tk);
                }
            }
        }
        return list;
    }

    // Lấy toàn bộ danh sách tài khoản
    public List<TaiKhoan> getAllTaiKhoan() throws SQLException {
        List<TaiKhoan> list = new ArrayList<>();
        String sql = "SELECT tenDangNhap, maNhanVien FROM TaiKhoan";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                TaiKhoan tk = new TaiKhoan();
                tk.setTenDangNhap(rs.getString("tenDangNhap"));
                tk.setMaNhanVien(rs.getString("maNhanVien") != null ? rs.getString("maNhanVien") : "");
                list.add(tk);
            }
        }
        return list;
    }
}