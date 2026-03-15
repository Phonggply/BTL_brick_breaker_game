package BTL_brick_breaker_game.src.model;
import java.awt.Rectangle;

public class PowerUp {

    private int x;
    private int y;

    private int width = 30;
    private int height = 30;

    private int speed = 2;

    private PowerUpType type;

    private boolean active = true;
    public enum PowerUpType {
        EXPAND,
        MULTI_BALL,
        SHIELD
    }
    public PowerUp(int x, int y, PowerUpType type){
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public void update(){
        y += speed;
    }

    public Rectangle getBounds(){
        return new Rectangle(x, y, width, height);
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public PowerUpType getType(){
        return type;
    }

    public boolean isActive(){
        return active;
    }

    public void deactivate(){
        active = false;
    }
}
