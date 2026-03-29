package view;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import model.Ball;
import model.Paddle;
import model.Brick;
import model.Level;
import model.PowerUp;
import controller.GameController;
import controller.InputHandler;

public class GamePanel extends JPanel implements Runnable {

    private GameController gameController;
    private InputHandler inputHandler;
    private Thread gameThread;
    private boolean running;
    
    private Image bgImage;
    private Map<Brick.BrickType, Image> brickImages;
    private Map<PowerUp.Type, Image> powerUpImages;
    private Font gameFont;

    public GamePanel() {
        this(0);
    }

    public GamePanel(int startLevel) {
        setBackground(Color.BLACK);
        setFocusable(true);
        loadResources();
        inputHandler = new InputHandler();
        gameController = new GameController(inputHandler, startLevel);
        addKeyListener(inputHandler);        
        
        // Chỉ bắt đầu game khi panel đã được hiển thị
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & java.awt.event.HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                if (gameThread == null) {
                    startGame();
                }
                requestFocusInWindow();
            }
        });
    }

    private void loadResources() {
        try {
            File bgFile = new File("assets/game_play_background.png");
            if (bgFile.exists()) {
                bgImage = ImageIO.read(bgFile);
            } else {
                System.err.println("Cảnh báo: Không tìm thấy assets/game_play_background.png");
            }

            brickImages = new HashMap<>();
            String[] brickFiles = {"brick_normal.png", "brick_strong_2.png", "brick_strong_3.png", "brick_unbreakable.png"};
            Brick.BrickType[] types = {Brick.BrickType.NORMAL, Brick.BrickType.STRONG_2, Brick.BrickType.STRONG_3, Brick.BrickType.UNBREAKABLE};
            
            for (int i = 0; i < brickFiles.length; i++) {
                File f = new File("assets/" + brickFiles[i]);
                if (f.exists()) {
                    brickImages.put(types[i], ImageIO.read(f));
                } else {
                    System.err.println("Cảnh báo: Không tìm thấy assets/" + brickFiles[i]);
                }
            }

            powerUpImages = new HashMap<>();
            String[] powerUpFiles = {"multiball_powerup.png", "powerup_expand.png", "powerup_shield.png"};
            PowerUp.Type[] pTypes = {PowerUp.Type.MULTIBALL, PowerUp.Type.EXPAND, PowerUp.Type.SHIELD};
            
            for (int i = 0; i < powerUpFiles.length; i++) {
                File f = new File("assets/" + powerUpFiles[i]);
                if (f.exists()) {
                    powerUpImages.put(pTypes[i], ImageIO.read(f));
                } else {
                    System.err.println("Cảnh báo: Không tìm thấy assets/" + powerUpFiles[i]);
                }
            }

            // Load Font
            File fontFile = new File("assets/PressStart2P-Regular.ttf");
            if (fontFile.exists()) {
                gameFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(12f);
            } else {
                gameFont = new Font("Arial", Font.BOLD, 14);
            }
        } catch (Exception e) {
            System.err.println("Lỗi nghiêm trọng khi tải tài nguyên: " + e.getMessage());
            e.printStackTrace();
            gameFont = new Font("Arial", Font.BOLD, 14);
        }
    }

    private void startGame() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void stopGame() {
        running = false;
        if (gameThread != null) {
            try {
                gameThread.join(500); // Đợi thread dừng trong tối đa 500ms
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        while(running){
            // Nếu đang pause, kiểm tra phím 1, 2, 3
            if (gameController.getGameState() == model.GameState.PAUSED) {
                if (inputHandler.isOnePressed()) {
                    inputHandler.setOnePressed(false);
                    handlePauseInput(KeyEvent.VK_1);
                } else if (inputHandler.isTwoPressed()) {
                    inputHandler.setTwoPressed(false);
                    handlePauseInput(KeyEvent.VK_2);
                } else if (inputHandler.isThreePressed()) {
                    inputHandler.setThreePressed(false);
                    handlePauseInput(KeyEvent.VK_3);
                }
            }

            gameController.update(getWidth(), getHeight());
            repaint();
            try{
                Thread.sleep(16);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        }
        draw(g2);
    }

    private void draw(Graphics2D g2) {
        int w = getWidth();
        int h = getHeight();

        // Vẽ UI (Score & Lives)
        g2.setFont(gameFont);
        g2.setColor(Color.WHITE);
        g2.drawString("SCORE: " + gameController.getScore(), 20, 30);
        g2.setColor(Color.RED);
        g2.drawString("LIVES: " + gameController.getLives(), 20, 60);

        // Vẽ Shield
        Paddle paddle = gameController.getPaddle();
        if (paddle != null && paddle.isShieldActive()) {
            g2.setColor(new Color(0, 255, 255, 120));
            g2.fillRect(0, h - 15, w, 15);
        }

        // Vẽ Power-ups
        for (PowerUp p : gameController.getFallingPowerUps()) {
            Image img = powerUpImages.get(p.getType());
            if (img != null) {
                g2.drawImage(img, p.getX(), p.getY(), p.getWidth(), p.getHeight(), this);
            }
        }

        // Vẽ Balls
        java.util.List<Ball> balls = gameController.getBalls();
        g2.setColor(Color.WHITE);
        for (Ball ball : balls) {
            if (ball != null) {
                g2.fillOval(ball.getX(), ball.getY(), ball.getSize(), ball.getSize());
            }
        }

        // Vẽ Paddle
        if(paddle != null){
            g2.setColor(Color.BLUE);
            g2.fillRect(paddle.getX(), paddle.getY(), paddle.getWidth(), paddle.getHeight());
        }

        // Vẽ Gạch
        Level level = gameController.getLevel();
        if(level != null){
            for (Brick[] row : level.getBricks()) {
                if (row == null) continue;
                for (Brick brick : row) {
                    if (brick != null && !brick.isDestroyed()) {
                        Image img = getBrickImage(brick);
                        if (img != null) {
                            g2.drawImage(img, brick.getX(), brick.getY(), brick.getWidth(), brick.getHeight(), this);
                        }
                    }
                }
            }
        }

        // ===== DRAW PAUSE MENU =====
        if (gameController.getGameState() == model.GameState.PAUSED) {
            drawPauseMenu(g2, w, h);
        }

        // Vẽ Game Over
        if (gameController.getGameState() == model.GameState.GAME_OVER) {
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, 0, w, h);
            g2.setColor(Color.RED);
            g2.setFont(gameFont.deriveFont(30f));
            g2.drawString("GAME OVER", w/2 - 150, h/2);
            g2.setFont(gameFont.deriveFont(15f));
            g2.setColor(Color.WHITE);
            g2.drawString("FINAL SCORE: " + gameController.getScore(), w/2 - 120, h/2 + 50);
        }
    }

    private void drawPauseMenu(Graphics2D g2, int w, int h) {
        // Overlay mờ toàn màn hình
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, w, h);

        // Cửa sổ nhỏ ở giữa
        int winW = 400, winH = 350;
        int winX = (w - winW) / 2;
        int winY = (h - winH) / 2;

        g2.setColor(new Color(50, 50, 50));
        g2.fillRoundRect(winX, winY, winW, winH, 20, 20);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(winX, winY, winW, winH, 20, 20);

        // Tiêu đề PAUSED
        g2.setFont(gameFont.deriveFont(24f));
        g2.drawString("PAUSED", winX + 130, winY + 50);

        // Thông tin điểm & tiền
        g2.setFont(gameFont.deriveFont(14f));
        g2.drawString("SCORE: " + gameController.getScore(), winX + 50, winY + 100);
        int[] balance = gameController.getPlayerBalance();
        g2.setColor(Color.YELLOW);
        g2.drawString("COINS: " + balance[0], winX + 50, winY + 130);
        
        // Hướng dẫn nút bấm (Tạm thời dùng MouseListener hoặc vẽ nút giả)
        g2.setColor(Color.WHITE);
        drawButton(g2, "1. RESUME", winX + 50, winY + 180);
        drawButton(g2, "2. RESTART", winX + 50, winY + 230);
        drawButton(g2, "3. MENU", winX + 50, winY + 280);
        
        g2.setFont(gameFont.deriveFont(10f));
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawString("Press 1, 2, or 3", winX + 130, winY + 330);
    }

    private void drawButton(Graphics2D g2, String text, int x, int y) {
        g2.setColor(new Color(100, 100, 100));
        g2.fillRect(x, y - 25, 300, 40);
        g2.setColor(Color.WHITE);
        g2.drawRect(x, y - 25, 300, 40);
        g2.drawString(text, x + 20, y);
    }

    public void handlePauseInput(int keyCode) {
        if (gameController.getGameState() != model.GameState.PAUSED) return;
        
        if (keyCode == KeyEvent.VK_1) {
            gameController.resume();
        } else if (keyCode == KeyEvent.VK_2) {
            gameController.restartLevel(getWidth());
        } else if (keyCode == KeyEvent.VK_3) {
            stopGame();
            javax.swing.SwingUtilities.invokeLater(() -> {
                Container parent = getParent();
                while (parent != null && !(parent instanceof GameFrame)) {
                    parent = parent.getParent();
                }
                if (parent instanceof GameFrame) {
                    ((GameFrame) parent).showMenu();
                }
            });
        }
    }

    private Image getBrickImage(Brick brick) {
        if (brick.getType() == Brick.BrickType.UNBREAKABLE) return brickImages.get(Brick.BrickType.UNBREAKABLE);
        switch (brick.getHealth()) {
            case 3: return brickImages.get(Brick.BrickType.STRONG_3);
            case 2: return brickImages.get(Brick.BrickType.STRONG_2);
            default: return brickImages.get(Brick.BrickType.NORMAL);
        }
    }
}
