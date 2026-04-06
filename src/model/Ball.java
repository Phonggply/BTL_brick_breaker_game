package model;

import java.awt.Rectangle;

public class Ball {   
    private double x, y;
    private int size;
    private double angleDegrees; // Góc di chuyển (độ)
    private double speed;

    public Ball(int x, int y, int size, double speed) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.speed = speed;
        this.angleDegrees = 90; // Mặc định bay lên
    }

    public void move(double fraction) {
        double radians = Math.toRadians(angleDegrees);
        x += Math.cos(radians) * speed * fraction;
        y += -Math.sin(radians) * speed * fraction; // Trục Y hướng xuống nên sin âm là bay lên
    }

    // Nảy gương theo trục X (Chạm tường trái/phải hoặc cạnh bên Paddle)
    public void reverseX() {
        angleDegrees = 180 - angleDegrees;
        fixAngle();
    }

    // Nảy gương theo trục Y (Chạm gạch hoặc mặt trên Paddle)
    public void reverseY() {
        angleDegrees = -angleDegrees;
        fixAngle();
    }

    public void setDirection(double angle) {
        this.angleDegrees = angle;
        fixAngle();
    }

    private void fixAngle() {
        while (angleDegrees < 0) angleDegrees += 360;
        while (angleDegrees >= 360) angleDegrees -= 360;
        
        // Tránh các góc chết khiến bóng bay ngang hoặc dọc mãi mãi
        // Ví dụ: góc 0, 180 (bay ngang) hoặc 90, 270 (bay dọc)
        double margin = 5.0; // Khoảng cách an toàn tối thiểu
        if (Math.abs(angleDegrees - 0) < margin) angleDegrees = margin;
        if (Math.abs(angleDegrees - 180) < margin) angleDegrees = 180 - margin;
        if (Math.abs(angleDegrees - 90) < margin) angleDegrees = 90 + margin;
        if (Math.abs(angleDegrees - 270) < margin) angleDegrees = 270 - margin;
    }

    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, size, size);
    }

    public int getX() { return (int)x; }
    public int getY() { return (int)y; }
    public int getSize() { return size; }
    public double getSpeed() { return speed; }
    public double getXDir() { return Math.cos(Math.toRadians(angleDegrees)); }
    public double getYDir() { return -Math.sin(Math.toRadians(angleDegrees)); }
    
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
