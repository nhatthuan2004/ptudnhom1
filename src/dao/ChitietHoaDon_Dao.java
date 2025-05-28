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
        String sql = "SELECT COUNT(*) FROM ChitietHoaDon WHERE maHoaDon = ? AND (maPhong = ? OR maPhieuDichVu IS NOT NULL)";
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
                    "maChuongTrinhKhuyenMai, maDatPhong, maPhieuDichVu FROM ChitietHoaDon WHERE maHoaDon = ?";
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

    public boolean themChitietHoaDon(ChitietHoaDon cthd) throws SQLException {
        // Kiểm tra các trường bắt buộc
        if (cthd.getMaHoaDon() == null || cthd.getMaHoaDon().trim().isEmpty()) {
            throw new SQLException("Mã hóa đơn không được để trống!");
        }
        if (cthd.getKhuyenMai() > 0 && (cthd.getMaChuongTrinhKhuyenMai() == null || cthd.getMaChuongTrinhKhuyenMai().trim().isEmpty())) {
            throw new SQLException("Mã chương trình khuyến mãi không được để trống khi có khuyến mãi!");
        }

        // Kiểm tra xem bản ghi đã tồn tại chưa (chỉ dựa trên maHoaDon)
        String checkSql = "SELECT COUNT(*) FROM ChitietHoaDon WHERE maHoaDon = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, cthd.getMaHoaDon());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                // Bản ghi đã tồn tại, cập nhật thay vì thêm mới
                System.out.println("Bản ghi đã tồn tại, chuyển sang cập nhật: " + cthd.getMaHoaDon());
                return suaChitietHoaDon(cthd);
            }
        }

        // Thêm mới nếu chưa tồn tại
        String sql = "INSERT INTO ChitietHoaDon (maHoaDon, maPhong, thueVat, soNgay, khuyenMai, tienPhong, " +
                    "tienDichVu, maChuongTrinhKhuyenMai, maDatPhong, maPhieuDichVu) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cthd.getMaHoaDon());
            ps.setString(2, cthd.getMaPhong()); // Có thể là null
            ps.setDouble(3, cthd.getThueVat());
            ps.setInt(4, cthd.getSoNgay());
            ps.setDouble(5, cthd.getKhuyenMai());
            ps.setDouble(6, cthd.getTienPhong());
            ps.setDouble(7, cthd.getTienDichVu());
            ps.setString(8, cthd.getMaChuongTrinhKhuyenMai());
            ps.setString(9, cthd.getMaDatPhong());
            ps.setString(10, cthd.getMaPhieuDichVu()); // Đảm bảo gán giá trị, có thể là null
            boolean result = ps.executeUpdate() > 0;
            System.out.println("Đã thêm chi tiết hóa đơn: " + cthd.getMaHoaDon() + ", maPhieuDichVu: " + cthd.getMaPhieuDichVu());
            return result;
        }
    }

    public boolean suaChitietHoaDon(ChitietHoaDon cthd) throws SQLException {
        // Kiểm tra các trường bắt buộc
        if (cthd.getMaHoaDon() == null || cthd.getMaHoaDon().trim().isEmpty()) {
            throw new SQLException("Mã hóa đơn không được để trống!");
        }
        if (cthd.getKhuyenMai() > 0 && (cthd.getMaChuongTrinhKhuyenMai() == null || cthd.getMaChuongTrinhKhuyenMai().trim().isEmpty())) {
            throw new SQLException("Mã chương trình khuyến mãi không được để trống khi có khuyến mãi!");
        }

        String sql = "UPDATE ChitietHoaDon SET maPhong = ?, thueVat = ?, soNgay = ?, khuyenMai = ?, tienPhong = ?, tienDichVu = ?, " +
                    "maChuongTrinhKhuyenMai = ?, maDatPhong = ?, maPhieuDichVu = ? " +
                    "WHERE maHoaDon = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cthd.getMaPhong()); // Có thể là null
            ps.setDouble(2, cthd.getThueVat());
            ps.setInt(3, cthd.getSoNgay());
            ps.setDouble(4, cthd.getKhuyenMai());
            ps.setDouble(5, cthd.getTienPhong());
            ps.setDouble(6, cthd.getTienDichVu());
            ps.setString(7, cthd.getMaChuongTrinhKhuyenMai());
            ps.setString(8, cthd.getMaDatPhong());
            ps.setString(9, cthd.getMaPhieuDichVu()); // Đảm bảo cập nhật maPhieuDichVu
            ps.setString(10, cthd.getMaHoaDon());
            boolean result = ps.executeUpdate() > 0;
            System.out.println("Đã cập nhật chi tiết hóa đơn: " + cthd.getMaHoaDon() + ", maPhieuDichVu: " + cthd.getMaPhieuDichVu());
            return result;
        }
    }

    public boolean xoaChitietHoaDon(String maHoaDon, String maPhong) throws SQLException {
        String sql = "DELETE FROM ChitietHoaDon WHERE maHoaDon = ? AND maPhong = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHoaDon);
            ps.setString(2, maPhong);
            boolean result = ps.executeUpdate() > 0;
            System.out.println("Đã xóa chi tiết hóa đơn: " + maHoaDon + ", Mã phòng: " + maPhong);
            return result;
        }
    }

    public List<ChitietHoaDon> getAllChitietHoaDon() throws SQLException {
        List<ChitietHoaDon> list = new ArrayList<>();
        String sql = "SELECT maHoaDon, maPhong, thueVat, soNgay, khuyenMai, tienPhong, tienDichVu, " +
                    "maChuongTrinhKhuyenMai, maDatPhong, maPhieuDichVu " +
                    "FROM ChitietHoaDon";
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
        System.out.println("Đã tải toàn bộ chi tiết hóa đơn: " + list.size() + " bản ghi");
        return list;
    }

    public List<ChitietHoaDon> getChitietHoaDonByMaDatPhong(String maDatPhong) throws SQLException {
        List<ChitietHoaDon> list = new ArrayList<>();
        String sql = "SELECT maHoaDon, maPhong, thueVat, soNgay, khuyenMai, tienPhong, tienDichVu, " +
                    "maChuongTrinhKhuyenMai, maDatPhong, maPhieuDichVu " +
                    "FROM ChitietHoaDon WHERE maDatPhong = ?";
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
        System.out.println("Đã tải chi tiết hóa đơn theo mã đặt phòng: " + maDatPhong + ", Số bản ghi: " + list.size());
        return list;
    }

    public List<ChitietHoaDon> timKiemChitietHoaDon(String tuKhoa) throws SQLException {
        List<ChitietHoaDon> list = new ArrayList<>();
        String sql = "SELECT maHoaDon, maPhong, thueVat, soNgay, khuyenMai, tienPhong, tienDichVu, " +
                    "maChuongTrinhKhuyenMai, maDatPhong, maPhieuDichVu " +
                    "FROM ChitietHoaDon " +
                    "WHERE maHoaDon LIKE ? OR maPhong LIKE ? OR maDatPhong LIKE ? OR maPhieuDichVu LIKE ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + tuKhoa + "%");
            ps.setString(2, "%" + tuKhoa + "%");
            ps.setString(3, "%" + tuKhoa + "%");
            ps.setString(4, "%" + tuKhoa + "%");
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
        System.out.println("Đã tìm kiếm chi tiết hóa đơn với từ khóa: " + tuKhoa + ", Số bản ghi: " + list.size());
        return list;
    }

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
            boolean result = cs.executeUpdate() > 0;
            System.out.println("Đã chuyển phòng: Từ " + maPhongCu + " sang " + maPhongMoi + " cho mã đặt phòng: " + maDatPhong);
            return result;
        }
    }
}