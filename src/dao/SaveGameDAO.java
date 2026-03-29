package dao;

import dao.entities.SaveGame;
import java.sql.*;

public class SaveGameDAO {
    
    private Connection connection;
    
    public SaveGameDAO() {
        this.connection = DBConnection.getConnection();
    }
    
    /**
     * sp_SaveGame - Lưu game
     */
    public String saveGame(int playerId, String gameState) {
        String sql = "{call sp_SaveGame(?, ?)}";
        
        try (CallableStatement cstmt = connection.prepareCall(sql)) {
            cstmt.setInt(1, playerId);
            cstmt.setString(2, gameState);
            
            ResultSet rs = cstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("Message");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
        return "Lưu thất bại";
    }
    
    /**
     * sp_LoadLatestGame - Tải game mới nhất
     */
    public SaveGame loadLatestGame(int playerId) {
        String sql = "{call sp_LoadLatestGame(?)}";
        
        try (CallableStatement cstmt = connection.prepareCall(sql)) {
            cstmt.setInt(1, playerId);
            ResultSet rs = cstmt.executeQuery();
            
            if (rs.next()) {
                SaveGame save = new SaveGame();
                save.setSaveId(rs.getInt("SaveId"));
                save.setPlayerId(rs.getInt("PlayerId"));
                save.setSaveName(rs.getString("SaveName"));
                save.setLevel(rs.getInt("Level"));
                save.setCurrentScore(rs.getInt("CurrentScore"));
                save.setLives(rs.getInt("Lives"));
                save.setBricksRemaining(rs.getInt("BricksRemaining"));
                save.setBallPositionX(rs.getDouble("BallPositionX"));
                save.setBallPositionY(rs.getDouble("BallPositionY"));
                save.setPaddlePositionX(rs.getDouble("PaddlePositionX"));
                save.setGameState(rs.getString("GameState"));
                save.setSaveDate(rs.getTimestamp("SaveDate"));
                return save;
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
