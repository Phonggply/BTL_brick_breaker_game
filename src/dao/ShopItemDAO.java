package dao;

import dao.entities.ShopItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShopItemDAO {
    
    public ShopItemDAO() {}
    
    private Connection getConnection() {
        return DBConnection.getConnection();
    }
    
    public List<ShopItem> getAllItems() {
        String sql = "SELECT * FROM ShopItems WHERE IsActive = 1";
        List<ShopItem> items = new ArrayList<>();
        Connection conn = getConnection();
        if (conn == null) return items;
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
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
                    item.setActive(rs.getBoolean("IsActive"));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public boolean buyItem(int playerId, int itemId, int quantity) {
        Connection conn = getConnection();
        if (conn == null) return false;
        
        try {
            conn.setAutoCommit(false);
            
            // 1. Lấy giá vật phẩm
            int price = 0;
            String currencyType = "";
            String sqlPrice = "SELECT Price, CurrencyType FROM ShopItems WHERE ItemId = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlPrice)) {
                ps.setInt(1, itemId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        price = rs.getInt("Price");
                        currencyType = rs.getString("CurrencyType");
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }
            
            int totalPrice = price * quantity;
            
            // 2. Kiểm tra số dư
            String sqlCheckBalance = "SELECT Coins, Gems FROM Players WHERE PlayerId = ?";
            int currentBalance = 0;
            try (PreparedStatement ps = conn.prepareStatement(sqlCheckBalance)) {
                ps.setInt(1, playerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        currentBalance = currencyType.equals("Coin") ? rs.getInt("Coins") : rs.getInt("Gems");
                    }
                }
            }
            
            if (currentBalance < totalPrice) {
                conn.rollback();
                return false;
            }
            
            // 3. Trừ tiền
            String sqlDeduct = currencyType.equals("Coin") ? 
                "UPDATE Players SET Coins = Coins - ? WHERE PlayerId = ?" : 
                "UPDATE Players SET Gems = Gems - ? WHERE PlayerId = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlDeduct)) {
                ps.setInt(1, totalPrice);
                ps.setInt(2, playerId);
                ps.executeUpdate();
            }
            
            // 4. Thêm vào kho đồ
            String sqlCheckInventory = "SELECT Quantity FROM UserInventory WHERE PlayerId = ? AND ItemId = ?";
            boolean exists = false;
            try (PreparedStatement ps = conn.prepareStatement(sqlCheckInventory)) {
                ps.setInt(1, playerId);
                ps.setInt(2, itemId);
                try (ResultSet rs = ps.executeQuery()) {
                    exists = rs.next();
                }
            }
            
            if (exists) {
                String sqlUpdateInv = "UPDATE UserInventory SET Quantity = Quantity + ? WHERE PlayerId = ? AND ItemId = ?";
                try (PreparedStatement ps = conn.prepareStatement(sqlUpdateInv)) {
                    ps.setInt(1, quantity);
                    ps.setInt(2, playerId);
                    ps.setInt(3, itemId);
                    ps.executeUpdate();
                }
            } else {
                String sqlInsertInv = "INSERT INTO UserInventory (PlayerId, ItemId, Quantity, IsEquipped, PurchasedAt) VALUES (?, ?, ?, 0, CURRENT_TIMESTAMP)";
                try (PreparedStatement ps = conn.prepareStatement(sqlInsertInv)) {
                    ps.setInt(1, playerId);
                    ps.setInt(2, itemId);
                    ps.setInt(3, quantity);
                    ps.executeUpdate();
                }
            }
            
            // 5. Ghi lịch sử mua
            String sqlHistory = "INSERT INTO PurchaseHistory (PlayerId, ItemId, PricePaid, PurchaseDate) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
            try (PreparedStatement ps = conn.prepareStatement(sqlHistory)) {
                ps.setInt(1, playerId);
                ps.setInt(2, itemId);
                ps.setInt(3, totalPrice);
                ps.executeUpdate();
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
        return false;
    }
}
