package BTL_brick_breaker_game.src.dao;

import BTL_brick_breaker_game.src.dao.entities.Player;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerDAO {
    
    private Connection connection;
    
    public PlayerDAO() {
        this.connection = DBConnection.getConnection();
    }
    
    /**
     * sp_CreatePlayer - Tạo người chơi mới
     */
    public Player createPlayer(String userName, String email) {
        String sql = "{call sp_CreatePlayer(?, ?)}";
        
        try (CallableStatement cstmt = connection.prepareCall(sql)) {
            cstmt.setString(1, userName);
            cstmt.setString(2, email);
            
            ResultSet rs = cstmt.executeQuery();
            if (rs.next()) {
                int playerId = rs.getInt("PlayerId");
                String message = rs.getString("Message");
                if (message.contains("thành công")) {
                    Player player = new Player(userName, email);
                    player.setPlayerId(playerId);
                    return player;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * sp_CheckBalance - Kiểm tra số dư
     */
    public int[] checkBalance(int playerId) {
        String sql = "{call sp_CheckBalance(?)}";
        int[] balance = new int[2]; // [coins, gems]
        
        try (CallableStatement cstmt = connection.prepareCall(sql)) {
            cstmt.setInt(1, playerId);
            ResultSet rs = cstmt.executeQuery();
            
            if (rs.next()) {
                balance[0] = rs.getInt("Coins");
                balance[1] = rs.getInt("Gems");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return balance;
    }
    
    /**
     * sp_GetPlayerStats - Lấy thống kê người chơi
     */
    public ResultSet getPlayerStats(int playerId) {
        String sql = "{call sp_GetPlayerStats(?)}";
        
        try {
            CallableStatement cstmt = connection.prepareCall(sql);
            cstmt.setInt(1, playerId);
            return cstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * sp_GetLeaderboard - Bảng xếp hạng
     */
    public ResultSet getLeaderboard(int topCount, String sortBy) {
        String sql = "{call sp_GetLeaderboard(?, ?)}";
        
        try {
            CallableStatement cstmt = connection.prepareCall(sql);
            cstmt.setInt(1, topCount);
            cstmt.setString(2, sortBy);
            return cstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * sp_DeletePlayer - Xóa người chơi
     */
    public boolean deletePlayer(int playerId) {
        String sql = "{call sp_DeletePlayer(?)}";
        
        try (CallableStatement cstmt = connection.prepareCall(sql)) {
            cstmt.setInt(1, playerId);
            ResultSet rs = cstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("Message").contains("thành công");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * fn_CanAfford - Kiểm tra đủ tiền mua không
     */
    public boolean canAfford(int playerId, int itemId, int quantity) {
        String sql = "SELECT dbo.fn_CanAfford(?, ?, ?) as CanAfford";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            ps.setInt(2, itemId);
            ps.setInt(3, quantity);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getBoolean("CanAfford");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * fn_GetPlayerRank - Lấy rank của người chơi
     */
    public int getPlayerRank(int playerId) {
        String sql = "SELECT dbo.fn_GetPlayerRank(?) as PlayerRank";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("PlayerRank");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * fn_GetCompletionRate - Tỷ lệ hoàn thành game
     */
    public double getCompletionRate(int playerId) {
        String sql = "SELECT dbo.fn_GetCompletionRate(?) as CompletionRate";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("CompletionRate");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * vw_PlayerDetails - Xem thông tin chi tiết
     */
    public ResultSet getPlayerDetails(int playerId) {
        String sql = "SELECT * FROM vw_PlayerDetails WHERE PlayerId = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * vw_TopPlayers - Top người chơi
     */
    public ResultSet getTopPlayers() {
        String sql = "SELECT * FROM vw_TopPlayers";
        
        try (Statement stmt = connection.createStatement()) {
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Lấy player theo ID
     */
    public Player getPlayerById(int playerId) {
        String sql = "SELECT * FROM Players WHERE PlayerId = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Player player = new Player();
                player.setPlayerId(rs.getInt("PlayerId"));
                player.setUserName(rs.getString("UserName"));
                player.setEmail(rs.getString("Email"));
                player.setCoins(rs.getInt("Coins"));
                player.setGems(rs.getInt("Gems"));
                player.setCreatedAt(rs.getTimestamp("CreatedAt"));
                return player;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void close() {
        DBConnection.closeConnection();
    }
}