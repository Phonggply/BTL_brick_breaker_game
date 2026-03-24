package BTL_brick_breaker_game.src.dao;

import BTL_brick_breaker_game.src.dao.entities.Inventory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryDAO {
    
    private Connection connection;
    
    public InventoryDAO() {
        this.connection = DBConnection.getConnection();
    }
    
    /**
     * sp_GetInventory - Lấy kho đồ của người chơi
     */
    public List<Inventory> getPlayerInventory(int playerId) {
        String sql = "{call sp_GetInventory(?)}";
        List<Inventory> inventory = new ArrayList<>();
        
        try (CallableStatement cstmt = connection.prepareCall(sql)) {
            cstmt.setInt(1, playerId);
            ResultSet rs = cstmt.executeQuery();
            
            while (rs.next()) {
                Inventory item = new Inventory();
                item.setInventoryId(rs.getInt("InventoryId"));
                item.setPlayerId(rs.getInt("PlayerId"));
                item.setItemId(rs.getInt("ItemId"));
                item.setQuantity(rs.getInt("Quantity"));
                item.setEquipped(rs.getBoolean("IsEquipped"));
                item.setPurchasedAt(rs.getTimestamp("PurchasedAt"));
                item.setItemName(rs.getString("ItemName"));
                item.setItemType(rs.getString("ItemType"));
                item.setEffectType(rs.getString("EffectType"));
                item.setEffectValue(rs.getInt("EffectValue"));
                inventory.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return inventory;
    }
    
    /**
     * sp_EquipItem - Trang bị vật phẩm
     */
    public String equipItem(int playerId, int itemId) {
        String sql = "{call sp_EquipItem(?, ?)}";
        
        try (CallableStatement cstmt = connection.prepareCall(sql)) {
            cstmt.setInt(1, playerId);
            cstmt.setInt(2, itemId);
            
            ResultSet rs = cstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("Message");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
        return "Trang bị thất bại";
    }
    
    public void close() {
        DBConnection.closeConnection();
    }
}