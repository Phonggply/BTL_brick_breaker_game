package dao;

import java.sql.*;

public class DBConnection {
    // Thông tin cấu hình MySQL
    private static final String HOST = "localhost"; // Đổi thành IP máy chủ nếu kết nối từ xa
    private static final String PORT = "3306";
    private static final String DB_NAME = "brick_breaker";
    private static final String USER = "root";
    private static final String PASSWORD = "123456789"; // Nhập mật khẩu MySQL của bạn ở đây
    
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    
    private static Connection connection = null;
    
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Tải driver MySQL
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Kết nối MySQL thành công!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Không tìm thấy driver MySQL! Hãy thêm mysql-connector-java.jar vào thư viện.");
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối MySQL: " + e.getMessage());
            // Nếu chưa có database, có thể bạn cần tạo database 'brick_breaker' trước trong MySQL
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
