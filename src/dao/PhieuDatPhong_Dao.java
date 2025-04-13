package dao;

import model.PhieuDatPhong;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;

public class PhieuDatPhong_Dao {
    private Connection connection;

    public PhieuDatPhong_Dao() throws SQLException {
        ConnectDB.getInstance();
		this.connection = ConnectDB.getConnection();
    }

    // Thêm phiếu đặt phòng
    public boolean addPhieuDatPhong(PhieuDatPhong phieu) throws SQLException {
        String sql = "INSERT INTO PhieuDatPhong (maDatPhong, ngayDen, ngayDi, ngayDat, soLuongNguoi, trangThai, maKhachHang) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, phieu.getMaDatPhong());
            stmt.setDate(2, java.sql.Date.valueOf(phieu.getNgayDen()));
            stmt.setDate(3, java.sql.Date.valueOf(phieu.getNgayDi()));
            stmt.setDate(4, java.sql.Date.valueOf(phieu.getNgayDat()));
            stmt.setInt(5, phieu.getSoLuongNguoi());
            stmt.setString(6, phieu.getTrangThai());
            stmt.setString(7, phieu.getMaKhachHang());
            return stmt.executeUpdate() > 0;
        }
    }

    // Lấy danh sách phiếu đặt phòng
    public List<PhieuDatPhong> getAllPhieuDatPhong() throws SQLException {
        List<PhieuDatPhong> list = new ArrayList<>();
        String sql = "SELECT * FROM PhieuDatPhong";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                PhieuDatPhong phieu = new PhieuDatPhong(
                        rs.getString("maDatPhong"),
                        rs.getDate("ngayDen").toLocalDate(),
                        rs.getDate("ngayDi").toLocalDate(),
                        rs.getDate("ngayDat").toLocalDate(),
                        rs.getInt("soLuongNguoi"),
                        rs.getString("trangThai"),
                        rs.getString("maKhachHang")
                );
                list.add(phieu);
            }
        }
        return list;
    }

    // Lấy mã đặt phòng tiếp theo
    public String getNextMaDatPhong() throws SQLException {
        String sql = "SELECT MAX(maDatPhong) AS maxMa FROM PhieuDatPhong";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                String maxMa = rs.getString("maxMa");
                if (maxMa == null) {
                    return "DP001";
                }
                int number = Integer.parseInt(maxMa.replace("DP", "")) + 1;
                return String.format("DP%03d", number);
            }
        }
        return "DP001";
    }
    
 // Phương thức xóa phiếu đặt phòng và chi tiết phiếu đặt phòng
    public void deletePhieuDatPhong(String maDatPhong) throws SQLException {
        // Xóa chi tiết phiếu đặt phòng trước
        String sqlDeleteChiTiet = "DELETE FROM ChitietPhieuDatPhong WHERE maDatPhong = ?";
        try (PreparedStatement psChiTiet = connection.prepareStatement(sqlDeleteChiTiet)) {
            psChiTiet.setString(1, maDatPhong);
            psChiTiet.executeUpdate();
        }

        // Sau đó xóa phiếu đặt phòng
        String sqlDeletePhieu = "DELETE FROM PhieuDatPhong WHERE maDatPhong = ?";
        try (PreparedStatement psPhieu = connection.prepareStatement(sqlDeletePhieu)) {
            psPhieu.setString(1, maDatPhong);
            psPhieu.executeUpdate();
        }
    }

    // Phương thức tìm kiếm
    public List<PhieuDatPhong> searchPhieuDatPhong(String tuKhoa) throws SQLException {
        List<PhieuDatPhong> result = new ArrayList<>();
        String sql = "SELECT * FROM PhieuDatPhong WHERE maDatPhong LIKE ? OR maKhachHang LIKE ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + tuKhoa + "%");
            ps.setString(2, "%" + tuKhoa + "%");
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PhieuDatPhong phieu = new PhieuDatPhong(
                        rs.getString("maDatPhong"),
                        rs.getDate("ngayDen") != null ? rs.getDate("ngayDen").toLocalDate() : null,
                        rs.getDate("ngayDi") != null ? rs.getDate("ngayDi").toLocalDate() : null,
                        rs.getDate("ngayDat") != null ? rs.getDate("ngayDat").toLocalDate() : null,
                        rs.getInt("soLuongNguoi"),
                        rs.getString("trangThai"),
                        rs.getString("maKhachHang")
                    );
                    result.add(phieu);
                }
            }
        }
        return result;
    }
}