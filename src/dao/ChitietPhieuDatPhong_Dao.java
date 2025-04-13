package dao;

import model.ChitietPhieuDatPhong;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;

public class ChitietPhieuDatPhong_Dao {
    private Connection connection;

    public ChitietPhieuDatPhong_Dao() throws SQLException {
        ConnectDB.getInstance();
		this.connection = ConnectDB.getConnection();
    }

    // Thêm chi tiết phiếu đặt phòng
    public boolean addChitietPhieuDatPhong(ChitietPhieuDatPhong chiTiet) throws SQLException {
        String sql = "INSERT INTO ChitietPhieuDatPhong (maPhong, maDatPhong, TienPhong, ThanhTien, soLuong) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, chiTiet.getMaPhong());
            stmt.setString(2, chiTiet.getMaDatPhong());
            stmt.setDouble(3, chiTiet.getGiaPhong());
            stmt.setDouble(4, chiTiet.getThanhTien());
            stmt.setInt(5, chiTiet.getSoLuong());
            return stmt.executeUpdate() > 0;
        }
    }

    // Lấy danh sách chi tiết theo mã đặt phòng
    public List<ChitietPhieuDatPhong> getChitietByMaDatPhong(String maDatPhong) throws SQLException {
        List<ChitietPhieuDatPhong> list = new ArrayList<>();
        String sql = "SELECT * FROM ChitietPhieuDatPhong WHERE maDatPhong = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, maDatPhong);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ChitietPhieuDatPhong chiTiet = new ChitietPhieuDatPhong(
                            rs.getString("maPhong"),
                            rs.getString("maDatPhong"),
                            rs.getDouble("TienPhong"),
                            rs.getDouble("ThanhTien"),
                            rs.getInt("soLuong")
                    );
                    list.add(chiTiet);
                }
            }
        }
        return list;
    }
}