package dao.entities;

import java.util.Date;

public class Score {
    private int scoreId;
    private int playerId;
    private int score;
    private int levelNumber;
    private Date playedDate;
    private String userName; 
    
    public Score() {}
    
    public Score(int playerId, int score, int levelNumber) {
        this.playerId = playerId;
        this.score = score;
        this.levelNumber = levelNumber;
        this.playedDate = new Date();
    }
    

    public int getScoreId() { return scoreId; }
    public void setScoreId(int scoreId) { this.scoreId = scoreId; }
    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public int getLevelNumber() { return levelNumber; }
    public void setLevelNumber(int levelNumber) { this.levelNumber = levelNumber; }
    public Date getPlayedDate() { return playedDate; }
    public void setPlayedDate(Date playedDate) { this.playedDate = playedDate; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
}
