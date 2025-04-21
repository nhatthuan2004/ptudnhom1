package dao;

import model.ChitietPhieuDatPhong;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import ConnectDB.ConnectDB;

public class ChitietPhieuDatPhong_Dao {
    private final ConnectDB connectDB;

    public ChitietPhieuDatPhong_Dao() {
        this.connectDB = ConnectDB.getInstance();
    }

    // Thêm chi tiết phiếu đặt phòng
    public boolean themChitietPhieuDatPhong(ChitietPhieuDatPhong ctpdp) throws SQLException {
        String sql = "INSERT INTO ChitietPhieuDatPhong (maDatPhong, maPhong, moTa, trangThai, tienPhong, tienDichVu, thanhTien, soLuong, daThanhToan) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
        	ps.setString(1, ctpdp.getMaDatPhong());
            ps.setString(2, ctpdp.getMaPhong());            
            ps.setString(3, ctpdp.getMoTa());
            ps.setString(4, ctpdp.getTrangThai());
            ps.setDouble(5, ctpdp.getTienPhong());
            ps.setDouble(6, ctpdp.getTienDichVu());
            ps.setDouble(7, ctpdp.getThanhTien());
            ps.setInt(8, ctpdp.getSoLuong());
            ps.setBoolean(9, ctpdp.getDaThanhToan());
            return ps.executeUpdate() > 0;
        }
    }

    // Xóa chi tiết phiếu đặt phòng
    public boolean xoaChitietPhieuDatPhong(String maDatPhong, String maPhong) throws SQLException {
        String sql = "DELETE FROM ChitietPhieuDatPhong WHERE maDatPhong = ? AND maPhong = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maDatPhong);
            ps.setString(2, maPhong);
            return ps.executeUpdate() > 0;
        }
    }

    // Sửa chi tiết phiếu đặt phòng
    public boolean suaChitietPhieuDatPhong(ChitietPhieuDatPhong ctpdp) throws SQLException {
        String sql = "UPDATE ChitietPhieuDatPhong SET moTa = ?, trangThai = ?, tienPhong = ?, tienDichVu = ?, " +
                     "thanhTien = ?, soLuong = ?, daThanhToan = ? WHERE maDatPhong = ? AND maPhong = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ctpdp.getMoTa());
            ps.setString(2, ctpdp.getTrangThai());
            ps.setDouble(3, ctpdp.getTienPhong());
            ps.setDouble(4, ctpdp.getTienDichVu());
            ps.setDouble(5, ctpdp.getThanhTien());
            ps.setInt(6, ctpdp.getSoLuong());
            ps.setBoolean(7, ctpdp.getDaThanhToan());
            ps.setString(8, ctpdp.getMaDatPhong());
            ps.setString(9, ctpdp.getMaPhong());
            return ps.executeUpdate() > 0;
        }
    }

    // Tìm kiếm chi tiết phiếu đặt phòng theo maDatPhong
    public List<ChitietPhieuDatPhong> timKiemChitietPhieuDatPhong(String maDatPhong) throws SQLException {
        List<ChitietPhieuDatPhong> list = new ArrayList<>();
        String sql = "SELECT maDatPhong, maPhong, moTa, trangThai, tienPhong, tienDichVu, thanhTien, soLuong, daThanhToan " +
                     "FROM ChitietPhieuDatPhong WHERE maDatPhong = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maDatPhong);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ChitietPhieuDatPhong ctpdp = new ChitietPhieuDatPhong(
                        rs.getString("maDatPhong"),
                        rs.getString("maPhong"),
                        rs.getString("moTa"),
                        rs.getString("trangThai"),
                        rs.getDouble("tienPhong"),
                        rs.getDouble("tienDichVu"),
                        rs.getDouble("thanhTien"),
                        rs.getInt("soLuong"),
                        rs.getBoolean("daThanhToan")
                    );
                    list.add(ctpdp);
                }
            }
        }
        return list;
    }

    // Lấy toàn bộ danh sách chi tiết phiếu đặt phòng trực tiếp từ bảng
    public List<ChitietPhieuDatPhong> getAllChitietPhieuDatPhong() throws SQLException {
        List<ChitietPhieuDatPhong> list = new ArrayList<>();
        String sql = "SELECT maDatPhong, maPhong, moTa, trangThai, tienPhong, tienDichVu, thanhTien, soLuong, daThanhToan " +
                     "FROM ChitietPhieuDatPhong";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ChitietPhieuDatPhong ctpdp = new ChitietPhieuDatPhong(
                    rs.getString("maDatPhong"),
                    rs.getString("maPhong"),
                    rs.getString("moTa"),
                    rs.getString("trangThai"),
                    rs.getDouble("tienPhong"),
                    rs.getDouble("tienDichVu"),
                    rs.getDouble("thanhTien"),
                    rs.getInt("soLuong"),
                    rs.getBoolean("daThanhToan")
                );
                list.add(ctpdp);
            }
        }
        return list;
    }

    // Gọi stored procedure sp_ThanhToanTuPhieuDat
    public boolean thanhToanTuPhieuDat(String maDatPhong, String maPhong, String maNhanVien, 
                                      String hinhThucThanhToan, String maChuongTrinhKhuyenMai) throws SQLException {
        String sql = "{CALL sp_ThanhToanTuPhieuDat(?, ?, ?, ?, ?)}";
        try (Connection conn = connectDB.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, maDatPhong);
            cs.setString(2, maPhong);
            cs.setString(3, maNhanVien);
            cs.setString(4, hinhThucThanhToan);
            cs.setString(5, maChuongTrinhKhuyenMai != null && !maChuongTrinhKhuyenMai.trim().isEmpty() ? 
                           maChuongTrinhKhuyenMai : null);
            return cs.executeUpdate() > 0;
        }
    }


    public void deleteChitietPhieuDatPhongByMaDatPhong(String maDatPhong) throws SQLException {
        String sql = "DELETE FROM ChitietPhieuDatPhong WHERE maDatPhong = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maDatPhong);
            stmt.executeUpdate();
        }
    }

}