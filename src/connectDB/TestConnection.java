package connectDB;

import java.sql.Connection;

public class TestConnection {
    public static void main(String[] args) {
        Connection conn = ConnectDB.getConnection();
        if (conn != null) {
            System.out.println("🎉 Kết nối CSDL thành công!");
        } else {
            System.out.println("⚠️ Kết nối CSDL thất bại.");
        }

        // Ngắt kết nối (không bắt buộc)
        ConnectDB.getInstance().disconnect();
    }
}
