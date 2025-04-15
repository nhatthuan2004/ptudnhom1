package dao;

import model.ChitietPhieuDichVu;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import ConnectDB.ConnectDB;

public class ChitietPhieuDichVu_Dao {
    private final ConnectDB connectDB;

    public ChitietPhieuDichVu_Dao() {
        this.connectDB = ConnectDB.getInstance();
    }

    // Thêm chi tiết phiếu dịch vụ
    public boolean themChitietPhieuDichVu(ChitietPhieuDichVu ctpdv) throws SQLException {
        String sql = "INSERT INTO ChitietPhieuDichVu (maPhieuDichVu, maDichVu, soLuong, donGia) VALUES (?, ?, ?, ?)";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // Kiểm tra giá trị đầu vào
            if (ctpdv.getMaPhieuDichVu().isEmpty() || ctpdv.getMaDichVu().isEmpty()) {
                throw new SQLException("Mã phiếu dịch vụ hoặc mã dịch vụ không được để trống!");
            }

            ps.setString(1, ctpdv.getMaPhieuDichVu());
            ps.setString(2, ctpdv.getMaDichVu());
            ps.setInt(3, ctpdv.getSoLuong());
            ps.setDouble(4, ctpdv.getDonGia());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            // Xử lý lỗi cụ thể nếu vi phạm ràng buộc khóa ngoại
            if (e.getSQLState().equals("23000")) { // Lỗi vi phạm ràng buộc khóa ngoại
                throw new SQLException("Lỗi: Mã phiếu dịch vụ hoặc mã dịch vụ không hợp lệ (không tồn tại trong bảng PhieuDichVu hoặc DichVu)!", e);
            }
            throw e; // Ném lại lỗi nếu không phải lỗi khóa ngoại
        }
    }

    // Xóa chi tiết phiếu dịch vụ
    public boolean xoaChitietPhieuDichVu(String maPhieuDichVu, String maDichVu) throws SQLException {
        String sql = "DELETE FROM ChitietPhieuDichVu WHERE maPhieuDichVu = ? AND maDichVu = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // Kiểm tra giá trị đầu vào
            if (maPhieuDichVu == null || maPhieuDichVu.isEmpty() || maDichVu == null || maDichVu.isEmpty()) {
                throw new SQLException("Mã phiếu dịch vụ hoặc mã dịch vụ không được để trống!");
            }

            ps.setString(1, maPhieuDichVu);
            ps.setString(2, maDichVu);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Sửa chi tiết phiếu dịch vụ
    public boolean suaChitietPhieuDichVu(ChitietPhieuDichVu ctpdv) throws SQLException {
        String sql = "UPDATE ChitietPhieuDichVu SET soLuong = ?, donGia = ? WHERE maPhieuDichVu = ? AND maDichVu = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // Kiểm tra giá trị đầu vào
            if (ctpdv.getMaPhieuDichVu().isEmpty() || ctpdv.getMaDichVu().isEmpty()) {
                throw new SQLException("Mã phiếu dịch vụ hoặc mã dịch vụ không được để trống!");
            }

            ps.setInt(1, ctpdv.getSoLuong());
            ps.setDouble(2, ctpdv.getDonGia());
            ps.setString(3, ctpdv.getMaPhieuDichVu());
            ps.setString(4, ctpdv.getMaDichVu());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Tìm kiếm chi tiết phiếu dịch vụ
    public List<ChitietPhieuDichVu> timKiemChitietPhieuDichVu(String tuKhoa) throws SQLException {
        List<ChitietPhieuDichVu> list = new ArrayList<>();
        String sql = "SELECT * FROM ChitietPhieuDichVu WHERE maPhieuDichVu LIKE ? OR maDichVu LIKE ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // Kiểm tra từ khóa
            String searchKeyword = tuKhoa != null ? tuKhoa.trim() : "";
            ps.setString(1, "%" + searchKeyword + "%");
            ps.setString(2, "%" + searchKeyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ChitietPhieuDichVu ctpdv = new ChitietPhieuDichVu(
                            rs.getString("maPhieuDichVu"),
                            rs.getString("maDichVu"),
                            rs.getInt("soLuong"),
                            rs.getDouble("donGia")
                    );
                    list.add(ctpdv);
                }
            }
        }
        return list;
    }

    // Lấy toàn bộ danh sách chi tiết phiếu dịch vụ
    public List<ChitietPhieuDichVu> getAllChitietPhieuDichVu() throws SQLException {
        List<ChitietPhieuDichVu> list = new ArrayList<>();
        String sql = "SELECT * FROM ChitietPhieuDichVu";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ChitietPhieuDichVu ctpdv = new ChitietPhieuDichVu(
                        rs.getString("maPhieuDichVu"),
                        rs.getString("maDichVu"),
                        rs.getInt("soLuong"),
                        rs.getDouble("donGia")
                );
                list.add(ctpdv);
            }
        }
        return list;
    }

    // Lấy danh sách chi tiết phiếu dịch vụ theo mã phiếu dịch vụ
    public List<ChitietPhieuDichVu> getChitietPhieuDichVuByMaPhieu(String maPhieuDichVu) throws SQLException {
        List<ChitietPhieuDichVu> list = new ArrayList<>();
        String sql = "SELECT * FROM ChitietPhieuDichVu WHERE maPhieuDichVu = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhieuDichVu);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ChitietPhieuDichVu ctpdv = new ChitietPhieuDichVu(
                            rs.getString("maPhieuDichVu"),
                            rs.getString("maDichVu"),
                            rs.getInt("soLuong"),
                            rs.getDouble("donGia")
                    );
                    list.add(ctpdv);
                }
            }
        }
        return list;
    }
}