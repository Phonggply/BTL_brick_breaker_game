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
    
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Kết nối MySQL thành công!");
            }
        } catch (ClassNotFoundException e) {
            showError("Không tìm thấy Driver MySQL!\nHãy đảm bảo file .jar đã nằm trong thư mục lib.");
        } catch (SQLException e) {
            String msg = "Lỗi kết nối MySQL!\n";
            if (e.getMessage().contains("Communications link failure")) {
                msg += "- Bạn chưa bật MySQL Server (XAMPP/WampServer).\n- Hoặc địa chỉ IP máy chủ không đúng.";
            } else if (e.getMessage().contains("Access denied")) {
                msg += "- Sai Username hoặc Password MySQL (kiểm tra lại DBConnection.java).";
            } else if (e.getMessage().contains("Unknown database")) {
                msg += "- Chưa tạo Database 'brick_breaker'.\nHãy chạy lệnh: CREATE DATABASE brick_breaker;";
            } else {
                msg += "- Lỗi: " + e.getMessage();
            }
            showError(msg);
        }
        return connection;
    }

    private static void showError(String message) {
        javax.swing.JOptionPane.showMessageDialog(null, message, "Lỗi Cơ Sở Dữ Liệu", javax.swing.JOptionPane.ERROR_MESSAGE);
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
