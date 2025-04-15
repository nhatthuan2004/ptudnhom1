package dao;

import model.NhanVien;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import ConnectDB.ConnectDB;

public class NhanVien_Dao {
    private final ConnectDB connectDB;

    public NhanVien_Dao() {
        this.connectDB = ConnectDB.getInstance();
    }

    // Thêm nhân viên
    public boolean themNhanVien(NhanVien nv) throws SQLException {
        String sql = "INSERT INTO NhanVien (maNhanVien, tenNhanVien, soDienThoai, gioiTinh, diaChi, chucVu, luong) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nv.getMaNhanVien());
            ps.setString(2, nv.getTenNhanVien());
            ps.setString(3, nv.getSoDienThoai());
            ps.setBoolean(4, nv.isGioiTinh());
            ps.setString(5, nv.getDiaChi());
            ps.setString(6, nv.getChucVu());
            ps.setDouble(7, nv.getLuong());
            return ps.executeUpdate() > 0;
        }
    }

    // Xóa nhân viên
    public boolean xoaNhanVien(String maNhanVien) throws SQLException {
        String sql = "DELETE FROM NhanVien WHERE maNhanVien = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNhanVien);
            return ps.executeUpdate() > 0;
        }
    }

    // Sửa nhân viên
    public boolean suaNhanVien(NhanVien nv) throws SQLException {
        String sql = "UPDATE NhanVien SET tenNhanVien = ?, soDienThoai = ?, gioiTinh = ?, diaChi = ?, chucVu = ?, luong = ? " +
                     "WHERE maNhanVien = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nv.getTenNhanVien());
            ps.setString(2, nv.getSoDienThoai());
            ps.setBoolean(3, nv.isGioiTinh());
            ps.setString(4, nv.getDiaChi());
            ps.setString(5, nv.getChucVu());
            ps.setDouble(6, nv.getLuong());
            ps.setString(7, nv.getMaNhanVien());
            return ps.executeUpdate() > 0;
        }
    }

    // Tìm kiếm nhân viên
    public List<NhanVien> timKiemNhanVien(String tuKhoa) throws SQLException {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT maNhanVien, tenNhanVien, soDienThoai, gioiTinh, diaChi, chucVu, luong " +
                     "FROM NhanVien WHERE maNhanVien LIKE ? OR tenNhanVien LIKE ? OR soDienThoai LIKE ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + tuKhoa + "%");
            ps.setString(2, "%" + tuKhoa + "%");
            ps.setString(3, "%" + tuKhoa + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    NhanVien nv = new NhanVien(
                        rs.getString("maNhanVien"),
                        rs.getString("tenNhanVien"),
                        rs.getString("soDienThoai"),
                        rs.getBoolean("gioiTinh"),
                        rs.getString("diaChi"),
                        rs.getString("chucVu"),
                        rs.getDouble("luong")
                    );
                    list.add(nv);
                }
            }
        }
        return list;
    }

    // Lấy toàn bộ danh sách nhân viên
    public List<NhanVien> getAllNhanVien() throws SQLException {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT maNhanVien, tenNhanVien, soDienThoai, gioiTinh, diaChi, chucVu, luong FROM NhanVien";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                NhanVien nv = new NhanVien(
                    rs.getString("maNhanVien"),
                    rs.getString("tenNhanVien"),
                    rs.getString("soDienThoai"),
                    rs.getBoolean("gioiTinh"),
                    rs.getString("diaChi"),
                    rs.getString("chucVu"),
                    rs.getDouble("luong")
                );
                list.add(nv);
            }
        }
        return list;
    }

    // Lấy nhân viên theo mã
    public NhanVien getNhanVienByMa(String maNhanVien) throws SQLException {
        String sql = "SELECT maNhanVien, tenNhanVien, soDienThoai, gioiTinh, diaChi, chucVu, luong " +
                     "FROM NhanVien WHERE maNhanVien = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNhanVien);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new NhanVien(
                        rs.getString("maNhanVien"),
                        rs.getString("tenNhanVien"),
                        rs.getString("soDienThoai"),
                        rs.getBoolean("gioiTinh"),
                        rs.getString("diaChi"),
                        rs.getString("chucVu"),
                        rs.getDouble("luong")
                    );
                }
            }
        }
        return null;
    }

    // Sinh mã nhân viên tự động
    public String getNextMaNhanVien() throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(maNhanVien, 3, LEN(maNhanVien) - 2) AS INT)) AS maxMa " +
                     "FROM NhanVien WHERE maNhanVien LIKE 'NV[0-9]%'";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next() && rs.getInt("maxMa") > 0) {
                int nextMa = rs.getInt("maxMa") + 1;
                return "NV" + String.format("%03d", nextMa);
            }
        }
        return "NV001";
    }
}