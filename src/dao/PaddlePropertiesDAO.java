package dao;

import dao.entities.PaddleProperties;
import java.sql.*;

public class PaddlePropertiesDAO {
    
    public PaddlePropertiesDAO() {}
    
    public PaddleProperties getPaddleProperties(int playerId) {
        String sql = "SELECT * FROM PaddleProperties WHERE PlayerId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PaddleProperties props = new PaddleProperties();
                    props.setPlayerId(rs.getInt("PlayerId"));
                    props.setPaddleWidth(rs.getDouble("PaddleWidth"));
                    props.setPaddleSpeed(rs.getDouble("PaddleSpeed"));
                    return props;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
