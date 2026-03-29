package model;

import java.awt.Rectangle;

public class Ball {   
    private double x, y;
    private int size;
    private double xDir, yDir;
    private double speed;

    public Ball(int x, int y, int size, double speed) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.speed = speed;
        this.xDir = 0; // Ban đầu bay thẳng lên hoặc theo góc mặc định
        this.yDir = -1;
    }

    public void move() {
        x += xDir * speed;
        y += yDir * speed;
    }

    public void reverseX() { xDir = -xDir; }
    public void reverseY() { yDir = -yDir; }

    public void setDirection(double angleDegrees) {
        double radians = Math.toRadians(angleDegrees);
        // Trong Java Swing, trục Y hướng xuống, nên bay lên thì sin phải âm
        this.xDir = Math.cos(radians);
        this.yDir = -Math.sin(radians);
    }

    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, size, size);
    }

    public int getX() { return (int)x; }
    public int getY() { return (int)y; }
    public double getXDir() { return xDir; }
    public double getYDir() { return yDir; }
    public int getSize() { return size; }
    public void setXDir(double xDir) { this.xDir = xDir; }
    public void setYDir(double yDir) { this.yDir = yDir; }
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
