package ConnectDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
	private static final String URL = "jdbc:sqlserver://NhatThuan:1433;databaseName=Khachsan;encrypt=true;trustServerCertificate=true;";
    private static final String USER = "sa";
    private static final String PASSWORD = "123456";

    // Singleton instance
    private static final ConnectDB instance = new ConnectDB();

    private ConnectDB() {
        // Constructor private để đảm bảo Singleton
    }

    public static ConnectDB getInstance() {
        return instance;
    }

    /**
     * Tạo kết nối mới đến CSDL
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Kết nối thành công đến CSDL.");
            return connection;
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Không tìm thấy driver SQL Server.");
            throw new SQLException("Driver not found", e);
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi kết nối đến cơ sở dữ liệu: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Đóng kết nối (dùng trong try-with-resources nên không cần gọi thủ công)
     */
    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("🔌 Kết nối đã đóng.");
            } catch (SQLException e) {
                System.err.println("❌ Lỗi khi đóng kết nối: " + e.getMessage());
            }
        }
    }
}