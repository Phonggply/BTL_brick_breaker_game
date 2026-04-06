package dao.entities;

import java.util.Date;

public class Player {
    private int playerId;
    private String userName;
    private String email;
    private Date createdDate;
    private int coins;
    private int gems;
    private int highestLevel = 1; // Mặc định là màn 1
    private Date createdAt;
    
    public int getHighestLevel() { return highestLevel; }
    public void setHighestLevel(int highestLevel) { this.highestLevel = highestLevel; }
    
    public Player() {}
    
    public Player(String userName, String email) {
        this.userName = userName;
        this.email = email;
        this.coins = 1000;
        this.gems = 50;
        this.createdDate = new Date();
        this.createdAt = new Date();
    }
    

    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
    public int getCoins() { return coins; }
    public void setCoins(int coins) { this.coins = coins; }
    public int getGems() { return gems; }
    public void setGems(int gems) { this.gems = gems; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
