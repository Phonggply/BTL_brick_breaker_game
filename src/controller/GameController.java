package controller;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.awt.Rectangle;

import model.Ball;
import model.GameState;
import model.Paddle;
import model.Brick;
import model.Level;
import model.LevelManager;
import model.PowerUp;
import dao.PlayerDAO;

public class GameController {
    private List<Ball> balls = new CopyOnWriteArrayList<>();
    private List<PowerUp> fallingPowerUps = new CopyOnWriteArrayList<>();
    private Paddle paddle;
    private Level level;
    private LevelManager levelManager;
    private InputHandler inputHandler;
    private PlayerDAO playerDAO;
    private dao.ScoreDAO scoreDAO;
    
    private volatile int score = 0;
    private int consecutiveHits = 0;
    private int currentPlayerId = 1; 
    private volatile int lives = 3; 
    private volatile GameState gameState;
    private boolean initialized = false;
    private int lastWidth = 0, lastHeight = 0;

    private int bonusLives = 0;
    private int bonusWidth = 0;

    public GameController(InputHandler inputHandler) {
        this(inputHandler, 0, 1);
    }

    public GameController(InputHandler inputHandler, int startLevel, int playerId) {
        this.inputHandler = inputHandler;
        this.currentPlayerId = playerId;
        this.playerDAO = new PlayerDAO();
        this.scoreDAO = new dao.ScoreDAO();
        levelManager = new LevelManager();
        levelManager.setCurrentLevel(startLevel);
        level = levelManager.getCurrentLevel();
        if (level == null) {
            level = new Level(new int[][]{{1, 1, 1, 1, 1}});
        }
        gameState = GameState.PLAYING;
        applyInventoryUpgrades();
    }

    private void applyInventoryUpgrades() {
        dao.InventoryDAO inventoryDAO = new dao.InventoryDAO();
        List<dao.entities.ShopItem> ownedItems = inventoryDAO.getOwnedItems(currentPlayerId);
        for (dao.entities.ShopItem item : ownedItems) {
            if ("Life".equals(item.getEffectType())) bonusLives += item.getEffectValue();
            else if ("Width".equals(item.getEffectType())) bonusWidth += item.getEffectValue();
        }
        this.lives += bonusLives;
    }

    private void resetBallAndPaddle(int currentWidth, int currentHeight) {
        balls.clear();
        fallingPowerUps.clear();
        int paddleW = (currentWidth / 8) + bonusWidth;
        int paddleH = 15;
        int paddleY = (int)(currentHeight * 0.8);
        Ball initialBall = new Ball(currentWidth/2, paddleY - 20, 12, 5.0);
        initialBall.setDirection(90); 
        balls.add(initialBall);
        paddle = new Paddle(currentWidth/2 - paddleW/2, paddleY, paddleW, paddleH, 12);
        consecutiveHits = 0;
        lastWidth = currentWidth;
        lastHeight = currentHeight;
    }

    public void update(int screenWidth, int screenHeight) {
        if (inputHandler.isEscapePressed()) {
            inputHandler.setEscapePressed(false);
            if (gameState == GameState.PLAYING) gameState = GameState.PAUSED;
            else if (gameState == GameState.PAUSED) gameState = GameState.PLAYING;
        }
        if (gameState != GameState.PLAYING || screenWidth <= 0 || screenHeight <= 0) return;
        if (!initialized) {
            level.initBricks(screenWidth);
            resetBallAndPaddle(screenWidth, screenHeight);
            initialized = true;
        }
        if (screenWidth != lastWidth || screenHeight != lastHeight) handleResize(screenWidth, screenHeight);
        handleInput(screenWidth);

        int subSteps = 5; // Tăng bước nhỏ để va chạm chính xác hơn
        for (int step = 0; step < subSteps; step++) {
            for (Ball b : balls) {
                b.move(1.0 / subSteps);
                checkWallCollision(b, screenWidth, screenHeight);
                checkBrickCollision(b);
                checkPaddleCollision(b);
            }
        }

        for (int i = 0; i < fallingPowerUps.size(); i++) {
            PowerUp p = fallingPowerUps.get(i);
            p.move();
            if (p.getY() > screenHeight) fallingPowerUps.remove(i--);
            else if (p.getBounds().intersects(paddle.getBounds())) {
                applyPowerUp(p, screenWidth);
                fallingPowerUps.remove(i--);
            }
        }
        checkBallOut(screenWidth, screenHeight);
        checkLevelComplete(screenWidth, screenHeight);
    }

    private void checkWallCollision(Ball b, int screenWidth, int screenHeight) {
        if (b.getX() <= 0) { 
            b.reverseX(); b.setPosition(1, b.getY()); 
        } else if (b.getX() + b.getSize() >= screenWidth) { 
            b.reverseX(); b.setPosition(screenWidth - b.getSize() - 1, b.getY()); 
        }
        if (b.getY() <= 0) { 
            b.reverseY(); b.setPosition(b.getX(), 1); 
        }
    }

