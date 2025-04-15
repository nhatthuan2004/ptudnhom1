package dao;

import model.HoaDon;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ConnectDB.ConnectDB;

public class HoaDon_Dao {
    private final ConnectDB connectDB;

    public HoaDon_Dao() {
        this.connectDB = ConnectDB.getInstance();
    }

    // Thêm hóa đơn
    public boolean themHoaDon(HoaDon hd) throws SQLException {
        String sql = "INSERT INTO HoaDon (maHoaDon, ngayLap, hinhThucThanhToan, tongTien, trangThai, maKhachHang, maNhanVien) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // Kiểm tra giá trị đầu vào
            if (hd.getMaHoaDon().isEmpty() || hd.getHinhThucThanhToan().isEmpty() ||
                hd.getMaKhachHang().isEmpty() || hd.getMaNhanVien().isEmpty()) {
                throw new SQLException("Các trường bắt buộc (maHoaDon, hinhThucThanhToan, maKhachHang, maNhanVien) không được để trống!");
            }

            ps.setString(1, hd.getMaHoaDon());
            ps.setTimestamp(2, hd.getNgayLap() != null ? Timestamp.valueOf(hd.getNgayLap()) : Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(3, hd.getHinhThucThanhToan());
            ps.setDouble(4, hd.getTongTien());
            ps.setBoolean(5, hd.getTrangThai());
            ps.setString(6, hd.getMaKhachHang());
            ps.setString(7, hd.getMaNhanVien());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            // Xử lý lỗi nếu vi phạm ràng buộc khóa ngoại
            if (e.getSQLState().equals("23000")) {
                throw new SQLException("Lỗi: Mã khách hàng hoặc mã nhân viên không hợp lệ (không tồn tại trong bảng KhachHang hoặc NhanVien)!", e);
            }
            throw e;
        }
    }

    // Xóa hóa đơn
    public boolean xoaHoaDon(String maHoaDon) throws SQLException {
        String sql = "DELETE FROM HoaDon WHERE maHoaDon = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (maHoaDon == null || maHoaDon.isEmpty()) {
                throw new SQLException("Mã hóa đơn không được để trống!");
            }

            ps.setString(1, maHoaDon);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Sửa hóa đơn
    public boolean suaHoaDon(HoaDon hd) throws SQLException {
        String sql = "UPDATE HoaDon SET ngayLap = ?, hinhThucThanhToan = ?, tongTien = ?, trangThai = ?, maKhachHang = ?, maNhanVien = ? WHERE maHoaDon = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (hd.getMaHoaDon().isEmpty() || hd.getHinhThucThanhToan().isEmpty() ||
                hd.getMaKhachHang().isEmpty() || hd.getMaNhanVien().isEmpty()) {
                throw new SQLException("Các trường bắt buộc (maHoaDon, hinhThucThanhToan, maKhachHang, maNhanVien) không được để trống!");
            }

            ps.setTimestamp(1, hd.getNgayLap() != null ? Timestamp.valueOf(hd.getNgayLap()) : Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(2, hd.getHinhThucThanhToan());
            ps.setDouble(3, hd.getTongTien());
            ps.setBoolean(4, hd.getTrangThai());
            ps.setString(5, hd.getMaKhachHang());
            ps.setString(6, hd.getMaNhanVien());
            ps.setString(7, hd.getMaHoaDon());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                throw new SQLException("Lỗi: Mã khách hàng hoặc mã nhân viên không hợp lệ (không tồn tại trong bảng KhachHang hoặc NhanVien)!", e);
            }
            throw e;
        }
    }

    // Tìm kiếm hóa đơn
    public List<HoaDon> timKiemHoaDon(String tuKhoa) throws SQLException {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM HoaDon WHERE maHoaDon LIKE ? OR maKhachHang LIKE ? OR maNhanVien LIKE ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String searchKeyword = tuKhoa != null ? tuKhoa.trim() : "";
            ps.setString(1, "%" + searchKeyword + "%");
            ps.setString(2, "%" + searchKeyword + "%");
            ps.setString(3, "%" + searchKeyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    HoaDon hd = new HoaDon(
                            rs.getString("maHoaDon"),
                            rs.getTimestamp("ngayLap") != null ? rs.getTimestamp("ngayLap").toLocalDateTime() : null,
                            rs.getString("hinhThucThanhToan"),
                            rs.getDouble("tongTien"),
                            rs.getBoolean("trangThai"),
                            rs.getString("maKhachHang"),
                            rs.getString("maNhanVien")
                    );
                    list.add(hd);
                }
            }
        }
        return list;
    }

    // Lấy toàn bộ danh sách hóa đơn
    public List<HoaDon> getAllHoaDon() throws SQLException {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM HoaDon";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                HoaDon hd = new HoaDon(
                        rs.getString("maHoaDon"),
                        rs.getTimestamp("ngayLap") != null ? rs.getTimestamp("ngayLap").toLocalDateTime() : null,
                        rs.getString("hinhThucThanhToan"),
                        rs.getDouble("tongTien"),
                        rs.getBoolean("trangThai"),
                        rs.getString("maKhachHang"),
                        rs.getString("maNhanVien")
                );
                list.add(hd);
            }
        }
        return list;
    }

    // Sinh mã hóa đơn tự động
    public String getNextMaHoaDon() throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(maHoaDon, 3, LEN(maHoaDon) - 2) AS INT)) AS maxMa FROM HoaDon WHERE maHoaDon LIKE 'HD[0-9]%'";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next() && rs.getObject("maxMa") != null) {
                int nextMa = rs.getInt("maxMa") + 1;
                return "HD" + String.format("%03d", nextMa);
            }
            return "HD001"; // Trả về mã đầu tiên nếu bảng rỗng
        }
    }

    // Tính tổng doanh thu trong một khoảng thời gian
    public double tinhDoanhThu(LocalDate tuNgay, LocalDate denNgay) throws SQLException {
        String sql = "SELECT SUM(tongTien) AS tongDoanhThu FROM HoaDon WHERE ngayLap BETWEEN ? AND ? AND trangThai = 1";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(tuNgay.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(denNgay.atTime(23, 59, 59)));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("tongDoanhThu");
                }
            }
        }
        return 0.0;
    }
}