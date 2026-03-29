package dao;

import dao.entities.BallProperties;
import java.sql.*;

public class BallPropertiesDAO {
    
    private Connection connection;
    
    public BallPropertiesDAO() {
        this.connection = DBConnection.getConnection();
    }
    
    /**
     * sp_GetBallProperties - Lấy thuộc tính bóng
     */
    public BallProperties getBallProperties(int playerId) {
        String sql = "{call sp_GetBallProperties(?)}";
        
        try (CallableStatement cstmt = connection.prepareCall(sql)) {
            cstmt.setInt(1, playerId);
            ResultSet rs = cstmt.executeQuery();
            
            if (rs.next()) {
                BallProperties props = new BallProperties();
                props.setPlayerId(rs.getInt("PlayerId"));
                props.setBallSpeed(rs.getDouble("BallSpeed"));
                props.setBallSize(rs.getDouble("BallSize"));
                props.setBallCount(rs.getInt("BallCount"));
                return props;
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
