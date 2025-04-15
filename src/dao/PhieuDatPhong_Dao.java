package dao;

import model.PhieuDatPhong;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ConnectDB.ConnectDB;

public class PhieuDatPhong_Dao {
    private final ConnectDB connectDB;

    public PhieuDatPhong_Dao() {
        this.connectDB = ConnectDB.getInstance();
    }

    // Thêm phiếu đặt phòng
    public boolean themPhieuDatPhong(PhieuDatPhong pdp) throws SQLException {
        String sql = "INSERT INTO PhieuDatPhong (maDatPhong, ngayDen, ngayDi, ngayDat, soLuongNguoi, trangThai, maKhachHang, maHoaDon) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // Kiểm tra giá trị đầu vào
            if (pdp.getMaDatPhong().isEmpty()) {
                throw new SQLException("Mã phiếu đặt phòng không được để trống!");
            }

            ps.setString(1, pdp.getMaDatPhong());
            ps.setDate(2, pdp.getNgayDen() != null ? Date.valueOf(pdp.getNgayDen()) : null);
            ps.setDate(3, pdp.getNgayDi() != null ? Date.valueOf(pdp.getNgayDi()) : null);
            ps.setDate(4, pdp.getNgayDat() != null ? Date.valueOf(pdp.getNgayDat()) : null);
            ps.setInt(5, pdp.getSoLuongNguoi());
            ps.setString(6, pdp.getTrangThai());
            ps.setString(7, pdp.getMaKhachHang());
            ps.setString(8, pdp.getMaHoaDon());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            // Xử lý lỗi nếu vi phạm ràng buộc khóa ngoại
            if (e.getSQLState().equals("23000")) {
                throw new SQLException("Lỗi: Mã khách hàng hoặc mã hóa đơn không hợp lệ (không tồn tại trong bảng KhachHang hoặc HoaDon)!", e);
            }
            throw e;
        }
    }

    // Xóa phiếu đặt phòng
    public boolean xoaPhieuDatPhong(String maDatPhong) throws SQLException {
        String sql = "DELETE FROM PhieuDatPhong WHERE maDatPhong = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (maDatPhong == null || maDatPhong.isEmpty()) {
                throw new SQLException("Mã phiếu đặt phòng không được để trống!");
            }

            ps.setString(1, maDatPhong);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Sửa phiếu đặt phòng
    public boolean suaPhieuDatPhong(PhieuDatPhong pdp) throws SQLException {
        String sql = "UPDATE PhieuDatPhong SET ngayDen = ?, ngayDi = ?, ngayDat = ?, soLuongNguoi = ?, trangThai = ?, maKhachHang = ?, maHoaDon = ? WHERE maDatPhong = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (pdp.getMaDatPhong().isEmpty()) {
                throw new SQLException("Mã phiếu đặt phòng không được để trống!");
            }

            ps.setDate(1, pdp.getNgayDen() != null ? Date.valueOf(pdp.getNgayDen()) : null);
            ps.setDate(2, pdp.getNgayDi() != null ? Date.valueOf(pdp.getNgayDi()) : null);
            ps.setDate(3, pdp.getNgayDat() != null ? Date.valueOf(pdp.getNgayDat()) : null);
            ps.setInt(4, pdp.getSoLuongNguoi());
            ps.setString(5, pdp.getTrangThai());
            ps.setString(6, pdp.getMaKhachHang());
            ps.setString(7, pdp.getMaHoaDon());
            ps.setString(8, pdp.getMaDatPhong());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                throw new SQLException("Lỗi: Mã khách hàng hoặc mã hóa đơn không hợp lệ (không tồn tại trong bảng KhachHang hoặc HoaDon)!", e);
            }
            throw e;
        }
    }

    // Tìm kiếm phiếu đặt phòng
    public List<PhieuDatPhong> timKiemPhieuDatPhong(String tuKhoa) throws SQLException {
        List<PhieuDatPhong> list = new ArrayList<>();
        String sql = "SELECT * FROM PhieuDatPhong WHERE maDatPhong LIKE ? OR maKhachHang LIKE ? OR maHoaDon LIKE ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String searchKeyword = tuKhoa != null ? tuKhoa.trim() : "";
            ps.setString(1, "%" + searchKeyword + "%");
            ps.setString(2, "%" + searchKeyword + "%");
            ps.setString(3, "%" + searchKeyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PhieuDatPhong pdp = new PhieuDatPhong(
                            rs.getString("maDatPhong"),
                            rs.getDate("ngayDen") != null ? rs.getDate("ngayDen").toLocalDate() : null,
                            rs.getDate("ngayDi") != null ? rs.getDate("ngayDi").toLocalDate() : null,
                            rs.getDate("ngayDat") != null ? rs.getDate("ngayDat").toLocalDate() : null,
                            rs.getInt("soLuongNguoi"),
                            rs.getString("trangThai"),
                            rs.getString("maKhachHang"),
                            rs.getString("maHoaDon")
                    );
                    list.add(pdp);
                }
            }
        }
        return list;
    }

    // Lấy toàn bộ danh sách phiếu đặt phòng
    public List<PhieuDatPhong> getAllPhieuDatPhong() throws SQLException {
        List<PhieuDatPhong> list = new ArrayList<>();
        String sql = "SELECT * FROM PhieuDatPhong";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PhieuDatPhong pdp = new PhieuDatPhong(
                        rs.getString("maDatPhong"),
                        rs.getDate("ngayDen") != null ? rs.getDate("ngayDen").toLocalDate() : null,
                        rs.getDate("ngayDi") != null ? rs.getDate("ngayDi").toLocalDate() : null,
                        rs.getDate("ngayDat") != null ? rs.getDate("ngayDat").toLocalDate() : null,
                        rs.getInt("soLuongNguoi"),
                        rs.getString("trangThai"),
                        rs.getString("maKhachHang"),
                        rs.getString("maHoaDon")
                );
                list.add(pdp);
            }
        }
        return list;
    }

    // Lấy danh sách phiếu đặt phòng theo trạng thái
    public List<PhieuDatPhong> getPhieuDatPhongByTrangThai(String trangThai) throws SQLException {
        List<PhieuDatPhong> list = new ArrayList<>();
        String sql = "SELECT * FROM PhieuDatPhong WHERE trangThai = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThai);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PhieuDatPhong pdp = new PhieuDatPhong(
                            rs.getString("maDatPhong"),
                            rs.getDate("ngayDen") != null ? rs.getDate("ngayDen").toLocalDate() : null,
                            rs.getDate("ngayDi") != null ? rs.getDate("ngayDi").toLocalDate() : null,
                            rs.getDate("ngayDat") != null ? rs.getDate("ngayDat").toLocalDate() : null,
                            rs.getInt("soLuongNguoi"),
                            rs.getString("trangThai"),
                            rs.getString("maKhachHang"),
                            rs.getString("maHoaDon")
                    );
                    list.add(pdp);
                }
            }
        }
        return list;
    }

    // Sinh mã phiếu đặt phòng tự động
    public String getNextMaPhieuDatPhong() throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(maDatPhong, 3, LEN(maDatPhong) - 2) AS INT)) AS maxMa " +
                     "FROM PhieuDatPhong WHERE maDatPhong LIKE 'DP[0-9]%'";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next() && rs.getObject("maxMa") != null) {
                int nextMa = rs.getInt("maxMa") + 1;
                return "DP" + String.format("%03d", nextMa);
            }
            return "DP001"; // Trả về mã đầu tiên nếu bảng rỗng
        }
    }
}