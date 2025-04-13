package connectDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
    private static Connection con = null;
    private static final ConnectDB instance = new ConnectDB();

    // Kết nối SQL Server - chú ý đổi tên server nếu cần
    private static final String URL = "jdbc:sqlserver://NhatThuan:1433;databaseName=Khachsan;encrypt=true;trustServerCertificate=true;";
    private static final String USER = "sa";
    private static final String PASSWORD = "123456";

    private ConnectDB() {
        // Constructor private để đảm bảo Singleton
    }

    public static ConnectDB getInstance() {
        return instance;
    }

    /**
     * Kết nối đến CSDL nếu chưa kết nối hoặc kết nối đã đóng
     */
    public Connection connect() {
        try {
            if (con == null || con.isClosed()) {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                con = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Kết nối thành công đến CSDL.");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Không tìm thấy driver SQL Server.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi kết nối đến cơ sở dữ liệu.");
            e.printStackTrace();
        }
        return con;
    }

    /**
     * Ngắt kết nối CSDL
     */
    public void disconnect() {
        if (con != null) {
            try {
                con.close();
                con = null;
                System.out.println("🔌 Kết nối đã đóng.");
            } catch (SQLException e) {
                System.err.println("❌ Lỗi khi đóng kết nối.");
                e.printStackTrace();
            }
        }
    }

    /**
     * Lấy connection hiện tại
     */
    public static Connection getConnection() {
        try {
            if (con == null || con.isClosed()) {
                return instance.connect();
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi kiểm tra trạng thái connection.");
            e.printStackTrace();
        }
        return con;
    }
}
