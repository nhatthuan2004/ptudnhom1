package dao;

import model.KhuyenMai;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import ConnectDB.ConnectDB;

public class KhuyenMai_Dao {
    private final ConnectDB connectDB;

    public KhuyenMai_Dao() {
        this.connectDB = ConnectDB.getInstance();
    }

    // Thêm chương trình khuyến mãi
    public boolean themKhuyenMai(KhuyenMai km) throws SQLException {
        String sql = "INSERT INTO ChuongTrinhKhuyenMai (maChuongTrinhKhuyenMai, tenChuongTrinhKhuyenMai, chietKhau, moTaChuongTrinhKhuyenMai, trangThai) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // Kiểm tra giá trị đầu vào
            if (km.getMaChuongTrinhKhuyenMai().isEmpty()) {
                throw new SQLException("Mã chương trình khuyến mãi không được để trống!");
            }
            if (km.getTenChuongTrinhKhuyenMai().isEmpty()) {
                throw new SQLException("Tên chương trình khuyến mãi không được để trống!");
            }

            ps.setString(1, km.getMaChuongTrinhKhuyenMai());
            ps.setString(2, km.getTenChuongTrinhKhuyenMai());
            ps.setDouble(3, km.getChietKhau());
            ps.setString(4, km.getMoTaChuongTrinhKhuyenMai());
            ps.setBoolean(5, km.isTrangThai());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Xóa chương trình khuyến mãi
    public boolean xoaKhuyenMai(String maChuongTrinh) throws SQLException {
        String sql = "DELETE FROM ChuongTrinhKhuyenMai WHERE maChuongTrinhKhuyenMai = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (maChuongTrinh == null || maChuongTrinh.isEmpty()) {
                throw new SQLException("Mã chương trình khuyến mãi không được để trống!");
            }

            ps.setString(1, maChuongTrinh);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Sửa thông tin chương trình khuyến mãi
    public boolean suaKhuyenMai(KhuyenMai km) throws SQLException {
        String sql = "UPDATE ChuongTrinhKhuyenMai SET tenChuongTrinhKhuyenMai = ?, chietKhau = ?, moTaChuongTrinhKhuyenMai = ?, trangThai = ? WHERE maChuongTrinhKhuyenMai = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (km.getMaChuongTrinhKhuyenMai().isEmpty()) {
                throw new SQLException("Mã chương trình khuyến mãi không được để trống!");
            }
            if (km.getTenChuongTrinhKhuyenMai().isEmpty()) {
                throw new SQLException("Tên chương trình khuyến mãi không được để trống!");
            }

            ps.setString(1, km.getTenChuongTrinhKhuyenMai());
            ps.setDouble(2, km.getChietKhau());
            ps.setString(3, km.getMoTaChuongTrinhKhuyenMai());
            ps.setBoolean(4, km.isTrangThai());
            ps.setString(5, km.getMaChuongTrinhKhuyenMai());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Tìm kiếm chương trình khuyến mãi
    public List<KhuyenMai> timKiemKhuyenMai(String tuKhoa) throws SQLException {
        List<KhuyenMai> list = new ArrayList<>();
        String sql = "SELECT * FROM ChuongTrinhKhuyenMai WHERE maChuongTrinhKhuyenMai LIKE ? OR tenChuongTrinhKhuyenMai LIKE ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String searchKeyword = tuKhoa != null ? tuKhoa.trim() : "";
            ps.setString(1, "%" + searchKeyword + "%");
            ps.setString(2, "%" + searchKeyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
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
        }
        return list;
    }

    // Lấy danh sách toàn bộ chương trình khuyến mãi
    public List<KhuyenMai> getAllKhuyenMai() throws SQLException {
        List<KhuyenMai> list = new ArrayList<>();
        String sql = "SELECT * FROM ChuongTrinhKhuyenMai";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
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

    // Lấy danh sách chương trình khuyến mãi đang hoạt động
    public List<KhuyenMai> getActiveKhuyenMai() throws SQLException {
        List<KhuyenMai> list = new ArrayList<>();
        String sql = "SELECT * FROM ChuongTrinhKhuyenMai WHERE trangThai = 1";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
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

    // Sinh mã khuyến mãi tự động
    public String getNextMaKhuyenMai() throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(maChuongTrinhKhuyenMai, 3, LEN(maChuongTrinhKhuyenMai) - 2) AS INT)) AS maxMa " +
                     "FROM ChuongTrinhKhuyenMai WHERE maChuongTrinhKhuyenMai LIKE 'KM[0-9]%'";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next() && rs.getObject("maxMa") != null) {
                int nextMa = rs.getInt("maxMa") + 1;
                return "KM" + String.format("%03d", nextMa);
            }
            return "KM001"; // Trả về mã đầu tiên nếu bảng rỗng
        }
    }
}