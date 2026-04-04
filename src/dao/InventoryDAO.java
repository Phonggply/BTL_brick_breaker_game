package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import dao.entities.ShopItem;

public class InventoryDAO {
    
    public InventoryDAO() {}
    
    public List<ShopItem> getOwnedItems(int playerId) {
        String sql = "SELECT si.* FROM ShopItems si " +
                     "JOIN UserInventory ui ON si.ItemId = ui.ItemId " +
                     "WHERE ui.PlayerId = ? AND ui.Quantity > 0";
        List<ShopItem> items = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return items;
            ps = conn.prepareStatement(sql);
            ps.setInt(1, playerId);
            rs = ps.executeQuery();
            while (rs.next()) {
                ShopItem item = new ShopItem();
                item.setItemId(rs.getInt("ItemId"));
                item.setItemName(rs.getString("ItemName"));
                item.setItemType(rs.getString("ItemType"));
                item.setEffectType(rs.getString("EffectType"));
                item.setEffectValue(rs.getInt("EffectValue"));
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(null, ps, rs);
        }
        return items;
    }

    // Hàm tiêu thụ vật phẩm (trừ 1 khi dùng)
    public void consumeItem(int playerId, int itemId) {
        String sql = "UPDATE UserInventory SET Quantity = GREATEST(0, Quantity - 1) " +
                     "WHERE PlayerId = ? AND ItemId = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return;
            ps = conn.prepareStatement(sql);
            ps.setInt(1, playerId);
            ps.setInt(2, itemId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(null, ps, null);
        }
    }
}
