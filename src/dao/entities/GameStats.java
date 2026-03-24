package BTL_brick_breaker_game.src.dao.entities;

public class GameStats {
    private int statsId;
    private int playerId;
    private int gamesPlayed;
    private int totalScore;
    private int bricksBroken;
    private int highestLevel;
    
    // Getters and Setters
    public int getStatsId() { return statsId; }
    public void setStatsId(int statsId) { this.statsId = statsId; }
    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }
    public int getGamesPlayed() { return gamesPlayed; }
    public void setGamesPlayed(int gamesPlayed) { this.gamesPlayed = gamesPlayed; }
    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }
    public int getBricksBroken() { return bricksBroken; }
    public void setBricksBroken(int bricksBroken) { this.bricksBroken = bricksBroken; }
    public int getHighestLevel() { return highestLevel; }
    public void setHighestLevel(int highestLevel) { this.highestLevel = highestLevel; }
}