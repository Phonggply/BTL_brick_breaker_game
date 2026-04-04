package model;

import java.awt.Rectangle;

public class Paddle {

    private int x, y, width, height, speed;
    private int originalWidth;
    private int expandCount = 0;
    private static final int MAX_EXPAND = 5;
    private long shieldEndTime = 0; // Thời điểm kết thúc shield (ms)

    public Paddle(int x, int y, int width, int height, int speed) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.originalWidth = width;
        this.height = height;
        this.speed = speed;
    }

    public void moveLeft() {
        x -= speed;
        if (x < 0) x = 0;
    }

    public void moveRight(int screenWidth) {
        x += speed;
        if (x + width > screenWidth) x = screenWidth - width;
    }

    public void expand(int screenWidth) {
        int maxWidth = (screenWidth * 3) / 5; // Giới hạn 3/5 màn hình
        if (expandCount < MAX_EXPAND && width < maxWidth) {
            this.width += 20; // Mỗi lần tăng thêm 20px
            if (this.width > maxWidth) this.width = maxWidth;
            
            // KIỂM TRA BIÊN: Nếu nở ra mà vượt quá lề phải thì lùi lại
            if (x + width > screenWidth) {
                x = screenWidth - width;
            }
            
            expandCount++;
        }
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void reset() {
        this.width = originalWidth;
        this.expandCount = 0;
        this.shieldEndTime = 0;
    }

    public void activateShield(int durationSeconds) {
        long durationMs = durationSeconds * 1000L;
        // Nếu đang có shield thì cộng dồn thời gian hoặc reset lại timer
        this.shieldEndTime = System.currentTimeMillis() + durationMs;
    }

    public boolean isShieldActive() {
        return System.currentTimeMillis() < shieldEndTime;
    }
    
    public long getShieldRemainingTime() {
        return Math.max(0, shieldEndTime - System.currentTimeMillis());
    }

    public Rectangle getBounds() { return new Rectangle(x, y, width, height); }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
