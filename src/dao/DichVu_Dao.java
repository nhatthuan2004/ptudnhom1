package dao;

import model.DichVu;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DichVu_Dao {
    private Connection connection;

    public DichVu_Dao() {
        this.connection = connectDB.ConnectDB.getConnection(); // lấy connection từ singleton
    }

    // Thêm dịch vụ
    public boolean themDichVu(DichVu dv) throws SQLException {
        String sql = "INSERT INTO DichVu (maDichVu, tenDichVu, moTa, gia, trangthai) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, dv.getMaDichVu());
            ps.setString(2, dv.getTenDichVu());
            ps.setString(3, dv.getMoTa());
            ps.setDouble(4, dv.getGia());
            ps.setString(5, dv.getTrangThai());
            return ps.executeUpdate() > 0;
        }
    }

    // Xóa dịch vụ
    public boolean xoaDichVu(String maDichVu) throws SQLException {
        String sql = "DELETE FROM DichVu WHERE maDichVu = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, maDichVu);
            return ps.executeUpdate() > 0;
        }
    }

    // Sửa thông tin dịch vụ
    public boolean suaDichVu(DichVu dv) throws SQLException {
        String sql = "UPDATE DichVu SET tenDichVu = ?, moTa = ?, gia = ?, trangthai = ? WHERE maDichVu = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, dv.getTenDichVu());
            ps.setString(2, dv.getMoTa());
            ps.setDouble(3, dv.getGia());
            ps.setString(4, dv.getTrangThai());
            ps.setString(5, dv.getMaDichVu());
            return ps.executeUpdate() > 0;
        }
    }

    // Tìm kiếm dịch vụ
    public List<DichVu> timKiemDichVu(String tuKhoa) throws SQLException {
        List<DichVu> list = new ArrayList<>();
        String sql = "SELECT * FROM DichVu WHERE maDichVu LIKE ? OR tenDichVu LIKE ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + tuKhoa + "%");
            ps.setString(2, "%" + tuKhoa + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DichVu dv = new DichVu(
                    rs.getString("maDichVu"),
                    rs.getString("tenDichVu"),
                    rs.getString("moTa"),
                    rs.getDouble("gia"),
                    rs.getString("trangthai")
                );
                list.add(dv);
            }
        }
        return list;
    }

    // Lấy toàn bộ danh sách dịch vụ
    public List<DichVu> getAllDichVu() {
        List<DichVu> list = new ArrayList<>();
        String sql = "SELECT * FROM DichVu";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                DichVu dv = new DichVu(
                    rs.getString("maDichVu"),
                    rs.getString("tenDichVu"),
                    rs.getString("moTa"),
                    rs.getDouble("gia"),
                    rs.getString("trangthai")
                );
                list.add(dv);
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi truy vấn dịch vụ.");
            e.printStackTrace();
        }
        return list;
    }

    // Sinh mã dịch vụ tự động
    public String getNextMaDichVu() throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(maDichVu, 3, 3) AS INT)) AS maxMa FROM DichVu";
        try (Statement stmt = connection.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int nextMa = rs.getInt("maxMa") + 1;
                return "DV" + String.format("%03d", nextMa); // Sinh mã DV001, DV002, ...
            }
        }
        return "DV001"; // Nếu chưa có dịch vụ nào
    }
}
