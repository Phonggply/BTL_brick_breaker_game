package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import dao.entities.ShopItem;

public class InventoryDAO {
    
    public InventoryDAO() {}
    
    private Connection getConnection() {
        return DBConnection.getConnection();
    }
    
    // Lấy danh sách các vật phẩm người chơi đang sở hữu
    public List<ShopItem> getOwnedItems(int playerId) {
        String sql = "SELECT si.* FROM ShopItems si " +
                     "JOIN UserInventory ui ON si.ItemId = ui.ItemId " +
                     "WHERE ui.PlayerId = ? AND ui.Quantity > 0";
        List<ShopItem> items = new ArrayList<>();
        Connection conn = getConnection();
        if (conn == null) return items;
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ShopItem item = new ShopItem();
                    item.setItemId(rs.getInt("ItemId"));
                    item.setItemName(rs.getString("ItemName"));
                    item.setItemType(rs.getString("ItemType"));
                    item.setEffectType(rs.getString("EffectType"));
                    item.setEffectValue(rs.getInt("EffectValue"));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
}
