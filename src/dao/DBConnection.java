package dao;

import java.sql.*;

public class DBConnection {
    // Database connection info
    private static final String HOST = "bemnnvecbe8fulphqvil-mysql.services.clever-cloud.com";
    private static final String PORT = "3306";
    private static final String DB_NAME = "bemnnvecbe8fulphqvil";
    private static final String USER = "upoe83uqhhwx6fgv";
    private static final String PASSWORD = "jwkvXta5iRU0wVJ3E8FA";
    
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    
    private static Connection connection = null;

    public static synchronized Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("New database connection established.");
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found", e);
        }
        return connection;
    }

    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (stmt != null) stmt.close(); } catch (SQLException e) {}
    }
    
    public static void forceClose() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
