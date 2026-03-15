package BTL_java.src.model;

 public class Ball {   
    private int x;
    private int y;
    private int size;
    private int xDir;
    private int yDir;
    private int speed;

    public Ball(int x, int y, int size, int speed) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.speed = speed;
        this.xDir = 1;
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
        yDir = -yDir;
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
