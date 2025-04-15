package dao;

import model.Phong;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import ConnectDB.ConnectDB;

public class Phong_Dao {
    private final ConnectDB connectDB;

    public Phong_Dao() {
        this.connectDB = ConnectDB.getInstance();
    }

    // Thêm phòng
    public boolean themPhong(Phong p) throws SQLException {
        String sql = "INSERT INTO Phong (maPhong, loaiPhong, giaPhong, trangThai, dondep, vitri, soNguoiToiDa, moTa) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getMaPhong());
            ps.setString(2, p.getLoaiPhong());
            ps.setDouble(3, p.getGiaPhong());
            ps.setString(4, p.getTrangThai());
            ps.setString(5, p.getDonDep());
            ps.setString(6, p.getViTri());
            ps.setInt(7, p.getSoNguoiToiDa());
            ps.setString(8, p.getMoTa());
            return ps.executeUpdate() > 0;
        }
    }

    // Xóa phòng
    public boolean xoaPhong(String maPhong) throws SQLException {
        String sql = "DELETE FROM Phong WHERE maPhong = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhong);
            return ps.executeUpdate() > 0;
        }
    }

    // Sửa thông tin phòng
    public boolean suaPhong(Phong p) throws SQLException {
        String sql = "UPDATE Phong SET loaiPhong = ?, giaPhong = ?, trangThai = ?, dondep = ?, vitri = ?, soNguoiToiDa = ?, moTa = ? " +
                     "WHERE maPhong = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getLoaiPhong());
            ps.setDouble(2, p.getGiaPhong());
            ps.setString(3, p.getTrangThai());
            ps.setString(4, p.getDonDep());
            ps.setString(5, p.getViTri());
            ps.setInt(6, p.getSoNguoiToiDa());
            ps.setString(7, p.getMoTa());
            ps.setString(8, p.getMaPhong());
            return ps.executeUpdate() > 0;
        }
    }

    // Tìm kiếm phòng
    public List<Phong> timKiemPhong(String tuKhoa) throws SQLException {
        List<Phong> list = new ArrayList<>();
        String sql = "SELECT maPhong, loaiPhong, giaPhong, trangThai, dondep, vitri, soNguoiToiDa, moTa " +
                     "FROM Phong WHERE maPhong LIKE ? OR loaiPhong LIKE ? OR vitri LIKE ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + tuKhoa + "%");
            ps.setString(2, "%" + tuKhoa + "%");
            ps.setString(3, "%" + tuKhoa + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Phong p = new Phong(
                        rs.getString("maPhong"),
                        rs.getString("loaiPhong"),
                        rs.getDouble("giaPhong"),
                        rs.getString("trangThai"),
                        rs.getString("dondep"),
                        rs.getString("vitri"),
                        rs.getInt("soNguoiToiDa"),
                        rs.getString("moTa")
                    );
                    list.add(p);
                }
            }
        }
        return list;
    }

    // Lấy toàn bộ danh sách phòng
    public List<Phong> getAllPhong() throws SQLException {
        List<Phong> list = new ArrayList<>();
        String sql = "SELECT maPhong, loaiPhong, giaPhong, trangThai, dondep, vitri, soNguoiToiDa, moTa FROM Phong";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Phong p = new Phong(
                    rs.getString("maPhong"),
                    rs.getString("loaiPhong"),
                    rs.getDouble("giaPhong"),
                    rs.getString("trangThai"),
                    rs.getString("dondep"),
                    rs.getString("vitri"),
                    rs.getInt("soNguoiToiDa"),
                    rs.getString("moTa")
                );
                list.add(p);
            }
        }
        return list;
    }

    // Lấy phòng theo mã
    public Phong getPhongByMa(String maPhong) throws SQLException {
        String sql = "SELECT maPhong, loaiPhong, giaPhong, trangThai, dondep, vitri, soNguoiToiDa, moTa " +
                     "FROM Phong WHERE maPhong = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhong);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Phong(
                        rs.getString("maPhong"),
                        rs.getString("loaiPhong"),
                        rs.getDouble("giaPhong"),
                        rs.getString("trangThai"),
                        rs.getString("dondep"),
                        rs.getString("vitri"),
                        rs.getInt("soNguoiToiDa"),
                        rs.getString("moTa")
                    );
                }
            }
        }
        return null;
    }

    // Sinh mã phòng tự động
    public String getNextMaPhong() throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(maPhong, 2, LEN(maPhong) - 1) AS INT)) AS maxMa " +
                     "FROM Phong WHERE maPhong LIKE 'P[0-9]%'";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next() && rs.getInt("maxMa") > 0) {
                int nextMa = rs.getInt("maxMa") + 1;
                return "P" + String.format("%03d", nextMa);
            }
        }
        return "P001";
    }
}