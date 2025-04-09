package dao;

import model.KhachHang;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhachHang_Dao {
    private Connection connection;

    public KhachHang_Dao() {
        this.connection = connectDB.ConnectDB.getConnection(); // lấy connection từ singleton
    }

    public KhachHang getKhachHangBySDT(String sdt) throws SQLException {
        String query = "SELECT * FROM KhachHang WHERE soDienThoai = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, sdt);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new KhachHang(
                        rs.getString("maKhachHang"),
                        rs.getString("tenKhachHang"),
                        rs.getString("soDienThoai"),
                        rs.getString("email"),
                        rs.getString("diaChi"),
                        rs.getString("cccd"),
                        rs.getDate("ngaySinh").toLocalDate(),
                        rs.getString("quoctich"),
                        rs.getString("gioitinh")
                );
            }
        }
        return null;
    }

    public KhachHang getKhachHangByCCCD(String cccd) throws SQLException {
        String query = "SELECT * FROM KhachHang WHERE cccd = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, cccd);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new KhachHang(
                        rs.getString("maKhachHang"),
                        rs.getString("tenKhachHang"),
                        rs.getString("soDienThoai"),
                        rs.getString("email"),
                        rs.getString("diaChi"),
                        rs.getString("cccd"),
                        rs.getDate("ngaySinh").toLocalDate(),
                        rs.getString("quoctich"),
                        rs.getString("gioitinh")
                );
            }
        }
        return null;
    }

    public boolean themKhachHang(KhachHang khachHang) throws SQLException {
        String query = "INSERT INTO KhachHang (maKhachHang, tenKhachHang, cccd, soDienThoai, email, diaChi, quoctich, gioitinh, ngaySinh) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, khachHang.getMaKhachHang());
            stmt.setString(2, khachHang.getTenKhachHang());
            stmt.setString(3, khachHang.getCccd());
            stmt.setString(4, khachHang.getSoDienThoai());
            stmt.setString(5, khachHang.getEmail());
            stmt.setString(6, khachHang.getDiaChi());
            stmt.setString(7, khachHang.getQuocTich());
            stmt.setString(8, khachHang.getGioiTinh());
            stmt.setDate(9, Date.valueOf(khachHang.getNgaySinh()));
            return stmt.executeUpdate() > 0; // Trả về true nếu thêm thành công
        }
    }

    public boolean suaKhachHang(KhachHang kh) throws SQLException {
        String sql = "UPDATE KhachHang SET tenKhachHang = ?, soDienThoai = ?, email = ?, diaChi = ?, cccd = ?, ngaySinh = ?, quoctich = ?, gioitinh = ? WHERE maKhachHang = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, kh.getTenKhachHang());
            ps.setString(2, kh.getSoDienThoai());
            ps.setString(3, kh.getEmail());
            ps.setString(4, kh.getDiaChi());
            ps.setString(5, kh.getCccd());
            ps.setDate(6, Date.valueOf(kh.getNgaySinh()));
            ps.setString(7, kh.getQuocTich());
            ps.setString(8, kh.getGioiTinh());
            ps.setString(9, kh.getMaKhachHang());
            return ps.executeUpdate() > 0; // Trả về true nếu cập nhật thành công
        }
    }

    public boolean xoaKhachHang(String maKhachHang) throws SQLException {
        String sql = "DELETE FROM KhachHang WHERE maKhachHang = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, maKhachHang);
            return ps.executeUpdate() > 0;
        }
    }

    public List<KhachHang> timKiemKhachHang(String tuKhoa) throws SQLException {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang WHERE maKhachHang LIKE ? OR tenKhachHang LIKE ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + tuKhoa + "%");
            ps.setString(2, "%" + tuKhoa + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                KhachHang kh = new KhachHang(
                        rs.getString("maKhachHang"),
                        rs.getString("tenKhachHang"),
                        rs.getString("soDienThoai"),
                        rs.getString("email"),
                        rs.getString("diaChi"),
                        rs.getString("cccd"),
                        rs.getDate("ngaySinh").toLocalDate(),
                        rs.getString("quoctich"),
                        rs.getString("gioitinh")
                );
                list.add(kh);
            }
        }
        return list;
    }

    public List<KhachHang> getAllKhachHang() {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                KhachHang kh = new KhachHang(
                        rs.getString("maKhachHang"),
                        rs.getString("tenKhachHang"),
                        rs.getString("soDienThoai"),
                        rs.getString("email"),
                        rs.getString("diaChi"),
                        rs.getString("cccd"),
                        rs.getDate("ngaySinh").toLocalDate(),
                        rs.getString("quoctich"),
                        rs.getString("gioitinh")
                );
                list.add(kh);
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi truy vấn khách hàng.");
            e.printStackTrace();
        }
        return list;
    }

    public String getNextMaKhachHang() throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(maKhachHang, 3, 3) AS INT)) AS maxMa FROM KhachHang";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int nextMa = rs.getInt("maxMa") + 1;
                return "KH" + String.format("%03d", nextMa);
            }
        }
        return "KH001";
    }
}