package dao;

import dao.entities.Player;
import java.sql.*;

public class PlayerDAO {
    
    public PlayerDAO() {
    }
    
    private Connection getConnection() {
        return DBConnection.getConnection();
    }
    
    public Player createPlayer(String userName, String email) {
        String sql = "INSERT INTO Players (UserName, Email, Coins, Gems, CreatedDate, CreatedAt) VALUES (?, ?, 1000, 50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
        Connection conn = getConnection();
        if (conn == null) return null;
        
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, userName);
            ps.setString(2, email);
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int playerId = rs.getInt(1);
                        Player player = new Player(userName, email);
                        player.setPlayerId(playerId);
                        return player;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public int[] checkBalance(int playerId) {
        String sql = "SELECT Coins, Gems FROM Players WHERE PlayerId = ?";
        int[] balance = new int[2];
        Connection conn = getConnection();
        if (conn == null) return balance;
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    balance[0] = rs.getInt("Coins");
                    balance[1] = rs.getInt("Gems");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return balance;
    }
    
    public Player getPlayerById(int playerId) {
        String sql = "SELECT * FROM Players WHERE PlayerId = ?";
        Connection conn = getConnection();
        if (conn == null) return null;
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Player player = new Player();
                    player.setPlayerId(rs.getInt("PlayerId"));
                    player.setUserName(rs.getString("UserName"));
                    player.setEmail(rs.getString("Email"));
                    player.setCoins(rs.getInt("Coins"));
                    player.setGems(rs.getInt("Gems"));
                    // SQLite lưu date kiểu string hoặc long, ta lấy tạm làm Date
                    player.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    return player;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addCoins(int playerId, int amount) {
        String sql = "UPDATE Players SET Coins = Coins + ? WHERE PlayerId = ?";
        Connection conn = getConnection();
        if (conn == null) return false;
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, amount);
            ps.setInt(2, playerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deletePlayer(int playerId) {
        String sql = "DELETE FROM Players WHERE PlayerId = ?";
        Connection conn = getConnection();
        if (conn == null) return false;
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getHighestLevel(int playerId) {
        String sql = "SELECT HighestLevel FROM GameStats WHERE PlayerId = ?";
        Connection conn = getConnection();
        if (conn == null) return 1;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("HighestLevel");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public void updateHighestLevel(int playerId, int level) {
        String sql = "INSERT INTO GameStats (PlayerId, HighestLevel) VALUES (?, ?) " +
                     "ON CONFLICT(PlayerId) DO UPDATE SET HighestLevel = MAX(HighestLevel, EXCLUDED.HighestLevel)";
        Connection conn = getConnection();
        if (conn == null) return;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            ps.setInt(2, level);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        DBConnection.closeConnection();
    }
}
