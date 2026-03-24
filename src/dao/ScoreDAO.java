package BTL_brick_breaker_game.src.dao;

import BTL_brick_breaker_game.src.dao.entities.Score;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScoreDAO {
    
    private Connection connection;
    
    public ScoreDAO() {
        this.connection = DBConnection.getConnection();
    }
    
    /**
     * sp_GameOver - Kết thúc game
     */
    public int gameOver(int playerId, int score) {
        String sql = "{call sp_GameOver(?, ?)}";
        
        try (CallableStatement cstmt = connection.prepareCall(sql)) {
            cstmt.setInt(1, playerId);
            cstmt.setInt(2, score);
            
            ResultSet rs = cstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("CoinEarned");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * sp_GetHighScores - Lấy top điểm cao nhất
     */
    public List<Score> getTopScores(int topCount) {
        String sql = "{call sp_GetHighScores(?)}";
        List<Score> scores = new ArrayList<>();
        
        try (CallableStatement cstmt = connection.prepareCall(sql)) {
            cstmt.setInt(1, topCount);
            ResultSet rs = cstmt.executeQuery();
            
            while (rs.next()) {
                Score score = new Score();
                score.setUserName(rs.getString("UserName"));
                score.setScore(rs.getInt("Score"));
                score.setPlayedDate(rs.getTimestamp("PlayedDate"));
                scores.add(score);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scores;
    }
    
    /**
     * Lấy điểm của người chơi
     */
    public List<Score> getPlayerScores(int playerId) {
        String sql = "SELECT * FROM Scores WHERE PlayerId = ? ORDER BY Score DESC";
        List<Score> scores = new ArrayList<>();
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Score score = new Score();
                score.setScoreId(rs.getInt("ScoreId"));
                score.setPlayerId(rs.getInt("PlayerId"));
                score.setScore(rs.getInt("Score"));
                score.setPlayedDate(rs.getTimestamp("PlayedDate"));
                scores.add(score);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scores;
    }
    
    public void close() {
        DBConnection.closeConnection();
    }
}