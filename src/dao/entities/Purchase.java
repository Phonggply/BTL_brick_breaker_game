package dao.entities;

import java.util.Date;

public class Purchase {
    private int purchaseId;
    private int playerId;
    private int itemId;
    private int pricePaid;
    private Date purchaseDate;
    private String itemName; 
    

    public int getPurchaseId() { return purchaseId; }
    public void setPurchaseId(int purchaseId) { this.purchaseId = purchaseId; }
    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }
    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }
    public int getPricePaid() { return pricePaid; }
    public void setPricePaid(int pricePaid) { this.pricePaid = pricePaid; }
    public Date getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(Date purchaseDate) { this.purchaseDate = purchaseDate; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
}
