package BTL_brick_breaker_game.src.model;

import java.awt.Rectangle;

 public class Ball {   
    private int x;
    private int y;
    private int size;
    private int xDir;
    private int yDir;
    private int speed;
    private final int MAX_X_DIR = 2;


    public Ball(int x, int y, int size, int speed) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.speed = speed;
        this.xDir = 0;
        this.yDir = -1;
    }
    public void move() {
        x += xDir * speed;
        y += yDir * speed;
    }
    public void reverseX () {
        xDir = -xDir;
    }
    public void reverseY () {
        yDir = - yDir;
    }
    public void setXDir(int xDir){
        if (xDir > MAX_X_DIR) xDir = MAX_X_DIR;
        if (xDir < -MAX_X_DIR) xDir = -MAX_X_DIR;
        this.xDir = xDir;
    }
    public void setYDir(int yDir){
        this.yDir = yDir;
    }
    public void handlePaddleCollision(int region) {
        switch (region) {
            case 0:
                setXDir(-2);
                break;
            case 1:
                setXDir(-1);
                break;
            case 2:
                setXDir(0);
                break;
            case 3:
                setXDir(1);
                break;
            case 4:
                setXDir(2);
                break;
        }
        yDir = -1;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSize() {
        return size;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
