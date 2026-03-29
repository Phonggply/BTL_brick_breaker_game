package dao;

import dao.entities.Score;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScoreDAO {
    
    public ScoreDAO() {
    }
    
    private Connection getConnection() {
        return DBConnection.getConnection();
    }
    
    public int gameOver(int playerId, int score) {
        Connection conn = getConnection();
        if (conn == null) return 0;
        
        try {
            conn.setAutoCommit(false); // Bắt đầu giao dịch (Transaction)
            
            // 1. Chèn điểm mới
            String sqlInsertScore = "INSERT INTO Scores (PlayerId, Score, PlayedDate) VALUES (?, ?, CURRENT_TIMESTAMP)";
            try (PreparedStatement ps = conn.prepareStatement(sqlInsertScore)) {
                ps.setInt(1, playerId);
                ps.setInt(2, score);
                ps.executeUpdate();
            }
            
            // 2. Cập nhật thống kê
            String sqlCheckStats = "SELECT 1 FROM GameStats WHERE PlayerId = ?";
            boolean hasStats = false;
            try (PreparedStatement ps = conn.prepareStatement(sqlCheckStats)) {
                ps.setInt(1, playerId);
                try (ResultSet rs = ps.executeQuery()) {
                    hasStats = rs.next();
                }
            }
            
            if (hasStats) {
                String sqlUpdateStats = "UPDATE GameStats SET GamesPlayed = GamesPlayed + 1, TotalScore = TotalScore + ? WHERE PlayerId = ?";
                try (PreparedStatement ps = conn.prepareStatement(sqlUpdateStats)) {
                    ps.setInt(1, score);
                    ps.setInt(2, playerId);
                    ps.executeUpdate();
                }
            } else {
                String sqlInsertStats = "INSERT INTO GameStats (PlayerId, GamesPlayed, TotalScore, BricksBroken, HighestLevel) VALUES (?, 1, ?, 0, 1)";
                try (PreparedStatement ps = conn.prepareStatement(sqlInsertStats)) {
                    ps.setInt(1, playerId);
                    ps.setInt(2, score);
                    ps.executeUpdate();
                }
            }
            
            // 3. Cộng xu thưởng
            int coinReward = score / 10;
            String sqlAddCoins = "UPDATE Players SET Coins = Coins + ? WHERE PlayerId = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlAddCoins)) {
                ps.setInt(1, coinReward);
                ps.setInt(2, playerId);
                ps.executeUpdate();
            }
            
            conn.commit();
            return coinReward;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
    
    public List<Score> getTopScores(int topCount) {
        String sql = "SELECT p.UserName, s.Score, s.PlayedDate " +
                     "FROM Scores s " +
                     "JOIN Players p ON s.PlayerId = p.PlayerId " +
                     "ORDER BY s.Score DESC LIMIT ?";
        List<Score> scores = new ArrayList<>();
        Connection conn = getConnection();
        if (conn == null) return scores;
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, topCount);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Score score = new Score();
                    score.setUserName(rs.getString("UserName"));
                    score.setScore(rs.getInt("Score"));
                    score.setPlayedDate(rs.getTimestamp("PlayedDate"));
                    scores.add(score);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scores;
    }
    
    public List<Score> getPlayerScores(int playerId) {
        String sql = "SELECT * FROM Scores WHERE PlayerId = ? ORDER BY Score DESC";
        List<Score> scores = new ArrayList<>();
        Connection conn = getConnection();
        if (conn == null) return scores;
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Score score = new Score();
                    score.setScoreId(rs.getInt("ScoreId"));
                    score.setPlayerId(rs.getInt("PlayerId"));
                    score.setScore(rs.getInt("Score"));
                    score.setPlayedDate(rs.getTimestamp("PlayedDate"));
                    scores.add(score);
                }
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
