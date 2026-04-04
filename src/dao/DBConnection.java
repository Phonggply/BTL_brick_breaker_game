package dao;

import java.sql.*;

public class DBConnection {
    // THÔNG TIN KẾT NỐI SERVER ONLINE (Thay bằng thông tin từ Clever Cloud của bạn)
    private static final String HOST = "bemnnvecbe8fulphqvil-mysql.services.clever-cloud.com"; // Copy dòng Host
    private static final String PORT = "3306";
    private static final String DB_NAME = "bemnnvecbe8fulphqvil"; // Copy dòng Database name
    private static final String USER = "upoe83uqhhwx6fgv"; // Copy dòng User
    private static final String PASSWORD = "jwkvXta5iRU0wVJ3E8FA"; // Copy dòng Password
    
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    
    private static Connection connection = null;

    public static synchronized Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Đã thiết lập kết nối mới tới MySQL Server!");
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found", e);
        }
        return connection;
    }

    // Không đóng connection ngay lập tức nữa, để dùng lại cho các lần sau
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (stmt != null) stmt.close(); } catch (SQLException e) {}
        // Không đóng conn ở đây để reuse
    }
    
    public static void forceClose() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Đã đóng kết nối database vĩnh viễn.");
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