    private void checkBrickCollision(Ball b) {
        Rectangle ballBounds = b.getBounds();
        for (Brick[] row : level.getBricks()) {
            if (row == null) continue;
            for (Brick brick : row) {
                if (brick != null && !brick.isDestroyed() && ballBounds.intersects(brick.getBounds())) {
                    handleCollisionDirection(b, brick);
                    
                    if (brick.getType() != Brick.BrickType.UNBREAKABLE) {
                        consecutiveHits++;
                        int multiplier = (consecutiveHits >= 3) ? 2 : 1;
                        
                        // Nếu gạch chưa vỡ hẳn (Level 3->2 hoặc 2->1) thì được 5 điểm
                        // Nếu gạch vỡ hẳn (Level 1->0) thì được 10 điểm
                        int hitPoints = (brick.getHealth() > 1) ? 5 : 10;
                        
                        score += (hitPoints * multiplier);
                        
                        brick.hit(); // Giảm máu viên gạch
                        
                        if (brick.isDestroyed()) {
                            // level.brickDestroyed() đã xử lý trong brick.hit() hoặc controller
                            level.brickDestroyed();
                            spawnPowerUp(brick.getX() + brick.getWidth()/2, brick.getY());
                        }
                    }
                    return; 
                }
            }
        }
    }

    private void checkPaddleCollision(Ball b) {
        if (b.getBounds().intersects(paddle.getBounds())) {
            Rectangle ballBounds = b.getBounds();
            Rectangle paddleBounds = paddle.getBounds();
            
            // Đẩy bóng ra khỏi Paddle ngay lập tức để tránh dính bóng
            b.setPosition(b.getX(), paddle.getY() - b.getSize() - 1);
            
            consecutiveHits = 0;
            int hitPos = (int)(ballBounds.getCenterX() - paddleBounds.x);
            int segmentW = paddleBounds.width / 5;
            
            // Ép góc nảy dựa trên vị trí chạm thanh đỡ
            if (hitPos < segmentW) b.setDirection(150);
            else if (hitPos < segmentW * 2) b.setDirection(120);
            else if (hitPos < segmentW * 3) b.setDirection(90);
            else if (hitPos < segmentW * 4) b.setDirection(60);
            else b.setDirection(30);
        }
    }

    private void handleCollisionDirection(Ball b, Brick brick) {
        Rectangle ballBounds = b.getBounds(), brickBounds = brick.getBounds();
        Rectangle intersection = ballBounds.intersection(brickBounds);
        
        // Nếu diện tích va chạm theo chiều rộng nhỏ hơn chiều cao -> Va chạm từ bên cạnh
        if (intersection.width < intersection.height) {
            b.reverseX();
            // Đẩy bóng ra ngoài viên gạch để tránh bị "nhốt"
            if (ballBounds.getCenterX() < brickBounds.getCenterX()) 
                b.setPosition(brickBounds.x - b.getSize() - 1, b.getY());
            else 
                b.setPosition(brickBounds.x + brickBounds.width + 1, b.getY());
        } else {
            // Va chạm từ trên hoặc dưới
            b.reverseY();
            if (ballBounds.getCenterY() < brickBounds.getCenterY()) 
                b.setPosition(b.getX(), brickBounds.y - b.getSize() - 1);
            else 
                b.setPosition(b.getX(), brickBounds.y + brickBounds.height + 1);
        }
    }

    public void restartLevel(int screenWidth) {
        score = 0; lives = 3;
        level.initBricks(screenWidth);
        resetBallAndPaddle(screenWidth, (int)(paddle.getY() / 0.8));
        gameState = GameState.PLAYING;
    }

    public void resume() { if (gameState == GameState.PAUSED) gameState = GameState.PLAYING; }
    public int[] getPlayerBalance() { return playerDAO.checkBalance(currentPlayerId); }
    private void handleInput(int screenWidth) {
        if (inputHandler.isLeftPressed()) paddle.moveLeft();
        if (inputHandler.isRightPressed()) paddle.moveRight(screenWidth);
    }

    private void spawnPowerUp(int x, int y) {
        if (Math.random() < 0.08) {
            double rand = Math.random();
            PowerUp.Type type = (rand < 0.33) ? PowerUp.Type.MULTIBALL : (rand < 0.66) ? PowerUp.Type.EXPAND : PowerUp.Type.SHIELD;
            fallingPowerUps.add(new PowerUp(x, y, type));
        }
    }

