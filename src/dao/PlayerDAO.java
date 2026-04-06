package dao;

import dao.entities.Player;
import java.sql.*;

public class PlayerDAO {
    
    public PlayerDAO() {}
    
    public Player register(String userName, String password, String email) {
        // Khớp với 1_script.sql: Bảng Players chỉ có UserName, Password, Coins
        String sql = "INSERT INTO Players (UserName, Password, Coins) VALUES (?, ?, 1000)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, userName);
            ps.setString(2, password);
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int playerId = rs.getInt(1);
                        Player player = new Player(userName, ""); // Bỏ email nếu DB không có
                        player.setPlayerId(playerId);
                        initializeStats(playerId);
                        return player;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi đăng ký: " + e.getMessage());
        } finally {
            // QUAN TRỌNG: Phải truyền 'conn' vào để đóng kết nối
            DBConnection.close(conn, ps, null);
        }
        return null;
    }

    public Player login(String userName, String password) {
        String sql = "SELECT * FROM Players WHERE UserName = ? AND Password = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, userName);
            ps.setString(2, password);
            rs = ps.executeQuery();
            if (rs.next()) {
                Player player = new Player();
                player.setPlayerId(rs.getInt("PlayerId"));
                player.setUserName(rs.getString("UserName"));
                player.setCoins(rs.getInt("Coins"));
                // player.setGems(rs.getInt("Gems")); // Bỏ nếu DB không có cột Gems
                return player;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi đăng nhập: " + e.getMessage());
        } finally {
            DBConnection.close(conn, ps, rs);
        }
        return null;
    }

    private void initializeStats(int playerId) {
        String sql = "INSERT IGNORE INTO GameStats (PlayerId, HighestLevel) VALUES (?, 1)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, playerId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        finally { DBConnection.close(conn, ps, null); }
    }
    
    public int getHighestLevel(int playerId) {
        String sql = "SELECT HighestLevel FROM GameStats WHERE PlayerId = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, playerId);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("HighestLevel");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps, rs);
        }
        return 1;
    }

    public int[] checkBalance(int playerId) {
        String sql = "SELECT Coins FROM Players WHERE PlayerId = ?";
        int[] balance = new int[2]; // Trả về mảng 2 phần tử để giữ tương thích với code cũ
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, playerId);
            rs = ps.executeQuery();
            if (rs.next()) {
                balance[0] = rs.getInt("Coins");
                balance[1] = 0; // DB không có Gems thì để 0
            }
        } catch (SQLException e) { e.printStackTrace(); }
        finally { DBConnection.close(conn, ps, rs); }
        return balance;
    }

    public void updateHighestLevel(int playerId, int level) {
        String updateSql = "UPDATE GameStats SET HighestLevel = GREATEST(HighestLevel, ?) WHERE PlayerId = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(updateSql);
            ps.setInt(1, level);
            ps.setInt(2, playerId);
            if (ps.executeUpdate() == 0) {
                initializeStats(playerId); // Nếu chưa có thì tạo mới
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps, null);
        }
    }

    public boolean addCoins(int playerId, int amount) {
        String sql = "UPDATE Players SET Coins = Coins + ? WHERE PlayerId = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, amount);
            ps.setInt(2, playerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps, null);
        }
        return false;
    }
}
