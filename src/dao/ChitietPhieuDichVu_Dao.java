package dao;

import model.ChitietPhieuDichVu;
import ConnectDB.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChitietPhieuDichVu_Dao {
    private final ConnectDB connectDB;

    public ChitietPhieuDichVu_Dao() {
        this.connectDB = ConnectDB.getInstance();
    }

    // Add a service ticket detail
    public boolean themChitietPhieuDichVu(ChitietPhieuDichVu ctpdv) throws SQLException {
    String sql = "INSERT INTO ChitietPhieuDichVu (maPhieuDichVu, maDichVu, soLuong, donGia, maPhong) VALUES (?, ?, ?, ?, ?)";
    try (Connection conn = connectDB.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        // Kiểm tra giá trị đầu vào
        if (ctpdv.getMaPhieuDichVu().isEmpty() || ctpdv.getMaDichVu().isEmpty()) {
            throw new SQLException("Service ticket ID or service ID cannot be empty!");
        }

        // Kiểm tra xem maPhong có tồn tại trong bảng Phong không (nếu không rỗng)
        String maPhong = ctpdv.getMaPhong();
        if (maPhong != null && !maPhong.isEmpty()) {
            String checkPhongSql = "SELECT COUNT(*) FROM Phong WHERE maPhong = ?";
            try (PreparedStatement checkPs = conn.prepareStatement(checkPhongSql)) {
                checkPs.setString(1, maPhong);
                ResultSet rs = checkPs.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    throw new SQLException("Room ID '" + maPhong + "' does not exist in the Phong table!");
                }
            }
        }

        ps.setString(1, ctpdv.getMaPhieuDichVu());
        ps.setString(2, ctpdv.getMaDichVu());
        ps.setInt(3, ctpdv.getSoLuong());
        ps.setDouble(4, ctpdv.getDonGia());
        ps.setString(5, maPhong != null && !maPhong.isEmpty() ? maPhong : null);

        int rowsAffected = ps.executeUpdate();
        return rowsAffected > 0;
    } catch (SQLException e) {
        if (e.getSQLState().equals("23000")) { // Lỗi vi phạm ràng buộc khóa ngoại
            throw new SQLException("Error: Invalid service ticket ID, service ID, or room ID!", e);
        }
        throw e;
    }
}

    // Delete a service ticket detail
    public boolean xoaChitietPhieuDichVu(String maPhieuDichVu, String maDichVu) throws SQLException {
        String sql = "DELETE FROM ChitietPhieuDichVu WHERE maPhieuDichVu = ? AND maDichVu = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (maPhieuDichVu == null || maPhieuDichVu.isEmpty() || maDichVu == null || maDichVu.isEmpty()) {
                throw new SQLException("Service ticket ID or service ID cannot be empty!");
            }

            ps.setString(1, maPhieuDichVu);
            ps.setString(2, maDichVu);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Update a service ticket detail
    public boolean suaChitietPhieuDichVu(ChitietPhieuDichVu ctpdv) throws SQLException {
        String sql = "UPDATE ChitietPhieuDichVu SET soLuong = ?, donGia = ?, maPhong = ? WHERE maPhieuDichVu = ? AND maDichVu = ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (ctpdv.getMaPhieuDichVu().isEmpty() || ctpdv.getMaDichVu().isEmpty()) {
                throw new SQLException("Service ticket ID or service ID cannot be empty!");
            }

            ps.setInt(1, ctpdv.getSoLuong());
            ps.setDouble(2, ctpdv.getDonGia());
            ps.setString(3, ctpdv.getMaPhong().isEmpty() ? null : ctpdv.getMaPhong());
            ps.setString(4, ctpdv.getMaPhieuDichVu());
            ps.setString(5, ctpdv.getMaDichVu());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Search for service ticket details
    public List<ChitietPhieuDichVu> timKiemChitietPhieuDichVu(String tuKhoa) throws SQLException {
        List<ChitietPhieuDichVu> list = new ArrayList<>();
        String sql = "SELECT * FROM ChitietPhieuDichVu WHERE maPhieuDichVu LIKE ? OR maDichVu LIKE ?";
        try (Connection conn = connectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String searchKeyword = tuKhoa != null ? tuKhoa.trim() : "";
            ps.setString(1, "%" + searchKeyword + "%");
            ps.setString(2, "%" + searchKeyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ChitietPhieuDichVu ctpdv = new ChitietPhieuDichVu(
                            rs.getString("maPhieuDichVu"),
                            rs.getString("maDichVu"),
                            rs.getInt("soLuong"),
                            rs.getDouble("donGia"),
                            rs.getString("maPhong")
                    );
                    list.add(ctpdv);
                }
            }
        }
        return list;
    }

    // Retrieve all service ticket details
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
                        rs.getDouble("donGia"),
                        rs.getString("maPhong")
                );
                list.add(ctpdv);
            }
        }
        return list;
    }

    // Retrieve service ticket details by service ticket ID
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
                            rs.getDouble("donGia"),
                            rs.getString("maPhong")
                    );
                    list.add(ctpdv);
                }
            }
        }
        return list;
    }
}