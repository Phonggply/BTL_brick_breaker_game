package dao;

import dao.entities.Score;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScoreDAO {
    
    public ScoreDAO() {}
    
    public List<Score> getLeaderboardByMaxScoreSum() throws SQLException {
        // Query mặc định: Tính tổng điểm cao nhất của mỗi level
        String sql = "SELECT p.UserName, SUM(ms.MaxScore) as TotalPoint " +
                     "FROM Players p " +
                     "JOIN ( " +
                     "    SELECT PlayerId, IFNULL(LevelNumber, 1) as Lvl, MAX(Score) as MaxScore " +
                     "    FROM Scores " +
                     "    GROUP BY PlayerId, Lvl " +
                     ") ms ON p.PlayerId = ms.PlayerId " +
                     "GROUP BY p.PlayerId, p.UserName " +
                     "ORDER BY TotalPoint DESC";
        
        // Query dự phòng: Nếu bảng Scores không có cột LevelNumber
        String sqlFallback = "SELECT p.UserName, SUM(s.Score) as TotalPoint " +
                            "FROM Players p " +
                            "JOIN Scores s ON p.PlayerId = s.PlayerId " +
                            "GROUP BY p.PlayerId, p.UserName " +
                            "ORDER BY TotalPoint DESC";
        
        List<Score> leaderboard = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        if (conn == null) throw new SQLException("Connection null");

        try (Statement st = conn.createStatement()) {
            ResultSet rs = null;
            try {
                rs = st.executeQuery(sql);
            } catch (SQLException e) {
                System.err.println("Ranking Error (using fallback): " + e.getMessage());
                rs = st.executeQuery(sqlFallback);
            }
            
            while (rs != null && rs.next()) {
                Score item = new Score();
                item.setUserName(rs.getString("UserName"));
                item.setScore(rs.getInt("TotalPoint"));
                leaderboard.add(item);
            }
            if (rs != null) rs.close();
        }
        return leaderboard;
    }

    public int gameOver(int playerId, int score, int levelNumber) {
        System.out.println("Saving game results: Player=" + playerId + ", Score=" + score + ", Level=" + levelNumber);
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return 0;
            conn.setAutoCommit(false);
            
            try {
                // 1. Lưu điểm vào bảng Scores
                String sqlInsertScore = "INSERT INTO Scores (PlayerId, Score, LevelNumber, PlayedDate) VALUES (?, ?, ?, NOW())";
                try (PreparedStatement ps = conn.prepareStatement(sqlInsertScore)) {
                    ps.setInt(1, playerId);
                    ps.setInt(2, score);
                    ps.setInt(3, levelNumber);
                    ps.executeUpdate();
                } catch (SQLException e) {
                    // Fallback nếu thiếu cột LevelNumber
                    String sqlFallback = "INSERT INTO Scores (PlayerId, Score, PlayedDate) VALUES (?, ?, NOW())";
                    try (PreparedStatement psF = conn.prepareStatement(sqlFallback)) {
                        psF.setInt(1, playerId);
                        psF.setInt(2, score);
                        psF.executeUpdate();
                    }
                }
                
                // 2. Cập nhật thống kê và Level cao nhất (HighestLevel)
                // Dùng GREATEST để đảm bảo level không bị giảm xuống
                String sqlUpdateStats = "UPDATE GameStats SET GamesPlayed = GamesPlayed + 1, " +
                                       "TotalScore = TotalScore + ?, " +
                                       "HighestLevel = GREATEST(HighestLevel, ?) " +
                                       "WHERE PlayerId = ?";
                try (PreparedStatement ps = conn.prepareStatement(sqlUpdateStats)) {
                    ps.setInt(1, score);
                    ps.setInt(2, levelNumber);
                    ps.setInt(3, playerId);
                    if (ps.executeUpdate() == 0) {
                        // Nếu chưa có dòng stats nào thì tạo mới
                        String sqlInsertStats = "INSERT INTO GameStats (PlayerId, GamesPlayed, TotalScore, HighestLevel) VALUES (?, 1, ?, ?)";
                        try (PreparedStatement psIns = conn.prepareStatement(sqlInsertStats)) {
                            psIns.setInt(1, playerId);
                            psIns.setInt(2, score);
                            psIns.setInt(3, levelNumber);
                            psIns.executeUpdate();
                        }
                    }
                }
                
                // 3. Cộng xu thưởng
                int coinReward = score / 5; // Tăng tỉ lệ thưởng xu
                String sqlAddCoins = "UPDATE Players SET Coins = Coins + ? WHERE PlayerId = ?";
                try (PreparedStatement ps = conn.prepareStatement(sqlAddCoins)) {
                    ps.setInt(1, coinReward);
                    ps.setInt(2, playerId);
                    ps.executeUpdate();
                }
                
                conn.commit();
                return coinReward;
            } catch (SQLException e) {
                conn.rollback();
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
