package BTL_brick_breaker_game.src.dao;

import BTL_brick_breaker_game.src.dao.entities.Purchase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseDAO {
    
    private Connection connection;
    
    public PurchaseDAO() {
        this.connection = DBConnection.getConnection();
    }
    
    /**
     * sp_GetPurchaseHistory - Lấy lịch sử mua hàng
     */
    public List<Purchase> getPurchaseHistory(int playerId) {
        String sql = "{call sp_GetPurchaseHistory(?)}";
        List<Purchase> purchases = new ArrayList<>();
        
        try (CallableStatement cstmt = connection.prepareCall(sql)) {
            cstmt.setInt(1, playerId);
            ResultSet rs = cstmt.executeQuery();
            
            while (rs.next()) {
                Purchase purchase = new Purchase();
                purchase.setPurchaseId(rs.getInt("PurchaseId"));
                purchase.setPlayerId(rs.getInt("PlayerId"));
                purchase.setItemId(rs.getInt("ItemId"));
                purchase.setPricePaid(rs.getInt("PricePaid"));
                purchase.setPurchaseDate(rs.getTimestamp("PurchaseDate"));
                purchase.setItemName(rs.getString("ItemName"));
                purchases.add(purchase);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return purchases;
    }
    
    public void close() {
        DBConnection.closeConnection();
    }
}