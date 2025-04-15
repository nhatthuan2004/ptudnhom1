package dao;

import model.ChitietHoaDon;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import ConnectDB.ConnectDB;

public class ChitietHoaDon_Dao {
    private final ConnectDB connectDB;

    public ChitietHoaDon_Dao() {
        this.connectDB = ConnectDB.getInstance();
    }
    public boolean kiemTraTonTaiChiTietPhong(String maHoaDon, String maPhong) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ChitietHoaDon WHERE maHoaDon = ? AND maPhong = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maHoaDon);
            stmt.setString(2, maPhong);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }
    public List<ChitietHoaDon> getChiTietDichVuByMaHoaDon(String maHoaDon) throws SQLException {
        List<ChitietHoaDon> list = new ArrayList<>();
        String sql = "SELECT maHoaDon, maPhong, thueVat, soNgay, khuyenMai, tienPhong, tienDichVu, " +
                     "maChuongTrinhKhuyenMai, maDatPhong, maPhieuDichVu FROM ChitietHoaDon " +
                     "WHERE maHoaDon = ? AND maPhieuDichVu IS NOT NULL";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHoaDon);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ChitietHoaDon cthd = new ChitietHoaDon(
                        rs.getString("maHoaDon"),
                        rs.getString("maPhong"),
                        rs.getDouble("thueVat"),
                        rs.getInt("soNgay"),
                        rs.getDouble("khuyenMai"),
                        rs.getDouble("tienPhong"),
                        rs.getDouble("tienDichVu"),
                        rs.getString("maChuongTrinhKhuyenMai"),
                        rs.getString("maDatPhong"),
                        rs.getString("maPhieuDichVu")
                    );
                    list.add(cthd);
                }
            }
        }
        return list;
    }

    // Thêm chi tiết hóa đơn (gọi stored procedure)
    public boolean themChitietHoaDon(ChitietHoaDon cthd, String maNhanVien, String hinhThucThanhToan) throws SQLException {
        String sql = "{CALL sp_ThanhToanTuPhieuDat(?, ?, ?, ?, ?)}";
        try (Connection conn = connectDB.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, cthd.getMaDatPhong());
            cs.setString(2, cthd.getMaPhong());
            cs.setString(3, maNhanVien);
            cs.setString(4, hinhThucThanhToan);
            cs.setString(5, cthd.getMaChuongTrinhKhuyenMai());
            return cs.executeUpdate() > 0;
        }
    }
    public boolean kiemTraTonTaiPhieuDichVu(String maHoaDon, String maPhieuDichVu) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ChitietHoaDon WHERE maHoaDon = ? AND maPhieuDichVu = ?";
        try (PreparedStatement stmt = ConnectDB.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setString(1, maHoaDon);
            stmt.setString(2, maPhieuDichVu);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public boolean kiemTraTonTai(String maHoaDon, String maPhieuDichVu) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ChitietHoaDon WHERE maHoaDon = ? AND maPhieuDichVu = ?";
        PreparedStatement stmt = ConnectDB.getInstance().getConnection().prepareStatement(sql);
        stmt.setString(1, maHoaDon);
        stmt.setString(2, maPhieuDichVu);
        ResultSet rs = stmt.executeQuery();
        return rs.next() && rs.getInt(1) > 0;
    }

    // Thêm chi tiết hóa đơn trực tiếp
    public boolean themChitietHoaDon(ChitietHoaDon cthd) throws SQLException {
        String sql = "INSERT INTO ChitietHoaDon (maHoaDon, maPhong, thueVat, soNgay, khuyenMai, tienPhong, " +
                     "tienDichVu, maChuongTrinhKhuyenMai, maDatPhong, maPhieuDichVu) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cthd.getMaHoaDon());
            ps.setString(2, cthd.getMaPhong());
            ps.setDouble(3, cthd.getThueVat());
            ps.setInt(4, cthd.getSoNgay());
            ps.setDouble(5, cthd.getKhuyenMai());
            ps.setDouble(6, cthd.getTienPhong());
            ps.setDouble(7, cthd.getTienDichVu());
            ps.setString(8, cthd.getMaChuongTrinhKhuyenMai());
            ps.setString(9, cthd.getMaDatPhong());
            ps.setString(10, cthd.getMaPhieuDichVu());
            return ps.executeUpdate() > 0;
        }
    }

    // Sửa chi tiết hóa đơn
    public boolean suaChitietHoaDon(ChitietHoaDon cthd) throws SQLException {
        String sql = "UPDATE ChitietHoaDon SET thueVat = ?, soNgay = ?, khuyenMai = ?, tienPhong = ?, tienDichVu = ?, " +
                     "maChuongTrinhKhuyenMai = ?, maDatPhong = ?, maPhieuDichVu = ? " +
                     "WHERE maHoaDon = ? AND maPhong = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, cthd.getThueVat());
            ps.setInt(2, cthd.getSoNgay());
            ps.setDouble(3, cthd.getKhuyenMai());
            ps.setDouble(4, cthd.getTienPhong());
            ps.setDouble(5, cthd.getTienDichVu());
            ps.setString(6, cthd.getMaChuongTrinhKhuyenMai());
            ps.setString(7, cthd.getMaDatPhong());
            ps.setString(8, cthd.getMaPhieuDichVu());
            ps.setString(9, cthd.getMaHoaDon());
            ps.setString(10, cthd.getMaPhong());
            return ps.executeUpdate() > 0;
        }
    }

    // Xóa chi tiết hóa đơn
    public boolean xoaChitietHoaDon(String maHoaDon, String maPhong) throws SQLException {
        String sql = "DELETE FROM ChitietHoaDon WHERE maHoaDon = ? AND maPhong = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHoaDon);
            ps.setString(2, maPhong);
            return ps.executeUpdate() > 0;
        }
    }

    // Lấy tất cả chi tiết hóa đơn thông qua view
    public List<ChitietHoaDon> getAllChitietHoaDon() throws SQLException {
        List<ChitietHoaDon> list = new ArrayList<>();
        String sql = "SELECT maHoaDon, maPhong, thueVat, soNgay, khuyenMai, tienPhong, tienDichVu, " +
                     "maChuongTrinhKhuyenMai, maDatPhong, maPhieuDichVu FROM vw_ChiTietHoaDon";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ChitietHoaDon cthd = new ChitietHoaDon(
                    rs.getString("maHoaDon"),
                    rs.getString("maPhong"),
                    rs.getDouble("thueVat"),
                    rs.getInt("soNgay"),
                    rs.getDouble("khuyenMai"),
                    rs.getDouble("tienPhong"),
                    rs.getDouble("tienDichVu"),
                    rs.getString("maChuongTrinhKhuyenMai"),
                    rs.getString("maDatPhong"),
                    rs.getString("maPhieuDichVu")
                );
                list.add(cthd);
            }
        }
        return list;
    }

    // Lấy chi tiết hóa đơn theo mã phiếu đặt phòng
    public List<ChitietHoaDon> getChitietHoaDonByMaDatPhong(String maDatPhong) throws SQLException {
        List<ChitietHoaDon> list = new ArrayList<>();
        String sql = "SELECT maHoaDon, maPhong, thueVat, soNgay, khuyenMai, tienPhong, tienDichVu, " +
                     "maChuongTrinhKhuyenMai, maDatPhong, maPhieuDichVu FROM ChitietHoaDon " +
                     "WHERE maDatPhong = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maDatPhong);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ChitietHoaDon cthd = new ChitietHoaDon(
                        rs.getString("maHoaDon"),
                        rs.getString("maPhong"),
                        rs.getDouble("thueVat"),
                        rs.getInt("soNgay"),
                        rs.getDouble("khuyenMai"),
                        rs.getDouble("tienPhong"),
                        rs.getDouble("tienDichVu"),
                        rs.getString("maChuongTrinhKhuyenMai"),
                        rs.getString("maDatPhong"),
                        rs.getString("maPhieuDichVu")
                    );
                    list.add(cthd);
                }
            }
        }
        return list;
    }

    // Tìm kiếm chi tiết hóa đơn theo từ khóa
    public List<ChitietHoaDon> timKiemChitietHoaDon(String tuKhoa) throws SQLException {
        List<ChitietHoaDon> list = new ArrayList<>();
        String sql = "SELECT maHoaDon, maPhong, thueVat, soNgay, khuyenMai, tienPhong, tienDichVu, " +
                     "maChuongTrinhKhuyenMai, maDatPhong, maPhieuDichVu FROM vw_ChiTietHoaDon " +
                     "WHERE maHoaDon LIKE ? OR maPhong LIKE ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + tuKhoa + "%");
            ps.setString(2, "%" + tuKhoa + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ChitietHoaDon cthd = new ChitietHoaDon(
                        rs.getString("maHoaDon"),
                        rs.getString("maPhong"),
                        rs.getDouble("thueVat"),
                        rs.getInt("soNgay"),
                        rs.getDouble("khuyenMai"),
                        rs.getDouble("tienPhong"),
                        rs.getDouble("tienDichVu"),
                        rs.getString("maChuongTrinhKhuyenMai"),
                        rs.getString("maDatPhong"),
                        rs.getString("maPhieuDichVu")
                    );
                    list.add(cthd);
                }
            }
        }
        return list;
    }

    // Gọi stored procedure sp_ChuyenPhong
    public boolean chuyenPhong(String maDatPhong, String maPhongCu, String maPhongMoi,
                              String liDo, String maNhanVien) throws SQLException {
        String sql = "{CALL sp_ChuyenPhong(?, ?, ?, ?, ?)}";
        try (Connection conn = connectDB.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, maDatPhong);
            cs.setString(2, maPhongCu);
            cs.setString(3, maPhongMoi);
            cs.setString(4, liDo);
            cs.setString(5, maNhanVien);
            return cs.executeUpdate() > 0;
        }
    }
}