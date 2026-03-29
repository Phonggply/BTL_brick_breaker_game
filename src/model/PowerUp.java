package model;

import java.awt.Rectangle;

public class PowerUp {
    public enum Type {
        MULTIBALL,
        EXPAND,
        SHIELD
    }

    private int x, y;
    private int width = 30, height = 30;
    private int speed = 3;
    private Type type;
    private boolean active = true;

    public PowerUp(int x, int y, Type type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public void move() {
        y += speed;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public Type getType() { return type; }
    public boolean isActive() { return active; }
    public void deactivate() { this.active = false; }
}
