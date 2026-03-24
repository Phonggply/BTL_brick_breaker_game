package BTL_brick_breaker_game.src.dao;

import java.sql.*;

public class DBConnection {
    private static final String SERVER = "localhost";
    private static final String PORT = "1433";
    private static final String DATABASE = "BrickBreakerGameDB";
    private static final String USER = "sa";
    private static final String PASSWORD = "123456";
    
    private static final String URL = "jdbc:sqlserver://" + SERVER + ":" + PORT + 
            ";databaseName=" + DATABASE + 
            ";encrypt=true;trustServerCertificate=true";
    
    private static Connection connection = null;
    
    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Kết nối SQL Server thành công!");
            } catch (ClassNotFoundException e) {
                System.err.println("Không tìm thấy driver SQL Server!");
                e.printStackTrace();
            } catch (SQLException e) {
                System.err.println("Lỗi kết nối database: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return connection;
    }
    
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Đã đóng kết nối database");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}