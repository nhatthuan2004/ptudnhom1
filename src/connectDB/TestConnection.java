package ConnectDB;

import java.sql.Connection;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        try (Connection conn = ConnectDB.getInstance().getConnection()) {
            if (conn != null && !conn.isClosed()) {
                // System.out.println("🎉 Kết nối CSDL thành công!"); // Đã xóa
            } else {
                System.out.println("⚠️ Kết nối CSDL thất bại.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi kết nối CSDL: " + e.getMessage());
            e.printStackTrace();
        }
    }
}