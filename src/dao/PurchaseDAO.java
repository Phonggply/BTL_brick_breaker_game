package dao;

import dao.entities.Purchase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseDAO {
    
    public PurchaseDAO() {}
    
    public List<Purchase> getPlayerPurchases(int playerId) {
        String sql = "SELECT * FROM PurchaseHistory WHERE PlayerId = ? ORDER BY PurchaseDate DESC";
        List<Purchase> purchases = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Purchase p = new Purchase();
                    p.setPurchaseId(rs.getInt("HistoryId")); // Note: Check table column name
                    p.setPlayerId(rs.getInt("PlayerId"));
                    p.setItemId(rs.getInt("ItemId"));
                    p.setPricePaid(rs.getInt("PricePaid"));
                    p.setPurchaseDate(rs.getTimestamp("PurchaseDate"));
                    purchases.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return purchases;
    }
}
