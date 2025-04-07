package dao;

import model.KhuyenMai;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhuyenMai_Dao {
    private Connection connection;

    public KhuyenMai_Dao() {
        this.connection = connectDB.ConnectDB.getConnection();
    }

    // Thêm chương trình khuyến mãi
    public boolean themKhuyenMai(KhuyenMai km) throws SQLException {
        String sql = "INSERT INTO ChuongTrinhKhuyenMai (maChuongTrinhKhuyenMai, tenChuongTrinhKhuyenMai, chietKhau, moTaChuongTrinhKhuyenMai, trangThai) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, km.getMaChuongTrinhKhuyenMai());
            ps.setString(2, km.getTenChuongTrinhKhuyenMai());
            ps.setDouble(3, km.getChietKhau());
            ps.setString(4, km.getMoTaChuongTrinhKhuyenMai());
            ps.setBoolean(5, km.isTrangThai());
            return ps.executeUpdate() > 0;
        }
    }

    // Xóa chương trình khuyến mãi
    public boolean xoaKhuyenMai(String maChuongTrinh) throws SQLException {
        String sql = "DELETE FROM ChuongTrinhKhuyenMai WHERE maChuongTrinhKhuyenMai = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, maChuongTrinh);
            return ps.executeUpdate() > 0;
        }
    }

    // Sửa thông tin chương trình khuyến mãi
    public boolean suaKhuyenMai(KhuyenMai km) throws SQLException {
        String sql = "UPDATE ChuongTrinhKhuyenMai SET tenChuongTrinhKhuyenMai = ?, chietKhau = ?, moTaChuongTrinhKhuyenMai = ?, trangThai = ? WHERE maChuongTrinhKhuyenMai = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, km.getTenChuongTrinhKhuyenMai());
            ps.setDouble(2, km.getChietKhau());
            ps.setString(3, km.getMoTaChuongTrinhKhuyenMai());
            ps.setBoolean(4, km.isTrangThai());
            ps.setString(5, km.getMaChuongTrinhKhuyenMai());
            return ps.executeUpdate() > 0;
        }
    }

    // Tìm kiếm chương trình khuyến mãi
    public List<KhuyenMai> timKiemKhuyenMai(String tuKhoa) throws SQLException {
        List<KhuyenMai> list = new ArrayList<>();
        String sql = "SELECT * FROM ChuongTrinhKhuyenMai WHERE maChuongTrinhKhuyenMai LIKE ? OR tenChuongTrinhKhuyenMai LIKE ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + tuKhoa + "%");
            ps.setString(2, "%" + tuKhoa + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                KhuyenMai km = new KhuyenMai(
                    rs.getString("maChuongTrinhKhuyenMai"),
                    rs.getString("tenChuongTrinhKhuyenMai"),
                    rs.getDouble("chietKhau"),
                    rs.getString("moTaChuongTrinhKhuyenMai"),
                    rs.getBoolean("trangThai")
                );
                list.add(km);
            }
        }
        return list;
    }

    // Lấy danh sách toàn bộ chương trình khuyến mãi
    public List<KhuyenMai> getAllKhuyenMai() {
        List<KhuyenMai> list = new ArrayList<>();
        String sql = "SELECT * FROM ChuongTrinhKhuyenMai";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                KhuyenMai km = new KhuyenMai(
                    rs.getString("maChuongTrinhKhuyenMai"),
                    rs.getString("tenChuongTrinhKhuyenMai"),
                    rs.getDouble("chietKhau"),
                    rs.getString("moTaChuongTrinhKhuyenMai"),
                    rs.getBoolean("trangThai")
                );
                list.add(km);
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi truy vấn chương trình khuyến mãi.");
            e.printStackTrace();
        }
        return list;
    }

    // Sinh mã khuyến mãi tự động
    public String getNextMaKhuyenMai() throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(maChuongTrinhKhuyenMai, 3, 3) AS INT)) AS maxMa FROM ChuongTrinhKhuyenMai";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int nextMa = rs.getInt("maxMa") + 1;
                return "KM" + String.format("%03d", nextMa);
            }
        }
        return "KM001";
    }
}
