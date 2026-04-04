package dao;

import java.sql.*;

public class DBConnection {
    private static final String DATABASE_FILE = "brick_breaker.db";
    private static final String URL = "jdbc:sqlite:" + DATABASE_FILE;
    
    private static Connection connection = null;
    
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Tải driver SQLite
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(URL);
                java.io.File dbFile = new java.io.File(DATABASE_FILE);
                System.out.println("Kết nối SQLite thành công!");
                System.out.println("Đường dẫn database: " + dbFile.getAbsolutePath());
                
                // Bật hỗ trợ Foreign Keys (SQLite mặc định tắt)
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("PRAGMA foreign_keys = ON;");
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Không tìm thấy driver SQLite! Hãy thêm sqlite-jdbc.jar vào thư viện.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối SQLite: " + e.getMessage());
            e.printStackTrace();
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
