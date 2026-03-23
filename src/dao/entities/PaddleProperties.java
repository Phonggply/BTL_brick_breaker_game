package BTL_brick_breaker_game.src.dao.entities;

public class PaddleProperties {
    private int playerId;
    private double paddleWidth;
    private double paddleSpeed;
    
    public PaddleProperties() {
        this.paddleWidth = 100.0;
        this.paddleSpeed = 10.0;
    }
    
    // Getters and Setters
    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }
    public double getPaddleWidth() { return paddleWidth; }
    public void setPaddleWidth(double paddleWidth) { this.paddleWidth = paddleWidth; }
    public double getPaddleSpeed() { return paddleSpeed; }
    public void setPaddleSpeed(double paddleSpeed) { this.paddleSpeed = paddleSpeed; }
}