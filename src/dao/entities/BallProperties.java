package dao.entities;

public class BallProperties {
    private int playerId;
    private double ballSpeed;
    private double ballSize;
    private int ballCount;
    
    public BallProperties() {
        this.ballSpeed = 5.0;
        this.ballSize = 1.0;
        this.ballCount = 1;
    }
    
    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }
    public double getBallSpeed() { return ballSpeed; }
    public void setBallSpeed(double ballSpeed) { this.ballSpeed = ballSpeed; }
    public double getBallSize() { return ballSize; }
    public void setBallSize(double ballSize) { this.ballSize = ballSize; }
    public int getBallCount() { return ballCount; }
    public void setBallCount(int ballCount) { this.ballCount = ballCount; }
}
