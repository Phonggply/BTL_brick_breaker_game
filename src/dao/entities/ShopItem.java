package dao.entities;

public class ShopItem {
    private int itemId;
    private String itemName;
    private String description;
    private int price;
    private String currencyType; 
    private String itemType; 
    private String effectType;
    private int effectValue;
    private String imagePath;
    private boolean isActive;
    

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
    public String getCurrencyType() { return currencyType; }
    public void setCurrencyType(String currencyType) { this.currencyType = currencyType; }
    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }
    public String getEffectType() { return effectType; }
    public void setEffectType(String effectType) { this.effectType = effectType; }
    public int getEffectValue() { return effectValue; }
    public void setEffectValue(int effectValue) { this.effectValue = effectValue; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
