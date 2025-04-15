package dao;

import model.PhieuDichVu;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ConnectDB.ConnectDB;

public class PhieuDichVu_Dao {
    private final ConnectDB connectDB;

    public PhieuDichVu_Dao() {
        this.connectDB = ConnectDB.getInstance();
    }

    // Thêm phiếu dịch vụ
    public boolean themPhieuDichVu(PhieuDichVu phieuDichVu) throws SQLException {
        String sql = "INSERT INTO PhieuDichVu (maPhieuDichVu, maHoaDon, ngayTao) VALUES (?, ?, ?)";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // Kiểm tra giá trị đầu vào
            if (phieuDichVu.getMaPhieuDichVu().isEmpty()) {
                throw new SQLException("Mã phiếu dịch vụ không được để trống!");
            }

            ps.setString(1, phieuDichVu.getMaPhieuDichVu());
            ps.setString(2, phieuDichVu.getMaHoaDon());
            ps.setDate(3, phieuDichVu.getNgayTao() != null ? Date.valueOf(phieuDichVu.getNgayTao()) : null);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            // Xử lý lỗi nếu vi phạm ràng buộc khóa ngoại
            if (e.getSQLState().equals("23000")) {
                throw new SQLException("Lỗi: Mã hóa đơn không hợp lệ (không tồn tại trong bảng HoaDon)!", e);
            }
            throw e;
        }
    }

    // Xóa phiếu dịch vụ
    public boolean xoaPhieuDichVu(String maPhieuDichVu) throws SQLException {
        String sql = "DELETE FROM PhieuDichVu WHERE maPhieuDichVu = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (maPhieuDichVu == null || maPhieuDichVu.isEmpty()) {
                throw new SQLException("Mã phiếu dịch vụ không được để trống!");
            }

            ps.setString(1, maPhieuDichVu);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Sửa phiếu dịch vụ
    public boolean suaPhieuDichVu(PhieuDichVu phieuDichVu) throws SQLException {
        String sql = "UPDATE PhieuDichVu SET maHoaDon = ?, ngayTao = ? WHERE maPhieuDichVu = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (phieuDichVu.getMaPhieuDichVu().isEmpty()) {
                throw new SQLException("Mã phiếu dịch vụ không được để trống!");
            }

            ps.setString(1, phieuDichVu.getMaHoaDon());
            ps.setDate(2, phieuDichVu.getNgayTao() != null ? Date.valueOf(phieuDichVu.getNgayTao()) : null);
            ps.setString(3, phieuDichVu.getMaPhieuDichVu());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                throw new SQLException("Lỗi: Mã hóa đơn không hợp lệ (không tồn tại trong bảng HoaDon)!", e);
            }
            throw e;
        }
    }

    // Tìm kiếm phiếu dịch vụ
    public List<PhieuDichVu> timKiemPhieuDichVu(String tuKhoa) throws SQLException {
        List<PhieuDichVu> list = new ArrayList<>();
        String sql = "SELECT * FROM PhieuDichVu WHERE maPhieuDichVu LIKE ? OR maHoaDon LIKE ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String searchKeyword = tuKhoa != null ? tuKhoa.trim() : "";
            ps.setString(1, "%" + searchKeyword + "%");
            ps.setString(2, "%" + searchKeyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PhieuDichVu pdv = new PhieuDichVu(
                            rs.getString("maPhieuDichVu"),
                            rs.getString("maHoaDon"),
                            rs.getDate("ngayTao") != null ? rs.getDate("ngayTao").toLocalDate() : null
                    );
                    list.add(pdv);
                }
            }
        }
        return list;
    }

    // Lấy toàn bộ danh sách phiếu dịch vụ
    public List<PhieuDichVu> getAllPhieuDichVu() throws SQLException {
        List<PhieuDichVu> list = new ArrayList<>();
        String sql = "SELECT * FROM PhieuDichVu";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PhieuDichVu pdv = new PhieuDichVu(
                        rs.getString("maPhieuDichVu"),
                        rs.getString("maHoaDon"),
                        rs.getDate("ngayTao") != null ? rs.getDate("ngayTao").toLocalDate() : null
                );
                list.add(pdv);
            }
        }
        return list;
    }

    // Sinh mã phiếu dịch vụ tự động
    public String getNextMaPhieuDichVu() throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(maPhieuDichVu, 4, LEN(maPhieuDichVu) - 3) AS INT)) AS maxMa " +
                     "FROM PhieuDichVu WHERE maPhieuDichVu LIKE 'PDV[0-9]%'";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next() && rs.getObject("maxMa") != null) {
                int nextMa = rs.getInt("maxMa") + 1;
                return "PDV" + String.format("%03d", nextMa);
            }
            return "PDV001"; // Trả về mã đầu tiên nếu bảng rỗng
        }
    }
}