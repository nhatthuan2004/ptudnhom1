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

    // Thêm khách hàng
    public boolean themKhachHang(KhachHang kh) throws SQLException {
        String sql = "INSERT INTO KhachHang (maKhachHang, tenKhachHang, soDienThoai, email, diaChi, cccd, ngaySinh, quoctich, gioitinh) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, kh.getMaKhachHang());
            ps.setString(2, kh.getTenKhachHang());
            ps.setString(3, kh.getSoDienThoai());
            ps.setString(4, kh.getEmail());
            ps.setString(5, kh.getDiaChi());
            ps.setString(6, kh.getCccd());
            ps.setDate(7, Date.valueOf(kh.getNgaySinh()));
            ps.setString(8, kh.getQuocTich());
            ps.setString(9, kh.getGioiTinh());
            return ps.executeUpdate() > 0;
        }
    }
    
    // Xóa khách hàng
    public boolean xoaKhachHang(String maKhachHang) throws SQLException {
        String sql = "DELETE FROM KhachHang WHERE maKhachHang = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, maKhachHang);
            return ps.executeUpdate() > 0;
        }
    }

    // Sửa thông tin khách hàng
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
            return ps.executeUpdate() > 0;
        }
    }

    // Tìm kiếm khách hàng
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

    // Lấy toàn bộ danh sách khách hàng
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

    // Phương thức sinh mã khách hàng tự động
    public String getNextMaKhachHang() throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(maKhachHang, 3, 3) AS INT)) AS maxMa FROM KhachHang";
        try (Statement stmt = connection.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int nextMa = rs.getInt("maxMa") + 1;
                return "KH" + String.format("%03d", nextMa); // Sinh mã KH001, KH002, ...
            }
        }
        return "KH001"; // Nếu chưa có khách hàng nào, sinh mã KH001
    }
}
