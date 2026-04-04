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
    private Image paddleImage;
    private Map<Brick.BrickType, Image> brickImages;
    private Map<PowerUp.Type, Image> powerUpImages;
    private Font gameFont;

    public GamePanel() {
        this(0, 1);
    }

    public GamePanel(int startLevel, int playerId) {
        setBackground(Color.BLACK);
        setFocusable(true);
        loadResources();
        inputHandler = new InputHandler();
        gameController = new GameController(inputHandler, startLevel, playerId);
        addKeyListener(inputHandler);        
        
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & java.awt.event.HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                if (gameThread == null) startGame();
                requestFocusInWindow();
            }
        });
    }

    private void loadResources() {
        try {
            // Load Background
            bgImage = loadImage("assets/game_play_background.png");
            paddleImage = loadImage("assets/paddle.png");

            brickImages = new HashMap<>();
            String[] brickFiles = {"brick_normal.png", "brick_strong_2.png", "brick_strong_3.png", "brick_unbreakable.png"};
            Brick.BrickType[] types = {Brick.BrickType.NORMAL, Brick.BrickType.STRONG_2, Brick.BrickType.STRONG_3, Brick.BrickType.UNBREAKABLE};
            for (int i = 0; i < brickFiles.length; i++) {
                brickImages.put(types[i], loadImage("assets/" + brickFiles[i]));
            }

            powerUpImages = new HashMap<>();
            String[] powerUpFiles = {"multiball_powerup.png", "powerup_expand.png", "powerup_shield.png"};
            PowerUp.Type[] pTypes = {PowerUp.Type.MULTIBALL, PowerUp.Type.EXPAND, PowerUp.Type.SHIELD};
            for (int i = 0; i < powerUpFiles.length; i++) {
                powerUpImages.put(pTypes[i], loadImage("assets/" + powerUpFiles[i]));
            }

            // Load Font
            File fontFile = new File("assets/PressStart2P-Regular.ttf");
            if (fontFile.exists()) {
                gameFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(12f);
            } else {
                gameFont = new Font("Arial", Font.BOLD, 14);
                System.err.println("Cảnh báo: Không tìm thấy font tại " + fontFile.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
            gameFont = new Font("Arial", Font.BOLD, 14);
        }
    }

    private Image loadImage(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                return ImageIO.read(file);
            }
            System.err.println("Lỗi: Không tìm thấy ảnh tại " + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void startGame() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void stopGame() {
        running = false;
        if (gameThread != null) {
            try { gameThread.join(500); } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double nsPerTick = 1000000000.0 / 60.0;
        double delta = 0;

        while(running){
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerTick;
            lastTime = now;

            while (delta >= 1) {
                update();
                delta--;
            }
            
            repaint();
            
            try { Thread.sleep(2); } catch(Exception e) { e.printStackTrace(); }
        }
    }

    private void update() {
        model.GameState state = gameController.getGameState();
        Level currentLevel = gameController.getLevel();
        boolean isWin = currentLevel != null && currentLevel.isLevelComplete();

        if (state == model.GameState.PAUSED || state == model.GameState.GAME_OVER) {
            if (inputHandler.isOnePressed()) {
                inputHandler.setOnePressed(false);
                if (isWin) gameController.nextLevel(getWidth(), getHeight());
                else handleStateInput(KeyEvent.VK_1, state);
            } else if (inputHandler.isTwoPressed()) {
                inputHandler.setTwoPressed(false);
                handleStateInput(KeyEvent.VK_2, state);
            } else if (inputHandler.isThreePressed()) {
                inputHandler.setThreePressed(false);
                handleStateInput(KeyEvent.VK_3, state);
            }
        }

        gameController.update(getWidth(), getHeight());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        if (bgImage != null) g2.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        draw(g2);
    }

    private void draw(Graphics2D g2) {
        int w = getWidth(), h = getHeight();
        g2.setFont(gameFont);
        g2.setColor(Color.WHITE);
        g2.drawString("SCORE: " + gameController.getScore(), 20, 30);
        g2.setColor(Color.RED);
        g2.drawString("LIVES: " + gameController.getLives(), 20, 60);

        Paddle paddle = gameController.getPaddle();
        if (paddle != null && paddle.isShieldActive()) {
            g2.setColor(new Color(0, 255, 255, 120));
            g2.fillRect(0, h - 15, w, 15);
        }

        for (PowerUp p : gameController.getFallingPowerUps()) {
            Image img = powerUpImages.get(p.getType());
            if (img != null) g2.drawImage(img, p.getX(), p.getY(), p.getWidth(), p.getHeight(), this);
        }

        for (Ball ball : gameController.getBalls()) {
            g2.setColor(Color.WHITE);
            g2.fillOval(ball.getX(), ball.getY(), ball.getSize(), ball.getSize());
        }

        if(paddle != null){
            if (paddleImage != null) g2.drawImage(paddleImage, paddle.getX(), paddle.getY(), paddle.getWidth(), paddle.getHeight(), this);
            else { g2.setColor(Color.BLUE); g2.fillRect(paddle.getX(), paddle.getY(), paddle.getWidth(), paddle.getHeight()); }
        }

        Level level = gameController.getLevel();
        if(level != null){
            for (Brick[] row : level.getBricks()) {
                if (row == null) continue;
                for (Brick brick : row) {
                    if (brick != null && !brick.isDestroyed()) {
                        Image img = getBrickImage(brick);
                        if (img != null) g2.drawImage(img, brick.getX(), brick.getY(), brick.getWidth(), brick.getHeight(), this);
                    }
                }
            }
        }

        if (level != null && level.isLevelComplete()) drawOverlayMenu(g2, w, h, "LEVEL COMPLETED");
        else if (gameController.getGameState() == model.GameState.PAUSED) drawOverlayMenu(g2, w, h, "PAUSED");
        else if (gameController.getGameState() == model.GameState.GAME_OVER) drawOverlayMenu(g2, w, h, "GAME OVER");
    }

    private void drawOverlayMenu(Graphics2D g2, int w, int h, String title) {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, w, h);

        int winW = 400, winH = 380;
        int winX = (w - winW) / 2, winY = (h - winH) / 2;

        g2.setColor(new Color(40, 40, 40));
        g2.fillRoundRect(winX, winY, winW, winH, 20, 20);
        
        Color themeColor = Color.WHITE;
        if (title.equals("GAME OVER")) themeColor = Color.RED;
        else if (title.equals("LEVEL COMPLETED")) themeColor = Color.GREEN;
        
        g2.setColor(themeColor);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(winX, winY, winW, winH, 20, 20);

        g2.setFont(gameFont.deriveFont(20f));
        g2.drawString(title, winX + (winW - g2.getFontMetrics().stringWidth(title))/2, winY + 60);

        g2.setFont(gameFont.deriveFont(14f));
        g2.setColor(Color.WHITE);
        g2.drawString("CURRENT SCORE: " + gameController.getScore(), winX + 50, winY + 120);
        
        int bonus = title.equals("LEVEL COMPLETED") ? 500 : 0;
        g2.setColor(Color.YELLOW);
        if (bonus > 0) g2.drawString("LEVEL BONUS: +" + bonus, winX + 50, winY + 150);
        else g2.drawString("COINS EARNED: +" + (gameController.getScore()/10), winX + 50, winY + 150);
        
        g2.setColor(Color.WHITE);
        if (title.equals("LEVEL COMPLETED")) {
            drawButton(g2, "1. NEXT LEVEL", winX + 50, winY + 200);
            drawButton(g2, "2. SHOP", winX + 50, winY + 250);
            drawButton(g2, "3. MENU", winX + 50, winY + 300);
        } else if (title.equals("PAUSED")) {
            drawButton(g2, "1. RESUME", winX + 50, winY + 200);
            drawButton(g2, "2. RESTART", winX + 50, winY + 250);
            drawButton(g2, "3. MENU", winX + 50, winY + 300);
        } else {
            drawButton(g2, "1. RESTART", winX + 50, winY + 200);
            drawButton(g2, "2. SHOP", winX + 50, winY + 250);
            drawButton(g2, "3. MENU", winX + 50, winY + 300);
        }
        
        g2.setFont(gameFont.deriveFont(10f));
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawString("Press 1, 2, or 3", winX + 130, winY + 355);
    }

    private void drawButton(Graphics2D g2, String text, int x, int y) {
        g2.setColor(new Color(80, 80, 80));
        g2.fillRect(x, y - 25, 300, 40);
        g2.setColor(Color.WHITE);
        g2.drawRect(x, y - 25, 300, 40);
        g2.drawString(text, x + 20, y);
    }

    private void handleStateInput(int keyCode, model.GameState state) {
        if (state == model.GameState.PAUSED) {
            if (keyCode == KeyEvent.VK_1) gameController.resume();
            else if (keyCode == KeyEvent.VK_2) gameController.restartLevel(getWidth());
            else if (keyCode == KeyEvent.VK_3) returnToMenu();
        } else if (state == model.GameState.GAME_OVER) {
            if (keyCode == KeyEvent.VK_1) gameController.restartLevel(getWidth());
            else if (keyCode == KeyEvent.VK_2) goToShop();
            else if (keyCode == KeyEvent.VK_3) returnToMenu();
        }
    }

    private void returnToMenu() {
        gameController.endSession(); // Chốt điểm và cộng xu trước khi thoát
        stopGame();
        javax.swing.SwingUtilities.invokeLater(() -> {
            GameFrame frame = getFrame();
            if (frame != null) frame.showMenu();
        });
    }

    private void goToShop() {
        gameController.endSession(); // Chốt điểm và cộng xu trước khi vào Shop
        stopGame();
        javax.swing.SwingUtilities.invokeLater(() -> {
            GameFrame frame = getFrame();
            if (frame != null) frame.showShop();
        });
    }

    private GameFrame getFrame() {
        Container parent = getParent();
        while (parent != null && !(parent instanceof GameFrame)) parent = parent.getParent();
        return (GameFrame) parent;
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
