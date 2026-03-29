package dao;

import dao.entities.PaddleProperties;
import java.sql.*;

public class PaddlePropertiesDAO {
    
    private Connection connection;
    
    public PaddlePropertiesDAO() {
        this.connection = DBConnection.getConnection();
    }
    
    /**
     * sp_GetPaddleProperties - Lấy thuộc tính vợt
     */
    public PaddleProperties getPaddleProperties(int playerId) {
        String sql = "{call sp_GetPaddleProperties(?)}";
        
        try (CallableStatement cstmt = connection.prepareCall(sql)) {
            cstmt.setInt(1, playerId);
            ResultSet rs = cstmt.executeQuery();
            
            if (rs.next()) {
                PaddleProperties props = new PaddleProperties();
                props.setPlayerId(rs.getInt("PlayerId"));
                props.setPaddleWidth(rs.getDouble("PaddleWidth"));
                props.setPaddleSpeed(rs.getDouble("PaddleSpeed"));
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
