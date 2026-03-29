package model;

import java.awt.Rectangle;

public class Brick {

    public enum BrickType {
        NORMAL(1),
        STRONG_2(2),
        STRONG_3(3),
        UNBREAKABLE(-1);

        private final int defaultHealth;
        BrickType(int health) { this.defaultHealth = health; }
        public int getDefaultHealth() { return defaultHealth; }
    }

    private int x, y, width, height;
    private int health;
    private BrickType type;
    private boolean destroyed;

    public Brick(int x, int y, int width, int height, BrickType type) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;
        this.health = type.getDefaultHealth();
        this.destroyed = false;
    }

    public void hit() {
        if (type != BrickType.UNBREAKABLE) {
            health--;
            if (health <= 0) {
                destroyed = true;
            }
        }
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public BrickType getType() { return type; }
    public int getHealth() { return health; }
    public Rectangle getBounds() { return new Rectangle(x, y, width, height); }
    public boolean isDestroyed() { return destroyed; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
