package BTL_brick_breaker_game.src.dao.entities;

import java.util.Date;

public class Score {
    private int scoreId;
    private int playerId;
    private int score;
    private Date playedDate;
    private String userName; // For display when joining
    
    public Score() {}
    
    public Score(int playerId, int score) {
        this.playerId = playerId;
        this.score = score;
        this.playedDate = new Date();
    }
    
    // Getters and Setters
    public int getScoreId() { return scoreId; }
    public void setScoreId(int scoreId) { this.scoreId = scoreId; }
    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public Date getPlayedDate() { return playedDate; }
    public void setPlayedDate(Date playedDate) { this.playedDate = playedDate; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
}