package BTL_brick_breaker_game.src.view;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.Image;
import BTL_brick_breaker_game.src.model.Ball;
import BTL_brick_breaker_game.src.model.Paddle;
import BTL_brick_breaker_game.src.model.Brick;
import BTL_brick_breaker_game.src.model.Level;

import BTL_brick_breaker_game.src.controller.GameController;

public class GamePanel extends JPanel implements Runnable, KeyListener {

    private GameController gameController;

    private Thread gameThread;
    private boolean running;

    public GamePanel() {

        setBackground(Color.BLACK);
        setFocusable(true);

        gameController = new GameController();
        addKeyListener(this);        
        requestFocusInWindow();  
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
        Image bg = new ImageIcon(
            getClass().getClassLoader().getResource("assets/game_play_background.png")
        ).getImage();
        g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
        int width = getWidth();
        int height = getHeight();

        draw(g2, width, height);

        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if(key == KeyEvent.VK_LEFT){
            gameController.getPaddle().moveLeft();
        }
        if(key == KeyEvent.VK_RIGHT){
            gameController.getPaddle().moveRight(getWidth());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    private void draw(Graphics2D g2, int width, int height){


        Ball ball = gameController.getBall();
        if(ball != null){
            g2.setColor(Color.WHITE);
            g2.fillOval(
                ball.getX(),
                ball.getY(),
                ball.getSize(),
                ball.getSize()
            );
        }
        Paddle paddle = gameController.getPaddle();
        if(paddle != null){
            g2.setColor(Color.BLUE);
            g2.fillRect(
                paddle.getX(),
                paddle.getY(),
                paddle.getWidth(),
                paddle.getHeight()
            );
        }
        Level level = gameController.getLevel();
        if(level != null){
            g2.setColor(Color.RED);

            for (Brick[] row : level.getBricks()) {
                for (Brick brick : row) {
                    if (brick != null && !brick.isDestroyed()) {
                        g2.fillRect(
                            brick.getX(),
                            brick.getY(),
                            brick.getWidth(),
                            brick.getHeight()
                        );
                    }
                }
            }
        }
    }
}