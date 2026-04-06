package dao;

import java.sql.*;
import java.util.LinkedList;

public class DBConnection {
    private static final String HOST = "bemnnvecbe8fulphqvil-mysql.services.clever-cloud.com";
    private static final String PORT = "3306";
    private static final String DB_NAME = "bemnnvecbe8fulphqvil";
    private static final String USER = "upoe83uqhhwx6fgv";
    private static final String PASSWORD = "jwkvXta5iRU0wVJ3E8FA";
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    // Tạo một danh sách các kết nối sẵn có (Simple Pool)
    private static final LinkedList<Connection> pool = new LinkedList<>();
    private static final int MAX_POOL_SIZE = 10; // Tăng lên 10 để phục vụ nhiều người hơn

    public static synchronized Connection getConnection() throws SQLException {
        while (!pool.isEmpty()) {
            Connection conn = pool.removeFirst();
            try {
                if (conn != null && !conn.isClosed() && conn.isValid(1)) {
                    return conn;
                }
            } catch (SQLException e) {
                // Nếu kết nối lỗi thì bỏ qua, lấy cái tiếp theo
            }
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static synchronized void close(Connection conn, Statement stmt, ResultSet rs) {
        try { if (rs != null) rs.close(); } catch (Exception e) {}
        try { if (stmt != null) stmt.close(); } catch (Exception e) {}
        
        // Thay vì đóng hoàn toàn, ta đưa kết nối trở lại pool để người sau dùng
        try {
            if (conn != null && !conn.isClosed()) {
                if (pool.size() < MAX_POOL_SIZE) {
                    pool.addLast(conn);
                } else {
                    conn.close();
                }
            }
        } catch (Exception e) {}
    }
}
