package dao;

import dao.entities.Score;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScoreDAO {
    
    public ScoreDAO() {}
    
    public List<Score> getLeaderboardByMaxScoreSum() throws SQLException {
        String sql = "SELECT p.UserName, SUM(ms.MaxScore) as TotalPoint " +
                     "FROM Players p " +
                     "JOIN ( " +
                     "    SELECT PlayerId, IFNULL(LevelNumber, 1) as Lvl, MAX(Score) as MaxScore " +
                     "    FROM Scores " +
                     "    GROUP BY PlayerId, Lvl " +
                     ") ms ON p.PlayerId = ms.PlayerId " +
                     "GROUP BY p.PlayerId, p.UserName " +
                     "ORDER BY TotalPoint DESC";
        
        List<Score> leaderboard = new ArrayList<>();
        System.out.println("--- FETCHING LEADERBOARD ---");
        
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Cannot establish database connection (Connection is null)");
        }
        
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Score item = new Score();
                String name = rs.getString("UserName");
                int total = rs.getInt("TotalPoint");
                item.setUserName(name != null ? name : "UNKNOWN");
                item.setScore(total);
                leaderboard.add(item);
                System.out.println(">> Found: " + name + " - " + total);
            }
        } catch (SQLException e) {
            System.err.println("!!! SQL Error: " + e.getMessage());
            throw e;
        }
        System.out.println("--- FINISHED: " + leaderboard.size() + " rows ---");
        return leaderboard;
    }

    public int gameOver(int playerId, int score, int levelNumber) {
        System.out.println("Saving score: PlayerID=" + playerId + ", Score=" + score + ", Level=" + levelNumber);
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                System.err.println("ERROR: Cannot connect to Database!");
                javax.swing.JOptionPane.showMessageDialog(null, "ERROR: Cannot connect to Database!", "Connection Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                return 0;
            }
            conn.setAutoCommit(false);
            
            try {
                // Try insert score
                String sqlInsertScore = "INSERT INTO Scores (PlayerId, Score, LevelNumber, PlayedDate) VALUES (?, ?, ?, NOW())";
                try (PreparedStatement ps = conn.prepareStatement(sqlInsertScore)) {
                    ps.setInt(1, playerId);
                    ps.setInt(2, score);
                    ps.setInt(3, levelNumber);
                    ps.executeUpdate();
                    System.out.println("-> Saved to Scores table.");
                } catch (SQLException e) {
                    System.out.println("-> Warning: Error saving with LevelNumber, trying fallback: " + e.getMessage());
                    String sqlFallback = "INSERT INTO Scores (PlayerId, Score, PlayedDate) VALUES (?, ?, NOW())";
                    try (PreparedStatement psFallback = conn.prepareStatement(sqlFallback)) {
                        psFallback.setInt(1, playerId);
                        psFallback.setInt(2, score);
                        psFallback.executeUpdate();
                        System.out.println("-> Fallback save success.");
                    }
                }
                
                // Update stats
                String sqlUpdateStats = "UPDATE GameStats SET GamesPlayed = GamesPlayed + 1, TotalScore = TotalScore + ?, HighestLevel = GREATEST(HighestLevel, ?) WHERE PlayerId = ?";
                try (PreparedStatement ps = conn.prepareStatement(sqlUpdateStats)) {
                    ps.setInt(1, score);
                    ps.setInt(2, levelNumber);
                    ps.setInt(3, playerId);
                    if (ps.executeUpdate() == 0) {
                        String sqlInsertStats = "INSERT INTO GameStats (PlayerId, GamesPlayed, TotalScore, BricksBroken, HighestLevel) VALUES (?, 1, ?, 0, ?)";
                        try (PreparedStatement psIns = conn.prepareStatement(sqlInsertStats)) {
                            psIns.setInt(1, playerId);
                            psIns.setInt(2, score);
                            psIns.setInt(3, levelNumber);
                            psIns.executeUpdate();
                        }
                    }
                }
                
                // Add coins
                int coinReward = score / 10;
                String sqlAddCoins = "UPDATE Players SET Coins = Coins + ? WHERE PlayerId = ?";
                try (PreparedStatement ps = conn.prepareStatement(sqlAddCoins)) {
                    ps.setInt(1, coinReward);
                    ps.setInt(2, playerId);
                    ps.executeUpdate();
                }
                
                conn.commit();
                System.out.println("===> ALL DATA COMMITTED TO DATABASE.");
                return coinReward;
                
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("SQL ERROR: " + e.getMessage());
                javax.swing.JOptionPane.showMessageDialog(null, "SCORE SAVE ERROR: " + e.getMessage(), "Database Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public List<Score> getTopScores(int topCount) {
        String sql = "SELECT p.UserName, s.Score, s.PlayedDate " +
                     "FROM Scores s " +
                     "JOIN Players p ON s.PlayerId = p.PlayerId " +
                     "ORDER BY s.Score DESC LIMIT ?";
        List<Score> scores = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
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
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
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
}
