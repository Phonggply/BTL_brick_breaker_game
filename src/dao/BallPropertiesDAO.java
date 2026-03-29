package dao;

import dao.entities.BallProperties;
import java.sql.*;

public class BallPropertiesDAO {
    
    public BallPropertiesDAO() {}
    
    private Connection getConnection() {
        return DBConnection.getConnection();
    }
    
    /**
     * Lấy thuộc tính bóng của người chơi từ SQLite
     */
    public BallProperties getBallProperties(int playerId) {
        String sql = "SELECT * FROM BallProperties WHERE PlayerId = ?";
        Connection conn = getConnection();
        if (conn == null) return null;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BallProperties props = new BallProperties();
                    props.setPlayerId(rs.getInt("PlayerId"));
                    props.setBallSpeed(rs.getDouble("BallSpeed"));
                    props.setBallSize(rs.getDouble("BallSize"));
                    props.setBallCount(rs.getInt("BallCount"));
                    return props;
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