    private void applyPowerUp(PowerUp p, int screenWidth) {
        switch (p.getType()) {
            case MULTIBALL:
                if (!balls.isEmpty()) {
                    Ball b = balls.get(0);
                    Ball b1 = new Ball(b.getX(), b.getY(), b.getSize(), 5.0); b1.setDirection(120); balls.add(b1);
                    Ball b2 = new Ball(b.getX(), b.getY(), b.getSize(), 5.0); b2.setDirection(60); balls.add(b2);
                }
                break;
            case EXPAND: paddle.expand(screenWidth); break;
            case SHIELD: paddle.activateShield(20); break;
        }
    }

    private void checkBallOut(int screenWidth, int screenHeight) {
        for (int i = 0; i < balls.size(); i++) {
            Ball b = balls.get(i);
            if (b.getY() + b.getSize() >= screenHeight - 15) { // Kiểm tra vùng gần Shield
                if (paddle.isShieldActive()) {
                    b.reverseY();
                    // Đẩy bóng lên trên vùng an toàn của Shield
                    b.setPosition(b.getX(), screenHeight - b.getSize() - 25);
                } else if (b.getY() >= screenHeight) {
                    balls.remove(i--);
                }
            }
        }
        if (balls.isEmpty()) {
            lives--;
            if (lives > 0) {
                Ball newBall = new Ball(paddle.getX() + paddle.getWidth()/2, paddle.getY() - 20, 12, 5.0);
                newBall.setDirection(90);
                balls.add(newBall);
            } else {
                gameState = GameState.GAME_OVER;
                endSession(); // Lưu điểm và cộng xu khi hết mạng
            }
        }
    }

    public void endSession() {
        if (score > 0 || lives < 3) {
            // Lưu điểm và cộng xu (score / 15) trong ScoreDAO
            scoreDAO.gameOver(currentPlayerId, score);
            
            // Tặng thưởng chuyên cần cố định 50 xu mỗi ván
            playerDAO.addCoins(currentPlayerId, 50);
            
            score = 0; // Reset điểm
        }
    }

    private void checkLevelComplete(int screenWidth, int screenHeight) {
        if (level != null && level.isLevelComplete()) {
            // 1. Lưu điểm và cộng xu thưởng theo điểm
            endSession();
            
            // 2. Thưởng thêm 200 xu vì đã thắng màn (Phần thưởng vượt ải)
            playerDAO.addCoins(currentPlayerId, 200);
            
            gameState = GameState.PAUSED; 
        }
    }

    public void nextLevel(int screenWidth, int screenHeight) {
        if (levelManager.hasNextLevel()) {
            levelManager.nextLevel();
            playerDAO.updateHighestLevel(currentPlayerId, levelManager.getCurrentLevelIndex() + 1);
            level = levelManager.getCurrentLevel();
            if (level != null) level.initBricks(screenWidth);
            resetBallAndPaddle(screenWidth, screenHeight);
            gameState = GameState.PLAYING;
        } else {
            gameState = GameState.GAME_OVER;
        }
    }

    public List<Ball> getBalls() { return balls; }
    public List<PowerUp> getFallingPowerUps() { return fallingPowerUps; }
    public Paddle getPaddle() { return paddle; }
    public Level getLevel() { return level; }
    public GameState getGameState() { return gameState; }
    public int getScore() { return score; }
    public int getLives() { return lives; }

    private void handleResize(int screenWidth, int screenHeight) {
        if (lastWidth <= 0 || lastHeight <= 0) {
            lastWidth = screenWidth;
            lastHeight = screenHeight;
            return;
        }

        double scaleX = (double) screenWidth / lastWidth;
        double scaleY = (double) screenHeight / lastHeight;

        if (level != null) level.repositionBricks(screenWidth, false);
        
        if (paddle != null) {
            // Cập nhật vị trí X của paddle theo tỉ lệ
            int newX = (int) (paddle.getX() * scaleX);
            int paddleW = screenWidth / 8 + bonusWidth;
            int paddleH = 15;
            int paddleY = (int)(screenHeight * 0.8);
            
            paddle.setX(newX);
            paddle.setSize(paddleW, paddleH);
            paddle.setY(paddleY);
            
            // Đảm bảo paddle không vượt quá biên màn hình
            if (paddle.getX() < 0) paddle.setX(0);
            if (paddle.getX() + paddle.getWidth() > screenWidth) paddle.setX(screenWidth - paddle.getWidth());
        }

        // Cập nhật vị trí tất cả các quả bóng theo tỉ lệ
        for (Ball b : balls) {
            double newX = b.getX() * scaleX;
            double newY = b.getY() * scaleY;
            b.setPosition(newX, newY);
        }

        // Cập nhật vị trí các PowerUp đang rơi theo tỉ lệ
        for (PowerUp p : fallingPowerUps) {
            int newX = (int) (p.getX() * scaleX);
            int newY = (int) (p.getY() * scaleY);
            p.setPosition(newX, newY);
        }

        lastWidth = screenWidth;
        lastHeight = screenHeight;
    }
}
