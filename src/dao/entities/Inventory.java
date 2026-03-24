package BTL_brick_breaker_game.src.dao.entities;

import java.util.Date;

public class Inventory {
    private int inventoryId;
    private int playerId;
    private int itemId;
    private int quantity;
    private boolean isEquipped;
    private Date purchasedAt;
    
    // Additional fields for display
    private String itemName;
    private String itemType;
    private String effectType;
    private int effectValue;
    
    // Getters and Setters
    public int getInventoryId() { return inventoryId; }
    public void setInventoryId(int inventoryId) { this.inventoryId = inventoryId; }
    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }
    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public boolean isEquipped() { return isEquipped; }
    public void setEquipped(boolean equipped) { isEquipped = equipped; }
    public Date getPurchasedAt() { return purchasedAt; }
    public void setPurchasedAt(Date purchasedAt) { this.purchasedAt = purchasedAt; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }
    public String getEffectType() { return effectType; }
    public void setEffectType(String effectType) { this.effectType = effectType; }
    public int getEffectValue() { return effectValue; }
    public void setEffectValue(int effectValue) { this.effectValue = effectValue; }
}