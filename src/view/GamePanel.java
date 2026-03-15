package BTL_brick_breaker_game.src.view;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;

import BTL_brick_breaker_game.src.controller.GameController;

public class GamePanel extends JPanel implements Runnable {

    private GameController gameController;

    private Thread gameThread;
    private boolean running;

    public GamePanel() {

        setBackground(Color.BLACK);
        setFocusable(true);

        gameController = new GameController();

        startGame();
    }

    private void startGame() {

        running = true;
        gameThread = new Thread(this);
        gameThread.start();

    }

    @Override
    public void run() {

        while(running){

            gameController.update();

            repaint();

            try{
                Thread.sleep(16); // ~60 FPS
            }catch(Exception e){
                e.printStackTrace();
            }

        }

    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        int width = getWidth();
        int height = getHeight();

        draw(g2, width, height);

    }

    private void draw(Graphics2D g2, int width, int height){

        // sau này vẽ object ở đây
        // ví dụ ball, paddle, brick...
        int ballSize = (int)(width * 0.02);
        int paddleWidth = (int)(width * 0.15);
        int paddleHeight = (int)(height * 0.03);
        int powerUpSize = (int)(width * 0.03);
        // vẽ ball
        g2.setColor(Color.WHITE);
        g2.fillOval(gameController.getBall().getX(), gameController.getBall().getY(), ballSize, ballSize);
        // vẽ paddle
        g2.setColor(Color.BLUE);
        g2.fillRect(gameController.getPaddle().getX(), gameController.getPaddle().getY(), paddleWidth, paddleHeight);
    }
}