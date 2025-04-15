package dao;

import model.DichVu;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import ConnectDB.ConnectDB;

public class DichVu_Dao {
    private final ConnectDB connectDB;

    public DichVu_Dao() {
        this.connectDB = ConnectDB.getInstance();
    }

    // Thêm dịch vụ
    public boolean themDichVu(DichVu dv) throws SQLException {
        String sql = "INSERT INTO DichVu (maDichVu, tenDichVu, moTa, gia, trangThai) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
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
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maDichVu);
            return ps.executeUpdate() > 0;
        }
    }

    // Sửa thông tin dịch vụ
    public boolean suaDichVu(DichVu dv) throws SQLException {
        String sql = "UPDATE DichVu SET tenDichVu = ?, moTa = ?, gia = ?, trangThai = ? WHERE maDichVu = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
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
        String sql = "SELECT maDichVu, tenDichVu, moTa, gia, trangThai FROM DichVu " +
                     "WHERE maDichVu LIKE ? OR tenDichVu LIKE ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + tuKhoa + "%");
            ps.setString(2, "%" + tuKhoa + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DichVu dv = new DichVu(
                        rs.getString("maDichVu"),
                        rs.getString("tenDichVu"),
                        rs.getString("moTa"),
                        rs.getDouble("gia"),
                        rs.getString("trangThai")
                    );
                    list.add(dv);
                }
            }
        }
        return list;
    }

    // Lấy toàn bộ danh sách dịch vụ
    public List<DichVu> getAllDichVu() throws SQLException {
        List<DichVu> list = new ArrayList<>();
        String sql = "SELECT maDichVu, tenDichVu, moTa, gia, trangThai FROM DichVu";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                DichVu dv = new DichVu(
                    rs.getString("maDichVu"),
                    rs.getString("tenDichVu"),
                    rs.getString("moTa"),
                    rs.getDouble("gia"),
                    rs.getString("trangThai")
                );
                list.add(dv);
            }
        }
        return list;
    }

    // Sinh mã dịch vụ tự động
    public String getNextMaDichVu() throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(maDichVu, 3, LEN(maDichVu) - 2) AS INT)) AS maxMa FROM DichVu WHERE maDichVu LIKE 'DV[0-9]%'";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next() && rs.getInt("maxMa") > 0) {
                int nextMa = rs.getInt("maxMa") + 1;
                return "DV" + String.format("%03d", nextMa);
            }
        }
        return "DV001";
    }
}