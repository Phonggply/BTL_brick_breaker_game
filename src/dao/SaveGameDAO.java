package dao;

import dao.entities.SaveGame;
import java.sql.*;

public class SaveGameDAO {
    
    public SaveGameDAO() {}
    
    private Connection getConnection() {
        return DBConnection.getConnection();
    }
    
    public boolean saveGame(int playerId, int level, int score, int lives, String gameState) {
        String sql = "INSERT INTO SaveGame (PlayerId, Level, CurrentScore, Lives, GameState, SaveDate) " +
                     "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        Connection conn = getConnection();
        if (conn == null) return false;
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            ps.setInt(2, level);
            ps.setInt(3, score);
            ps.setInt(4, lives);
            ps.setString(5, gameState);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public SaveGame getLatestSave(int playerId) {
        String sql = "SELECT * FROM SaveGame WHERE PlayerId = ? ORDER BY SaveDate DESC LIMIT 1";
        Connection conn = getConnection();
        if (conn == null) return null;
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    SaveGame save = new SaveGame();
                    save.setSaveId(rs.getInt("SaveId"));
                    save.setPlayerId(rs.getInt("PlayerId"));
                    save.setLevel(rs.getInt("Level"));
                    save.setCurrentScore(rs.getInt("CurrentScore"));
                    save.setLives(rs.getInt("Lives"));
                    save.setGameState(rs.getString("GameState"));
                    save.setSaveDate(rs.getTimestamp("SaveDate"));
                    return save;
                }
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
