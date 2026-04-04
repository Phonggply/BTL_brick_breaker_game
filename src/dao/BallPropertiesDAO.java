package dao;

import dao.entities.BallProperties;
import java.sql.*;

public class BallPropertiesDAO {
    
    public BallPropertiesDAO() {}
    
    public BallProperties getBallProperties(int playerId) {
        String sql = "SELECT * FROM BallProperties WHERE PlayerId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
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
}
