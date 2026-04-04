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
    private dao.BallPropertiesDAO ballPropertiesDAO;
    private dao.PaddlePropertiesDAO paddlePropertiesDAO;
    
    private volatile int score = 0;
    private int consecutiveHits = 0;
    private int currentPlayerId = 1; 
    private volatile int lives = 3; 
    private volatile GameState gameState;
    private boolean initialized = false;
    private int lastWidth = 0, lastHeight = 0;

    private int bonusLives = 0;
    private int bonusWidth = 0;
    private double ballSpeedMultiplier = 1.0;
    private double ballSizeMultiplier = 1.0;
    private double paddleSpeedMultiplier = 1.0;
    private volatile boolean loading = true;
    private boolean hasMultiBallUpgrade = false;
    private int shieldDurationFromUpgrade = 0;

    public GameController(InputHandler inputHandler) {
        this(inputHandler, 0, 1);
    }

    public GameController(InputHandler inputHandler, int startLevel, int playerId) {
        this.inputHandler = inputHandler;
        this.currentPlayerId = playerId;
        this.playerDAO = new PlayerDAO();
        this.scoreDAO = new dao.ScoreDAO();
        this.ballPropertiesDAO = new dao.BallPropertiesDAO();
        this.paddlePropertiesDAO = new dao.PaddlePropertiesDAO();
        
        levelManager = new LevelManager();
        levelManager.setCurrentLevel(startLevel);
        level = levelManager.getCurrentLevel();
        if (level == null) {
            level = new Level(new int[][]{{1, 1, 1, 1, 1}});
        }
        gameState = GameState.PLAYING;
        
        new Thread(() -> {
            applyInventoryUpgrades();
            loadProperties();
            loading = false;
        }).start();
    }

    public boolean isLoading() { return loading; }

    private void applyInventoryUpgrades() {
        dao.InventoryDAO inventoryDAO = new dao.InventoryDAO();
        List<dao.entities.ShopItem> ownedItems = inventoryDAO.getOwnedItems(currentPlayerId);
        
        bonusWidth = 0;
        bonusLives = 0;
        hasMultiBallUpgrade = false;
        shieldDurationFromUpgrade = 0;

        for (dao.entities.ShopItem item : ownedItems) {
            String type = item.getEffectType();
            int value = item.getEffectValue();
            
            boolean consumed = false;
            if ("EXPAND".equals(type) || "Width".equals(type)) {
                bonusWidth += value;
                consumed = true;
            } else if ("Life".equals(type)) {
                bonusLives += value;
                consumed = true;
            } else if ("MULTIBALL".equals(type)) {
                hasMultiBallUpgrade = true;
                consumed = true;
            } else if ("SHIELD".equals(type)) {
                shieldDurationFromUpgrade = Math.max(shieldDurationFromUpgrade, value);
                consumed = true;
            }

            if (consumed) {
                inventoryDAO.consumeItem(currentPlayerId, item.getItemId());
            }
        }
        this.lives = 3 + bonusLives;
    }

    private void loadProperties() {
        dao.entities.BallProperties ballProps = ballPropertiesDAO.getBallProperties(currentPlayerId);
        if (ballProps != null) {
            this.ballSpeedMultiplier = ballProps.getBallSpeed() / 5.0;
            this.ballSizeMultiplier = ballProps.getBallSize();
        }
        
        dao.entities.PaddleProperties paddleProps = paddlePropertiesDAO.getPaddleProperties(currentPlayerId);
        if (paddleProps != null) {
            this.paddleSpeedMultiplier = paddleProps.getPaddleSpeed() / 12.0;
        }
    }

    private void resetBallAndPaddle(int currentWidth, int currentHeight) {
        balls.clear();
        fallingPowerUps.clear();
        
        int paddleW = (currentWidth / 8) + bonusWidth;
        int paddleH = Math.max(10, (int)(currentHeight * 0.025));
        int paddleY = (int)(currentHeight * 0.8);
        
        int baseBallSize = Math.max(8, (int)(currentWidth * 0.015));
        int ballSize = (int)(baseBallSize * ballSizeMultiplier);
        double ballSpeed = 5.0 * ballSpeedMultiplier;
        int paddleSpeed = (int)(12 * paddleSpeedMultiplier);
        
        Ball initialBall = new Ball(currentWidth/2, paddleY - 20, ballSize, ballSpeed);
        initialBall.setDirection(90); 
        balls.add(initialBall);

        if (hasMultiBallUpgrade) {
            Ball b1 = new Ball(initialBall.getX(), initialBall.getY(), ballSize, ballSpeed);
            b1.setDirection(120);
            balls.add(b1);
            Ball b2 = new Ball(initialBall.getX(), initialBall.getY(), ballSize, ballSpeed);
            b2.setDirection(60);
            balls.add(b2);
        }

        paddle = new Paddle(currentWidth/2 - paddleW/2, paddleY, paddleW, paddleH, paddleSpeed);
        
        if (shieldDurationFromUpgrade > 0) {
            paddle.activateShield(shieldDurationFromUpgrade);
        }

        consecutiveHits = 0;
        lastWidth = currentWidth;
        lastHeight = currentHeight;
    }

    public void update(int screenWidth, int screenHeight) {
        if (loading || inputHandler.isEscapePressed()) {
            inputHandler.setEscapePressed(false);
            if (!loading) {
                if (gameState == GameState.PLAYING) gameState = GameState.PAUSED;
                else if (gameState == GameState.PAUSED) gameState = GameState.PLAYING;
            }
            return;
        }
        if (gameState != GameState.PLAYING || screenWidth <= 0 || screenHeight <= 0) return;
        
        if (!initialized) {
            level.initBricks(screenWidth, screenHeight);
            resetBallAndPaddle(screenWidth, screenHeight);
            initialized = true;
        }
        
        if (screenWidth != lastWidth || screenHeight != lastHeight) {
            handleResize(screenWidth, screenHeight);
        }
        
        handleInput(screenWidth);

        int subSteps = 5; 
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
                        int hitPoints = (brick.getHealth() > 1) ? 5 : 10;
                        score += (hitPoints * multiplier);
                        brick.hit();
                        if (brick.isDestroyed()) {
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
            b.setPosition(b.getX(), paddle.getY() - b.getSize() - 1);
            consecutiveHits = 0;
            int hitPos = (int)(ballBounds.getCenterX() - paddleBounds.x);
            int segmentW = paddleBounds.width / 5;
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
        if (intersection.width < intersection.height) {
            b.reverseX();
            if (ballBounds.getCenterX() < brickBounds.getCenterX()) 
                b.setPosition(brickBounds.x - b.getSize() - 1, b.getY());
            else 
                b.setPosition(brickBounds.x + brickBounds.width + 1, b.getY());
        } else {
            b.reverseY();
            if (ballBounds.getCenterY() < brickBounds.getCenterY()) 
                b.setPosition(b.getX(), brickBounds.y - b.getSize() - 1);
            else 
                b.setPosition(b.getX(), brickBounds.y + brickBounds.height + 1);
        }
    }

    public void restartLevel(int screenWidth) {
        score = 0; lives = 3 + bonusLives;
        int currentHeight = (int)(paddle.getY() / 0.8);
        level.initBricks(screenWidth, currentHeight);
        resetBallAndPaddle(screenWidth, currentHeight);
        gameState = GameState.PLAYING;
    }

    public void endSession() {
        if (score >= 50) {
            scoreDAO.gameOver(currentPlayerId, score);
        }
        score = 0; 
    }

    private void checkBallOut(int screenWidth, int screenHeight) {
        for (int i = 0; i < balls.size(); i++) {
            Ball b = balls.get(i);
            if (b.getY() + b.getSize() >= screenHeight - 15) {
                if (paddle.isShieldActive()) {
                    b.reverseY();
                    b.setPosition(b.getX(), screenHeight - b.getSize() - 25);
                } else if (b.getY() >= screenHeight) {
                    balls.remove(i--);
                }
            }
        }
        if (balls.isEmpty()) {
            lives--;
            if (lives > 0) {
                int baseBallSize = Math.max(8, (int)(screenWidth * 0.015));
                int ballSize = (int)(baseBallSize * ballSizeMultiplier);
                double ballSpeed = 5.0 * ballSpeedMultiplier;
                Ball newBall = new Ball(paddle.getX() + paddle.getWidth()/2, paddle.getY() - 20, ballSize, ballSpeed);
                newBall.setDirection(90);
                balls.add(newBall);
            } else {
                gameState = GameState.GAME_OVER;
                endSession();
            }
        }
    }

    private void checkLevelComplete(int screenWidth, int screenHeight) {
        if (level != null && level.isLevelComplete()) {
            endSession();
            playerDAO.addCoins(currentPlayerId, 200);
            gameState = GameState.PAUSED; 
        }
    }

    public void nextLevel(int screenWidth, int screenHeight) {
        if (levelManager.hasNextLevel()) {
            levelManager.nextLevel();
            playerDAO.updateHighestLevel(currentPlayerId, levelManager.getCurrentLevelIndex() + 1);
            level = levelManager.getCurrentLevel();
            if (level != null) level.initBricks(screenWidth, screenHeight);
            resetBallAndPaddle(screenWidth, screenHeight);
            gameState = GameState.PLAYING;
        } else {
            gameState = GameState.GAME_OVER;
        }
    }

    private void handleResize(int screenWidth, int screenHeight) {
        if (lastWidth <= 0 || lastHeight <= 0) {
            lastWidth = screenWidth; lastHeight = screenHeight;
            return;
        }
        double scaleX = (double) screenWidth / lastWidth;
        double scaleY = (double) screenHeight / lastHeight;
        if (level != null) level.repositionBricks(screenWidth, screenHeight, false);
        if (paddle != null) {
            int newX = (int) (paddle.getX() * scaleX);
            int paddleW = screenWidth / 8 + bonusWidth;
            int paddleH = Math.max(10, (int)(screenHeight * 0.025));
            paddle.setX(newX);
            paddle.setSize(paddleW, paddleH);
            paddle.setY((int)(screenHeight * 0.8));
            if (paddle.getX() < 0) paddle.setX(0);
            if (paddle.getX() + paddle.getWidth() > screenWidth) paddle.setX(screenWidth - paddle.getWidth());
        }
        for (Ball b : balls) b.setPosition(b.getX() * scaleX, b.getY() * scaleY);
        for (PowerUp p : fallingPowerUps) p.setPosition((int)(p.getX() * scaleX), (int)(p.getY() * scaleY));
        lastWidth = screenWidth; lastHeight = screenHeight;
    }

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
                // NHÂN 3 SỐ BÓNG HIỆN TẠI
                List<Ball> currentBalls = new java.util.ArrayList<>(balls);
                for (Ball b : currentBalls) {
                    // Tạo quả bóng mới thứ 1: nảy sang trái 30 độ so với hướng cũ
                    Ball b1 = new Ball(b.getX(), b.getY(), b.getSize(), b.getSpeed());
                    b1.setDirection(90 + 30); // Bay chéo trái
                    balls.add(b1);
                    
                    // Tạo quả bóng mới thứ 2: nảy sang phải 30 độ so với hướng cũ
                    Ball b2 = new Ball(b.getX(), b.getY(), b.getSize(), b.getSpeed());
                    b2.setDirection(90 - 30); // Bay chéo phải
                    balls.add(b2);
                }
                break;
            case EXPAND: paddle.expand(screenWidth); break;
            case SHIELD: paddle.activateShield(20); break;
        }
    }

    public void resume() { if (gameState == GameState.PAUSED) gameState = GameState.PLAYING; }
    public List<Ball> getBalls() { return balls; }
    public List<PowerUp> getFallingPowerUps() { return fallingPowerUps; }
    public Paddle getPaddle() { return paddle; }
    public Level getLevel() { return level; }
    public GameState getGameState() { return gameState; }
    public int getScore() { return score; }
    public int getLives() { return lives; }
}
