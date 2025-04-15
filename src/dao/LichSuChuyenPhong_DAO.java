package dao;

import model.LichSuChuyenPhong;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import ConnectDB.ConnectDB;

public class LichSuChuyenPhong_DAO {
    private final ConnectDB connectDB;

    public LichSuChuyenPhong_DAO() {
        this.connectDB = ConnectDB.getInstance();
    }

    // Thêm lịch sử chuyển phòng
    public boolean themLichSuChuyenPhong(LichSuChuyenPhong lscp) throws SQLException {
        String sql = "INSERT INTO LichSuChuyenPhong (maLichSu, maPhieuDat, maPhongCu, maPhongMoi, thoiGianChuyen, lyDo, maNhanVien) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lscp.getMaLichSu());
            ps.setString(2, lscp.getMaPhieuDat());
            ps.setString(3, lscp.getMaPhongCu());
            ps.setString(4, lscp.getMaPhongMoi());
            ps.setTimestamp(5, lscp.getThoiGianChuyen() != null ? Timestamp.valueOf(lscp.getThoiGianChuyen()) : null);
            ps.setString(6, lscp.getLyDo());
            ps.setString(7, lscp.getMaNhanVien());
            return ps.executeUpdate() > 0;
        }
    }

    // Xóa lịch sử chuyển phòng
    public boolean xoaLichSuChuyenPhong(String maLichSu) throws SQLException {
        String sql = "DELETE FROM LichSuChuyenPhong WHERE maLichSu = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maLichSu);
            return ps.executeUpdate() > 0;
        }
    }

    // Sửa lịch sử chuyển phòng
    public boolean suaLichSuChuyenPhong(LichSuChuyenPhong lscp) throws SQLException {
        String sql = "UPDATE LichSuChuyenPhong SET maPhieuDat = ?, maPhongCu = ?, maPhongMoi = ?, thoiGianChuyen = ?, lyDo = ?, maNhanVien = ? " +
                     "WHERE maLichSu = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lscp.getMaPhieuDat());
            ps.setString(2, lscp.getMaPhongCu());
            ps.setString(3, lscp.getMaPhongMoi());
            ps.setTimestamp(4, lscp.getThoiGianChuyen() != null ? Timestamp.valueOf(lscp.getThoiGianChuyen()) : null);
            ps.setString(5, lscp.getLyDo());
            ps.setString(6, lscp.getMaNhanVien());
            ps.setString(7, lscp.getMaLichSu());
            return ps.executeUpdate() > 0;
        }
    }

    // Tìm kiếm lịch sử chuyển phòng
    public List<LichSuChuyenPhong> timKiemLichSuChuyenPhong(String tuKhoa) throws SQLException {
        List<LichSuChuyenPhong> list = new ArrayList<>();
        String sql = "SELECT maLichSu, maPhieuDat, maPhongCu, maPhongMoi, thoiGianChuyen, lyDo, maNhanVien " +
                     "FROM LichSuChuyenPhong WHERE maLichSu LIKE ? OR maPhieuDat LIKE ? OR maNhanVien LIKE ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + tuKhoa + "%");
            ps.setString(2, "%" + tuKhoa + "%");
            ps.setString(3, "%" + tuKhoa + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LichSuChuyenPhong lscp = new LichSuChuyenPhong(
                        rs.getString("maLichSu"),
                        rs.getString("maPhieuDat"),
                        rs.getString("maPhongCu"),
                        rs.getString("maPhongMoi"),
                        rs.getTimestamp("thoiGianChuyen") != null ? rs.getTimestamp("thoiGianChuyen").toLocalDateTime() : null,
                        rs.getString("lyDo"),
                        rs.getString("maNhanVien")
                    );
                    list.add(lscp);
                }
            }
        }
        return list;
    }

    // Lấy toàn bộ danh sách lịch sử chuyển phòng
    public List<LichSuChuyenPhong> getAllLichSuChuyenPhong() throws SQLException {
        List<LichSuChuyenPhong> list = new ArrayList<>();
        String sql = "SELECT maLichSu, maPhieuDat, maPhongCu, maPhongMoi, thoiGianChuyen, lyDo, maNhanVien FROM LichSuChuyenPhong";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LichSuChuyenPhong lscp = new LichSuChuyenPhong(
                    rs.getString("maLichSu"),
                    rs.getString("maPhieuDat"),
                    rs.getString("maPhongCu"),
                    rs.getString("maPhongMoi"),
                    rs.getTimestamp("thoiGianChuyen") != null ? rs.getTimestamp("thoiGianChuyen").toLocalDateTime() : null,
                    rs.getString("lyDo"),
                    rs.getString("maNhanVien")
                );
                list.add(lscp);
            }
        }
        return list;
    }

    // Lấy lịch sử chuyển phòng theo mã
    public LichSuChuyenPhong getLichSuChuyenPhongByMa(String maLichSu) throws SQLException {
        String sql = "SELECT maLichSu, maPhieuDat, maPhongCu, maPhongMoi, thoiGianChuyen, lyDo, maNhanVien " +
                     "FROM LichSuChuyenPhong WHERE maLichSu = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maLichSu);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new LichSuChuyenPhong(
                        rs.getString("maLichSu"),
                        rs.getString("maPhieuDat"),
                        rs.getString("maPhongCu"),
                        rs.getString("maPhongMoi"),
                        rs.getTimestamp("thoiGianChuyen") != null ? rs.getTimestamp("thoiGianChuyen").toLocalDateTime() : null,
                        rs.getString("lyDo"),
                        rs.getString("maNhanVien")
                    );
                }
            }
        }
        return null;
    }

    // Sinh mã lịch sử tự động
    public String getNextMaLichSu() throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(maLichSu, 3, LEN(maLichSu) - 2) AS INT)) AS maxMa " +
                     "FROM LichSuChuyenPhong WHERE maLichSu LIKE 'LS[0-9]%'";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next() && rs.getInt("maxMa") > 0) {
                int nextMa = rs.getInt("maxMa") + 1;
                return "LS" + String.format("%03d", nextMa);
            }
        }
        return "LS001";
    }
}