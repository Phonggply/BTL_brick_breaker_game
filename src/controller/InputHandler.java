package controller;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class InputHandler implements KeyListener {

    private volatile boolean leftPressed;
    private volatile boolean rightPressed;
    private volatile boolean escapePressed;
    private volatile boolean onePressed;
    private volatile boolean twoPressed;
    private volatile boolean threePressed;

    public boolean isLeftPressed() { return leftPressed; }
    public boolean isRightPressed() { return rightPressed; }
    public boolean isEscapePressed() { return escapePressed; }
    public boolean isOnePressed() { return onePressed; }
    public boolean isTwoPressed() { return twoPressed; }
    public boolean isThreePressed() { return threePressed; }

    public void setEscapePressed(boolean pressed) { this.escapePressed = pressed; }
    public void setOnePressed(boolean pressed) { this.onePressed = pressed; }
    public void setTwoPressed(boolean pressed) { this.twoPressed = pressed; }
    public void setThreePressed(boolean pressed) { this.threePressed = pressed; }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) leftPressed = true;
        if (key == KeyEvent.VK_RIGHT) rightPressed = true;
        if (key == KeyEvent.VK_ESCAPE) escapePressed = true;
        if (key == KeyEvent.VK_1) onePressed = true;
        if (key == KeyEvent.VK_2) twoPressed = true;
        if (key == KeyEvent.VK_3) threePressed = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) leftPressed = false;
        if (key == KeyEvent.VK_RIGHT) rightPressed = false;
        if (key == KeyEvent.VK_ESCAPE) escapePressed = false;
        if (key == KeyEvent.VK_1) onePressed = false;
        if (key == KeyEvent.VK_2) twoPressed = false;
        if (key == KeyEvent.VK_3) threePressed = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}
