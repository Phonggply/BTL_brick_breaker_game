package BTL_brick_breaker_game.src.model;

import java.awt.Rectangle;

public class Paddle {

    private int x;
    private int y;
    private int width;
    private int height;
    private int speed;

    // shield
    private boolean shieldActive = false;

    public Paddle(int x, int y, int width, int height, int speed) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
    }

    public void moveLeft() {
        if (x - speed >= 0) {
            x -= speed;
        }
    }

    public void moveRight(int screenWidth) {
        if (x + width + speed <= screenWidth) {
            x += speed;
        }
    }

    public int getHitRegion(int ballX) {
        int relativeX = ballX - x;
        double region = (double) relativeX / width;
        if (region < 0.2) return 0;
        else if (region < 0.4) return 1;
        else if (region < 0.6) return 2;
        else if (region < 0.8) return 3;
        else return 4;
    }

    public void activateShield() {
        shieldActive = true;
    }

    public void deactivateShield() {
        shieldActive = false;
    }

    public boolean hasShield() {
        return shieldActive;
    }


    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setX(int x) {
        this.x = x;
    }
}