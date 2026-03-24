package BTL_brick_breaker_game.src.dao;

import BTL_brick_breaker_game.src.dao.entities.ShopItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShopItemDAO {
    
    private Connection connection;
    
    public ShopItemDAO() {
        this.connection = DBConnection.getConnection();
    }
    
    /**
     * sp_BuyItem - Mua vật phẩm
     */
    public String buyItem(int playerId, int itemId, int quantity) {
        String sql = "{call sp_BuyItem(?, ?, ?)}";
        
        try (CallableStatement cstmt = connection.prepareCall(sql)) {
            cstmt.setInt(1, playerId);
            cstmt.setInt(2, itemId);
            cstmt.setInt(3, quantity);
            
            ResultSet rs = cstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("Message");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
        return "Mua thất bại";
    }
    
    /**
     * sp_UsePowerUp - Sử dụng PowerUp
     */
    public String usePowerUp(int playerId, int inventoryId) {
        String sql = "{call sp_UsePowerUp(?, ?)}";
        
        try (CallableStatement cstmt = connection.prepareCall(sql)) {
            cstmt.setInt(1, playerId);
            cstmt.setInt(2, inventoryId);
            
            ResultSet rs = cstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("Message");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
        return "Sử dụng thất bại";
    }
    
    /**
     * vw_ShopItems - Lấy danh sách shop
     */
    public List<ShopItem> getAllShopItems() {
        String sql = "SELECT * FROM vw_ShopItems ORDER BY SortOrder, Price";
        List<ShopItem> items = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                ShopItem item = new ShopItem();
                item.setItemId(rs.getInt("ItemId"));
                item.setItemName(rs.getString("ItemName"));
                item.setDescription(rs.getString("Description"));
                item.setPrice(rs.getInt("Price"));
                item.setCurrencyType(rs.getString("CurrencyType"));
                item.setItemType(rs.getString("ItemType"));
                item.setEffectType(rs.getString("EffectType"));
                item.setEffectValue(rs.getInt("EffectValue"));
                item.setImagePath(rs.getString("ImagePath"));
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
    
    /**
     * Lấy item theo loại
     */
    public List<ShopItem> getItemsByType(String itemType) {
        String sql = "SELECT * FROM ShopItems WHERE ItemType = ? AND IsActive = 1";
        List<ShopItem> items = new ArrayList<>();
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, itemType);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                ShopItem item = new ShopItem();
                item.setItemId(rs.getInt("ItemId"));
                item.setItemName(rs.getString("ItemName"));
                item.setDescription(rs.getString("Description"));
                item.setPrice(rs.getInt("Price"));
                item.setCurrencyType(rs.getString("CurrencyType"));
                item.setItemType(rs.getString("ItemType"));
                item.setEffectType(rs.getString("EffectType"));
                item.setEffectValue(rs.getInt("EffectValue"));
                item.setImagePath(rs.getString("ImagePath"));
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
    
    public void close() {
        DBConnection.closeConnection();
    }
}