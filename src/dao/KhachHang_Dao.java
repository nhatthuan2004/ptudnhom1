package dao;

import model.KhachHang;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import ConnectDB.ConnectDB;
import java.time.LocalDate;

public class KhachHang_Dao {
    private final ConnectDB connectDB;

    public KhachHang_Dao() {
        this.connectDB = ConnectDB.getInstance();
    }

    public KhachHang getKhachHangBySDT(String sdt) throws SQLException {
        String query = "SELECT maKhachHang, tenKhachHang, cccd, soDienThoai, diaChi, quoctich, ngaySinh, gioitinh, email " +
                      "FROM KhachHang WHERE soDienThoai = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, sdt);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new KhachHang(
                        rs.getString("maKhachHang"),
                        rs.getString("tenKhachHang"),
                        rs.getString("cccd"),
                        rs.getString("soDienThoai"),
                        rs.getString("diaChi"),
                        rs.getString("quoctich"),
                        rs.getDate("ngaySinh") != null ? rs.getDate("ngaySinh").toLocalDate() : null,
                        rs.getString("gioitinh"),
                        rs.getString("email")
                    );
                }
            }
        }
        return null;
    }

    public KhachHang getKhachHangByCCCD(String cccd) throws SQLException {
        String query = "SELECT maKhachHang, tenKhachHang, cccd, soDienThoai, diaChi, quoctich, ngaySinh, gioitinh, email " +
                      "FROM KhachHang WHERE cccd = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, cccd);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new KhachHang(
                        rs.getString("maKhachHang"),
                        rs.getString("tenKhachHang"),
                        rs.getString("cccd"),
                        rs.getString("soDienThoai"),
                        rs.getString("diaChi"),
                        rs.getString("quoctich"),
                        rs.getDate("ngaySinh") != null ? rs.getDate("ngaySinh").toLocalDate() : null,
                        rs.getString("gioitinh"),
                        rs.getString("email")
                    );
                }
            }
        }
        return null;
    }

    public boolean themKhachHang(KhachHang khachHang) throws SQLException {
        String query = "INSERT INTO KhachHang (maKhachHang, tenKhachHang, cccd, soDienThoai, email, diaChi, quoctich, gioitinh, ngaySinh) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, khachHang.getMaKhachHang());
            stmt.setString(2, khachHang.getTenKhachHang());
            stmt.setString(3, khachHang.getCccd());
            stmt.setString(4, khachHang.getSoDienThoai());
            stmt.setString(5, khachHang.getEmail());
            stmt.setString(6, khachHang.getDiaChi());
            stmt.setString(7, khachHang.getQuocTich());
            stmt.setString(8, khachHang.getGioiTinh());
            stmt.setDate(9, khachHang.getNgaySinh() != null ? Date.valueOf(khachHang.getNgaySinh()) : null);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean suaKhachHang(KhachHang kh) throws SQLException {
        String sql = "UPDATE KhachHang SET tenKhachHang = ?, soDienThoai = ?, email = ?, diaChi = ?, cccd = ?, ngaySinh = ?, quoctich = ?, gioitinh = ? " +
                     "WHERE maKhachHang = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kh.getTenKhachHang());
            ps.setString(2, kh.getSoDienThoai());
            ps.setString(3, kh.getEmail());
            ps.setString(4, kh.getDiaChi());
            ps.setString(5, kh.getCccd());
            ps.setDate(6, kh.getNgaySinh() != null ? Date.valueOf(kh.getNgaySinh()) : null);
            ps.setString(7, kh.getQuocTich());
            ps.setString(8, kh.getGioiTinh());
            ps.setString(9, kh.getMaKhachHang());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean xoaKhachHang(String maKhachHang) throws SQLException {
        String sql = "DELETE FROM KhachHang WHERE maKhachHang = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKhachHang);
            return ps.executeUpdate() > 0;
        }
    }

    public List<KhachHang> timKiemKhachHang(String tuKhoa) throws SQLException {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT maKhachHang, tenKhachHang, cccd, soDienThoai, diaChi, quoctich, ngaySinh, gioitinh, email " +
                     "FROM KhachHang WHERE maKhachHang LIKE ? OR tenKhachHang LIKE ? OR soDienThoai LIKE ? OR cccd LIKE ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + tuKhoa + "%");
            ps.setString(2, "%" + tuKhoa + "%");
            ps.setString(3, "%" + tuKhoa + "%");
            ps.setString(4, "%" + tuKhoa + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    KhachHang kh = new KhachHang(
                        rs.getString("maKhachHang"),
                        rs.getString("tenKhachHang"),
                        rs.getString("cccd"),
                        rs.getString("soDienThoai"),
                        rs.getString("diaChi"),
                        rs.getString("quoctich"),
                        rs.getDate("ngaySinh") != null ? rs.getDate("ngaySinh").toLocalDate() : null,
                        rs.getString("gioitinh"),
                        rs.getString("email")
                    );
                    list.add(kh);
                }
            }
        }
        return list;
    }

    public List<KhachHang> getAllKhachHang() throws SQLException {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT maKhachHang, tenKhachHang, cccd, soDienThoai, diaChi, quoctich, ngaySinh, gioitinh, email " +
                     "FROM KhachHang";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                KhachHang kh = new KhachHang(
                    rs.getString("maKhachHang"),
                    rs.getString("tenKhachHang"),
                    rs.getString("cccd"),
                    rs.getString("soDienThoai"),
                    rs.getString("diaChi"),
                    rs.getString("quoctich"),
                    rs.getDate("ngaySinh") != null ? rs.getDate("ngaySinh").toLocalDate() : null,
                    rs.getString("gioitinh"),
                    rs.getString("email")
                );
                list.add(kh);
            }
        }
        return list;
    }

    public KhachHang getKhachHangById(String maKhachHang) throws SQLException {
        String query = "SELECT maKhachHang, tenKhachHang, cccd, soDienThoai, diaChi, quoctich, ngaySinh, gioitinh, email " +
                      "FROM KhachHang WHERE maKhachHang = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, maKhachHang);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new KhachHang(
                        rs.getString("maKhachHang"),
                        rs.getString("tenKhachHang"),
                        rs.getString("cccd"),
                        rs.getString("soDienThoai"),
                        rs.getString("diaChi"),
                        rs.getString("quoctich"),
                        rs.getDate("ngaySinh") != null ? rs.getDate("ngaySinh").toLocalDate() : null,
                        rs.getString("gioitinh"),
                        rs.getString("email")
                    );
                }
            }
        }
        return null;
    }

    public String getNextMaKhachHang() throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(maKhachHang, 3, LEN(maKhachHang) - 2) AS INT)) AS maxMa " +
                     "FROM KhachHang WHERE maKhachHang LIKE 'KH[0-9]%'";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next() && rs.getInt("maxMa") > 0) {
                int nextMa = rs.getInt("maxMa") + 1;
                return "KH" + String.format("%03d", nextMa);
            }
        }
        return "KH001";
    }
}