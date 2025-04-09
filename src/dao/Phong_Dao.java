package dao;

import model.Phong;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Phong_Dao {
    private Connection connection;

    public Phong_Dao() {
        this.connection = connectDB.ConnectDB.getConnection(); // lấy connection từ singleton
    }

    // Thêm phòng
    public boolean themPhong(Phong p) throws SQLException {
        String sql = "INSERT INTO Phong (maPhong, loaiPhong, giaPhong, trangThai, dondep, vitri, soNguoiToiDa, moTa) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
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
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, maPhong);
            return ps.executeUpdate() > 0;
        }
    }

    // Sửa thông tin phòng
    public boolean suaPhong(Phong p) throws SQLException {
        String sql = "UPDATE Phong SET loaiPhong = ?, giaPhong = ?, trangThai = ?, dondep = ?, vitri = ?, soNguoiToiDa = ?, moTa = ? WHERE maPhong = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
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
        String sql = "SELECT * FROM Phong WHERE maPhong LIKE ? OR loaiPhong LIKE ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + tuKhoa + "%");
            ps.setString(2, "%" + tuKhoa + "%");
            ResultSet rs = ps.executeQuery();
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

    // Lấy toàn bộ danh sách phòng
    public List<Phong> getAllPhong() {
        List<Phong> list = new ArrayList<>();
        String sql = "SELECT * FROM Phong";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
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
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi truy vấn danh sách phòng.");
            e.printStackTrace();
        }
        return list;
    }

    // Sinh mã phòng tự động (giả sử: P001, P002, ...)
    public String getNextMaPhong() throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(maPhong, 2, 3) AS INT)) AS maxMa FROM Phong";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int nextMa = rs.getInt("maxMa") + 1;
                return "P" + String.format("%03d", nextMa);
            }
        }
        return "P001";
    }
}
