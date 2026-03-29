package dao.entities;

import java.util.Date;

public class SaveGame {
    private int saveId;
    private int playerId;
    private String saveName;
    private int level;
    private int currentScore;
    private int lives;
    private int bricksRemaining;
    private double ballPositionX;
    private double ballPositionY;
    private double paddlePositionX;
    private String gameState;
    private Date saveDate;
    
    // Getters and Setters
    public int getSaveId() { return saveId; }
    public void setSaveId(int saveId) { this.saveId = saveId; }
    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }
    public String getSaveName() { return saveName; }
    public void setSaveName(String saveName) { this.saveName = saveName; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public int getCurrentScore() { return currentScore; }
    public void setCurrentScore(int currentScore) { this.currentScore = currentScore; }
    public int getLives() { return lives; }
    public void setLives(int lives) { this.lives = lives; }
    public int getBricksRemaining() { return bricksRemaining; }
    public void setBricksRemaining(int bricksRemaining) { this.bricksRemaining = bricksRemaining; }
    public double getBallPositionX() { return ballPositionX; }
    public void setBallPositionX(double ballPositionX) { this.ballPositionX = ballPositionX; }
    public double getBallPositionY() { return ballPositionY; }
    public void setBallPositionY(double ballPositionY) { this.ballPositionY = ballPositionY; }
    public double getPaddlePositionX() { return paddlePositionX; }
    public void setPaddlePositionX(double paddlePositionX) { this.paddlePositionX = paddlePositionX; }
    public String getGameState() { return gameState; }
    public void setGameState(String gameState) { this.gameState = gameState; }
    public Date getSaveDate() { return saveDate; }
    public void setSaveDate(Date saveDate) { this.saveDate = saveDate; }
}
